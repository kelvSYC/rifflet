# rifflet-core

Kotlin Multiplatform library for parsing IFF (Interchange File Format) files. Targets JVM; no platform-specific dependencies.

## IFF primer

An IFF file contains a single root **group chunk** — `FORM`, `LIST`, or `CAT ` — which frames all other data. Every chunk has a 4-byte type ID and a 4-byte big-endian size, followed by exactly that many bytes of body (plus a pad byte when the size is odd).

| Chunk type | Structure |
|---|---|
| `FORM` | 4-byte content-type field, then zero or more local or group chunks |
| `LIST` | 4-byte list-type field, optional `PROP` chunks, then group chunk items |
| `CAT ` | 4-byte hint field, optional `PROP` chunks, then group chunk items |
| `PROP` | Only valid inside `LIST`/`CAT `. Supplies default sub-chunks to `FORM` chunks of a matching content type within the enclosing group. |
| `    ` | Blank filler chunk. Silently discarded wherever it appears. |

Variant IDs `FOR1`–`FOR9`, `LIS1`–`LIS9`, and `CAT1`–`CAT9` have the same binary structure as their canonical counterparts. The spec defines them as **local** forms — intended for nesting inside a `LIST` or `CAT ` rather than appearing at the file root. A `FORM SMPL` is a self-contained object; a `FOR1 SMPL` signals that the same content is a subordinate sub-object of its enclosing group.

## Parsing

### Overview

Parsing is a two-layer pipeline:

1. **`IffRootParser`** reads the raw bytes, walks the chunk tree, and dispatches to user-registered parsers.
2. **`IffParserCore`** holds the registry of those parsers, keyed by the inner content-type (or hint) field.

Three parser interfaces map to the three group chunk types:

- `FormChunkParser<T>` — receives the FORM's sub-chunks as a `ListMultimap<ChunkId, Any>`.
- `ListChunkParser<T>` — receives the LIST's items as a `List<Any>` and the resolved PROP properties.
- `CatChunkParser<T>` — receives the CAT's items as a `List<Any>` and the resolved PROP properties.

Sub-chunks whose type has no registered parser are left as their raw `IffChunk` representation in the map or list handed to the parent parser. Chunks that are parsed become the registered parser's return type.

### Worked example

The hypothetical **SMPL** format is a `FORM SMPL` containing two local chunks:

- `NAME` — UTF-8 string, the sample name
- `RATE` — 4-byte big-endian unsigned integer, the sample rate in Hz

```kotlin
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.IffRootParser
import okio.Buffer

data class Sample(val name: String, val rate: UInt)

val smplParser: IffRootParser<Sample> = IffRootParser.newParser {
    root = IffRootParser.Root.FormRoot(ChunkId("SMPL"))
    core {
        addLocalParser(ChunkId("NAME")) { data ->
            data.utf8()
        }
        addLocalParser(ChunkId("RATE")) { data ->
            Buffer().write(data).readInt().toUInt()
        }
        addFormParser(ChunkId("SMPL")) { chunks ->
            Sample(
                name = chunks[ChunkId("NAME")].filterIsInstance<String>().first(),
                rate = chunks[ChunkId("RATE")].filterIsInstance<UInt>().first(),
            )
        }
    }
}

val sample: Sample = smplParser.parse(source)
```

The `source` argument is an `okio.Source`. The parser reads exactly one root chunk from it.

### LIST and CAT parsers

`addListParser` and `addCatParser` work the same way as `addFormParser`. Their assembler lambdas receive a `List<Any>` of the parsed group chunk items in document order.

```kotlin
addListParser(ChunkId("SMPL")) { items ->
    items.filterIsInstance<Sample>()
}
```

For a `LIST`, the `PROP` sub-chunks are resolved and merged into each matching `FORM` before the form parser's assembler runs — you do not need to handle them manually.

### Nested parsers

All parsers registered in the same `IffParserCore` share that core for recursive dispatch. This means registering a `FormChunkParser` and a `ListChunkParser` in the same core is sufficient for the list parser to receive already-parsed domain objects — no separate wiring is needed.

Building on the `SMPL` example, an album format (`LIST ALBM`) containing multiple `FORM SMPL` items:

