# rifflet-civ3

Kotlin Multiplatform library for parsing Civilization III scenario and save files (`.BIC`, `.BIX`, `.BIQ`). Targets JVM; no platform-specific dependencies. Depends on `rifflet-core` for the shared `Chunk`/`ChunkId`/`RiffletParseException` primitives.

Civ3 files use a flat, non-nested chunk-like structure: a 4-byte file magic (`BIC ` for vanilla Civ3, `BICX` for PTW/Conquests), a `VER#` version header, then a sequence of marker-tagged sections (`BLDG`, `RACE`, `TECH`, `TERR`, ...), each framed as a 4-byte marker, a little-endian 4-byte item count, and that many length-prefixed items. A section may be omitted entirely, meaning "use the engine default for that category."

Files may optionally be compressed with PKWare Data Compression Library ("Implode"). Compression is auto-detected and transparently decompressed during parsing.

**This library is parse-only** — there is no encoding API yet. All 27 section markers Civ3 defines are modeled with a dedicated `*Section`/`*Entry` type (`BldgSection`/`BldgEntry`, `TerrSection`/`TerrEntry`, and so on); a marker outside that set falls back to `Civ3RawSection`.

## Parsing

```kotlin
import com.kelvsyc.rifflet.civ3.Civ3RootParser
import okio.FileSystem
import okio.Path.Companion.toPath

val file = FileSystem.SYSTEM.source("scenario.biq".toPath()).use { Civ3RootParser.parse(it) }

println("Civ3 format version ${file.header.major}.${file.header.minor}: ${file.header.title}")
```

`Civ3RootParser.parse` throws `RiffletParseException` when the source is not a well-formed Civ3 file, compressed or not.

## Accessing sections

Sections are returned in file order. Use `filterIsInstance` to narrow to a specific modeled type:

```kotlin
import com.kelvsyc.rifflet.civ3.BldgSection

val buildings = file.sections.filterIsInstance<BldgSection>().singleOrNull()?.entries.orEmpty()
```

A marker with no dedicated type (none, currently — every Civ3 section marker is modeled) would appear as `Civ3RawSection`, preserving its raw length-prefixed items.

## Error handling

`Civ3RootParser.parse` throws `RiffletParseException` (unchecked) when:

- Neither the file's leading bytes nor its PKWare-Implode-decompressed bytes match the `BIC `/`BICX` magic.
- The `VER#` header is malformed (wrong marker, header count other than one, unexpected header length).
- The binary data is truncated mid-section or mid-item.
- The PKWare Implode bitstream is malformed (bad header flags, an invalid Huffman code, or a copy distance pointing before the start of the decompressed output).
- A modeled entry's own byte-length invariants aren't met (e.g. a fixed-size field with the wrong number of bytes for its declared count).

A `RiffletParseException` means the bytes couldn't be turned into a `Civ3File` at all — there is no partial result to inspect. This is different in kind from what the validation layer (below) reports: parsing only checks whether the file's *shape* obeys the wire format, never whether its *contents* make sense as a Civ3 scenario. A file can parse perfectly cleanly and still describe something the real editors would never let you build — a building whose required government index points nowhere, for instance.

## Validation

`Civ3File.validate(): List<ValidationIssue>` checks a successfully parsed file against constraints already confirmed against the real Civ3 map editors — behavior a parse can't see, because it's about content, not shape. Unlike `Civ3RootParser.parse`, `validate()` never throws: a `Civ3File` you already hold is never invalidated by calling it, and the returned list is simply empty when nothing looks wrong.

```kotlin
import com.kelvsyc.rifflet.civ3.validate

val issues = file.validate()
issues.forEach { println("${it.section.name}[${it.index}].${it.field}: ${it.message}") }
```

Validation is opt-in and deliberately separate from parsing:

- **Parsing** (`RiffletParseException`, thrown) asks *can these bytes become a `Civ3File` at all?* — a malformed or truncated file has no valid in-memory representation to hand back, so it must throw.
- **Encoding** (not yet implemented; would throw `RiffletEncodeException`, mirroring `rifflet-core`'s IFF/RIFF/RIFX encoders) would ask the same question in reverse: *can this in-memory value become valid bytes?* Both are checks on the wire format's own structural rules, just at opposite boundaries.
- **Validation** (`ValidationIssue`, returned) asks *does this value describe something the real editor would actually produce?* The bytes are perfectly well-formed either direction; the issue is with the scenario's content, not the file's shape. That's an opinion a caller may want to see without ever being blocked by it — hence a list, not an exception.

The `ValidationRule` contract covers era-dependent rules, bit/enum validity, and cross-entry or whole-file constraints (section cardinality, required section sets, and so on) without changing shape.

Every issue also carries a [`ValidationSeverity`](src/commonMain/kotlin/com/kelvsyc/rifflet/civ3/validation/ValidationIssue.kt): `ERROR` means every real official file matches the constraint without exception, `WARNING` means the constraint holds in general but at least one real official file is a confirmed exception. Neither implies anything about whether the game engine would tolerate a violation at runtime — that's a separate, generally unconfirmed question outside what this library checks.
