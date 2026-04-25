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