```kotlin
data class Album(val tracks: List<Sample>)

val albumParser: IffRootParser<Album> = IffRootParser.newParser {
    root = IffRootParser.Root.ListRoot(ChunkId("ALBM"))
    core {
        // Local parsers are shared across all group parsers in this core.
        addLocalParser(ChunkId("NAME")) { data -> data.utf8() }
        addLocalParser(ChunkId("RATE")) { data -> Buffer().write(data).readInt().toUInt() }

        // Each FORM SMPL item in the list is parsed by this before the list assembler runs.
        addFormParser(ChunkId("SMPL")) { chunks ->
            Sample(
                name = chunks[ChunkId("NAME")].filterIsInstance<String>().first(),
                rate = chunks[ChunkId("RATE")].filterIsInstance<UInt>().first(),
            )
        }

        // By the time this assembler is called, each item is already a Sample.
        addListParser(ChunkId("ALBM")) { items ->
            Album(tracks = items.filterIsInstance<Sample>())
        }
    }
}
```

Items whose content type has no registered parser are passed through as their raw `IffChunk` subtype. `filterIsInstance` is therefore the idiomatic way to extract expected types from the items list.

### Private cores

When two FORM types share a local chunk ID but interpret its bytes differently, a single `IffParserCore` cannot hold both parsers — the second registration would silently shadow the first. The solution is to give each FORM its own private core, constructed with `IffParserCore.newCore`, and supply it directly via the `addFormParser(type, parser)` overload that accepts a pre-built `FormChunkParser`.

`FormParser` is the concrete implementation used by the lambda overload. Constructing it explicitly allows you to pass a private core while keeping the same assembler pattern:

```kotlin
// FORM SMPL: NAME = UTF-8 string, DATA = raw PCM bytes
// FORM MIDI: NAME = UTF-8 string, DATA = 4-byte big-endian tempo (BPM)
// Both carry a DATA chunk, but with incompatible layouts.

data class Sample(val name: String, val data: ByteString)
data class MidiTrack(val name: String, val tempo: UInt)
data class Pack(val samples: List<Sample>, val midiTracks: List<MidiTrack>)

val smplCore = IffParserCore.newCore {
    addLocalParser(ChunkId("NAME")) { data -> data.utf8() }
    addLocalParser(ChunkId("DATA")) { data -> data }
}

val midiCore = IffParserCore.newCore {
    addLocalParser(ChunkId("NAME")) { data -> data.utf8() }
    addLocalParser(ChunkId("DATA")) { data -> Buffer().write(data).readInt().toUInt() }
}

val packParser: IffRootParser<Pack> = IffRootParser.newParser {
    root = IffRootParser.Root.CatRoot(ChunkId("PACK"))
    core {
        addFormParser(ChunkId("SMPL"), FormParser(smplCore) { chunks ->
            Sample(
                name = chunks[ChunkId("NAME")].filterIsInstance<String>().first(),
                data = chunks[ChunkId("DATA")].filterIsInstance<ByteString>().first(),
            )
        })
        addFormParser(ChunkId("MIDI"), FormParser(midiCore) { chunks ->
            MidiTrack(
                name = chunks[ChunkId("NAME")].filterIsInstance<String>().first(),
                tempo = chunks[ChunkId("DATA")].filterIsInstance<UInt>().first(),
            )
        })
        addCatParser(ChunkId("PACK")) { items ->
            Pack(
                samples = items.filterIsInstance<Sample>(),
                midiTracks = items.filterIsInstance<MidiTrack>(),
            )
        }
    }
}
```

The outer `CAT PACK` core knows nothing about `NAME` or `DATA`; those are entirely private to each FORM's core. Nested group chunks within `FORM SMPL` or `FORM MIDI` are dispatched through their respective private cores, not the outer one.

### PROP properties

`PROP` chunks inside a `LIST` or `CAT ` supply default sub-chunks for `FORM` chunks of a specific content type. When the `FormChunkParser` assembler is called, its `chunks` map already contains the merged result: FORM-local sub-chunks take precedence over PROP defaults, and PROP defaults fill in for keys absent from the FORM.

### Variant chunks

