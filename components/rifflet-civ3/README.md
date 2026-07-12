# rifflet-civ3

Kotlin Multiplatform library for parsing Civilization III scenario and save files (`.BIC`, `.BIX`, `.BIQ`). Targets JVM; no platform-specific dependencies. Depends on `rifflet-core` for the shared `Chunk`/`ChunkId`/`RiffletParseException` primitives.

Civ3 files use a flat, non-nested chunk-like structure: a 4-byte file magic (`BIC ` for vanilla Civ3, `BICX` for PTW/Conquests), a `VER#` version header, then a sequence of marker-tagged sections (`BLDG`, `RACE`, `TECH`, `TERR`, ...), each framed as a 4-byte marker, a little-endian 4-byte item count, and that many length-prefixed items. A section may be omitted entirely, meaning "use the engine default for that category."

Files may optionally be compressed with PKWare Data Compression Library ("Implode"). Compression is auto-detected and transparently decompressed during parsing.

**This library is parse-only** — there is no encoding API, and only the `VER#` header is modeled so far. Every other section is preserved as `Civ3RawSection`.

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

Sections are returned in file order. Use `filterIsInstance` to narrow to a specific type once modeled types exist; today every section is a `Civ3RawSection`:

```kotlin
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.civ3.Civ3RawSection

val techSection = file.sections.filterIsInstance<Civ3RawSection>().firstOrNull { it.chunkId == ChunkId("TECH") }
```

## Error handling

`Civ3RootParser.parse` throws `RiffletParseException` (unchecked) when:

- Neither the file's leading bytes nor its PKWare-Implode-decompressed bytes match the `BIC `/`BICX` magic.
- The `VER#` header is malformed (wrong marker, header count other than one, unexpected header length).
- The binary data is truncated mid-section or mid-item.
- The PKWare Implode bitstream is malformed (bad header flags, an invalid Huffman code, or a copy distance pointing before the start of the decompressed output).