`FOR1`–`FOR9` (and their `LIST`/`CAT ` equivalents) are parsed using the same code path as their canonical counterparts because their binary layout is identical. When encountered as a nested chunk, a `FOR1 SMPL` will be dispatched to the same `FormChunkParser` registered for content type `SMPL` as a `FORM SMPL` would be — this is a convenience of the dispatch mechanism, not a spec requirement that the two be treated as equivalent.

**`IffParserCore` does not support variant-aware dispatch.** Neither `FormChunkParser`, `ListChunkParser`, nor `CatChunkParser` — whether implemented directly or supplied as an assembler lambda — receives the outer chunk ID. Once a chunk tree enters the core, the distinction between `FORM SMPL` and `FOR1 SMPL` is invisible to every registered parser. A format that assigns different semantics to `FORM ABCD` and `FOR1 ABCD` cannot be correctly handled within a single `IffParserCore`; variant-aware logic must be implemented above the core, by inspecting `FormChunk.outerChunkId` on the raw chunk tree before dispatch.

To declare a root that carries a variant ID, set `variantId` explicitly. The parser will then reject a canonical `FORM` root in its place:

```kotlin
root = IffRootParser.Root.FormRoot(
    type = ChunkId("SMPL"),
    variantId = ChunkId("FOR1"),
)
```

### Parser leniency

The parser is intentionally strict in most respects but departs from the spec in two areas:

**Variant IDs at root.** The spec defines `FOR1`–`FOR9`, `LIS1`–`LIS9`, and `CAT1`–`CAT9` as local variants not intended to appear at the file root. `IffRootParser` permits them at root when `variantId` is set explicitly — this is an opt-in escape hatch, not an endorsement of the practice.

**LIST item homogeneity.** The spec states that all group chunks within a `LIST` should be of the same content type, matching the list-type field. The parser does not enforce this; the list-type field is treated as advisory. A `LIST` may contain group chunks of mixed content types without error.

### Error handling

`IffRootParser.parse` throws `RiffletParseException` (unchecked) when:

- The root chunk kind or content type does not match the declared `Root`.
- No parser is registered for the root content type.
- The binary data violates the IFF structure rules (e.g. `PROP` after a group item, a group chunk inside a `PROP`, a duplicate `PROP` for the same form type, or `PROP` inside a `FORM`).

## Encoding

### Overview

Encoding mirrors the parsing pipeline in reverse:

1. **`IffRootEncoder`** frames the root group chunk and delegates to a body encoder.
2. **`IffEncoderCore`** holds the registry of body encoders, keyed by inner type or hint field.

Three body encoder interfaces map to the three group chunk types:

- `FormBodyEncoder<T>` — receives a domain object and writes its child local and group chunks.
- `ListBodyEncoder<T>` — receives a domain object and writes its child group chunks in order.
- `CatBodyEncoder<T>` — same as `ListBodyEncoder`, for `CAT ` chunks.

Body encoders are not `ChunkEncoder` instances — they write only the body content, not the outer framing. The outer header (type ID, size, and for group chunks the inner type field) is written by the caller.

The values produced by a disassembler should be domain objects — strings, data classes, or other application types — not `IffChunk` subtypes. The encoder operates entirely at the domain level; constructing `LocalChunk`, `FormChunk`, or any other chunk representation manually in a disassembler bypasses the encoding pipeline and is not a supported use case. The only legitimate exception is preserving the raw body bytes of a chunk whose type the application does not model, as described in [Round-trip limitations](#round-trip-limitations).

### Worked example

Using the same hypothetical **SMPL** format as the parsing section:

```kotlin
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.IffRootEncoder

data class Sample(val name: String, val rate: UInt)

val smplEncoder: IffRootEncoder<Sample> = IffRootEncoder.newEncoder {
    root = IffRootEncoder.Root.FormRoot(ChunkId("SMPL"))
    encoder(FormBodyEncoder(IffEncoderCore.newCore {
        addLocalEncoder(ChunkId("NAME")) { value: String, dest -> dest.writeUtf8(value) }
        addLocalEncoder(ChunkId("RATE")) { value: UInt, dest -> dest.writeInt(value.toInt()) }
    }) { sample ->
        listMultimapOf(
            ChunkId("NAME") to sample.name,
            ChunkId("RATE") to sample.rate,
        )
    })
}

smplEncoder.encode(sample, sink)
```

The core supplied to `FormBodyEncoder` only needs to know how to encode the child chunks (`NAME` and `RATE`). The root encoder calls the `FormBodyEncoder` directly — it does not look up `SMPL` in any core — so there is no need to register the `SMPL` disassembler in the core.

The disassembler lambda returns a `ListMultimap<ChunkId, Any>` — the multimap preserves insertion order, which becomes the chunk order in the encoded output. Each `(typeId, value)` pair is dispatched through the core's registered encoders.

The `sink` argument is an `okio.Sink`. The encoder writes exactly one root chunk to it.

### LIST and CAT encoders

`addListEncoder` and `addCatEncoder` work the same way as `addFormEncoder`. Their disassembler lambdas return a `List<Pair<ChunkId, Any>>` rather than a multimap, because `LIST` and `CAT ` contain only group chunks (no local chunks):

```kotlin
addListEncoder(ChunkId("ALBM")) { album: Album ->
    album.tracks.map { ChunkId("SMPL") to it as Any }
}
```

For the common case of a homogeneous sequence, `ListBodyEncoder.uniform` and `CatBodyEncoder.uniform` avoid the need to build a core manually:

```kotlin
addListEncoder(
    ChunkId("ALBM"),
    ListBodyEncoder.uniform(ChunkId("SMPL"), smplFormBodyEncoder),
)
```

### PROP chunks

`LIST` and `CAT ` chunks may include `PROP` chunks to supply shared default local chunks for enclosed `FORM` chunks of a given content type. Use `ListBodyEncoder.withProperties` (or `CatBodyEncoder.withProperties`, or the three-argument `addListEncoder`/`addCatEncoder` overloads) to opt in.

`withProperties` takes a second lambda, `propertiesDisassembler`, that returns a `Map<ChunkId, Any>` keyed by form-type. For each entry, a `PROP` chunk is written — using the `PropBodyEncoder` registered for that form-type in `core.propEncoders` — before any group chunks, as required by the IFF spec.

`PropBodyEncoder` encodes only local chunks and is registered in the core via `addPropEncoder`. It is otherwise identical in shape to `FormBodyEncoder`.

Building on the album example: if all `FORM SMPL` items share the same sample rate, it can be factored into a `PROP`:

```kotlin
data class Album(val sharedRate: UInt, val tracks: List<Sample>)

val core = IffEncoderCore.newCore {
    addLocalEncoder(ChunkId("NAME")) { value: String, dest -> dest.writeUtf8(value) }
    addLocalEncoder(ChunkId("RATE")) { value: UInt, dest -> dest.writeInt(value.toInt()) }
    addFormEncoder(ChunkId("SMPL")) { sample: Sample ->
        // RATE is omitted here; it will be supplied by the PROP default.
        listMultimapOf(ChunkId("NAME") to sample.name)
    }
    addPropEncoder(ChunkId("SMPL")) { album: Album ->
        // Encodes RATE as a shared property for all FORM SMPL items.
        listMultimapOf(ChunkId("RATE") to album.sharedRate)
    }
}

val albumEncoder: IffRootEncoder<Album> = IffRootEncoder.newEncoder {
    root = IffRootEncoder.Root.ListRoot(ChunkId("ALBM"))
    encoder(ListBodyEncoder.withProperties(
        core,
        propertiesDisassembler = { album -> mapOf(ChunkId("SMPL") to album) },
        disassembler = { album -> album.tracks.map { ChunkId("SMPL") to it as Any } },
    ))
}
```

**Format grammar is the caller's responsibility.** The IFF spec defines the mechanism — `PROP` supplies defaults for `FORM` chunks of a matching type within the enclosing group — but does not dictate which chunk types a given format places in a `PROP` versus in each individual `FORM`. Callers must respect the grammar of the specific IFF-based format being written.

### Cross-type dispatch

When a group chunk contains children of a different group chunk type — for example, a `LIST ALBM` whose items are `FORM SMPL` chunks — the list disassembler must be able to reach the `SMPL` form encoder. Because each body encoder dispatches only through its own `IffEncoderCore`, both encoders must share the same core instance.

The lambda overloads (`addFormEncoder(type) { disassembler }`, `addListEncoder`, `addCatEncoder`) wire the created encoder to the core being built, which makes this possible: register all participating encoders in a single `newCore` block using the lambda overloads, then pass that core to any body encoders constructed directly.

Pre-built encoders supplied via the direct overloads (`addFormEncoder(type, preBuiltEncoder)`) bring their own private core; registrations in the outer core are not visible to them and they cannot participate in shared dispatch.

Building on the `SMPL` example, an album format (`LIST ALBM`) containing multiple `FORM SMPL` items:

```kotlin
data class Album(val tracks: List<Sample>)

val core = IffEncoderCore.newCore {
    addLocalEncoder(ChunkId("NAME")) { value: String, dest -> dest.writeUtf8(value) }
    addLocalEncoder(ChunkId("RATE")) { value: UInt, dest -> dest.writeInt(value.toInt()) }
    // Wired to `core` via lambda overload, so NAME and RATE dispatch works.
    addFormEncoder(ChunkId("SMPL")) { sample: Sample ->
        listMultimapOf(
            ChunkId("NAME") to sample.name,
            ChunkId("RATE") to sample.rate,
        )
    }
}

val albumEncoder: IffRootEncoder<Album> = IffRootEncoder.newEncoder {
    root = IffRootEncoder.Root.ListRoot(ChunkId("ALBM"))
    encoder(ListBodyEncoder(core) { album ->
        album.tracks.map { ChunkId("SMPL") to it as Any }
    })
}
```

The `ListBodyEncoder` is constructed with `core` directly and finds the `SMPL` form encoder there when the disassembler emits it.

### Error handling

`IffRootEncoder.encode` throws `RiffletEncodeException` (unchecked) when:

- No encoder is registered in the core for a chunk type produced by a disassembler.
- The encoder kind supplied to the builder does not match the declared `Root` kind.

### Round-trip limitations

The encoder and parser do not form a lossless round-trip. The following data is preserved by the parser but has no encoding counterpart:

**Unparsed local chunks.** When the parser encounters a local chunk with no registered `LocalChunkParser`, it retains the raw bytes as a `LocalChunk` in the parsed chunk tree. The encoder has no corresponding passthrough — every value produced by a disassembler must correspond to a registered encoder. To preserve unparsed chunks across a round-trip, the domain type must explicitly store the raw data (e.g. as a `ByteString`) and the disassembler must return it under the correct chunk ID with a registered raw-byte encoder:

```kotlin
// Domain type explicitly carries the raw body of an unrecognised chunk.
data class Sample(val name: String, val rate: UInt, val unknownChunks: List<Pair<ChunkId, ByteString>>)

// Encoder side: register a raw-byte encoder for each unrecognised chunk ID encountered.
sample.unknownChunks.forEach { (id, _) ->
    addLocalEncoder(id) { value: ByteString, dest -> dest.write(value) }
}
```

Because `ChunkEncoder` is not a functional interface, the lambda overload `addLocalEncoder(type) { value, dest -> ... }` is the only way to register an encoder without providing a full `ChunkEncoder` implementation.

**PROP chunks.** `PROP` chunks inside a `LIST` or `CAT ` are parsed and their sub-chunks are merged into the relevant `FORM` assemblers before the form parser's assembler runs. The encoder supports writing `PROP` chunks via `withProperties`, but there is no automatic extraction: the caller must decide which chunks to factor into a `PROP` and supply a matching `PropBodyEncoder`. A round-trip that does not use `withProperties` will inline all sub-chunks directly into each `FORM` — the structural sharing is lost, and the output may be larger.

**Variant outer IDs.** The parser accepts `FOR1`–`FOR9`, `LIS1`–`LIS9`, and `CAT1`–`CAT9` and preserves the original outer ID in `FormChunk.outerChunkId`, `ListChunk.outerChunkId`, and `CatChunk.outerChunkId`. The encoder always writes the canonical outer ID (`FORM`, `LIST`, or `CAT `). There is no mechanism to select a variant.

**Blank chunks.** Blank (`    `) chunks are silently discarded during parsing and cannot be produced by the encoder.
