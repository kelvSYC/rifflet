package com.kelvsyc.rifflet.rifx

import com.kelvsyc.kotlin.core.collections.listMultimapOf
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun id(name: String) = ChunkId(name)

/** Writes a bare RIFX chunk: typeId (4 bytes) + BE size (4 bytes) + body + optional pad. */
private fun rifxChunk(type: String, body: ByteArray = byteArrayOf()): ByteString {
    val buf = Buffer().apply {
        writeString(type, Charsets.ISO_8859_1)
        writeInt(body.size)
        write(body)
        if (body.size % 2 != 0) writeByte(0)
    }
    return buf.readByteString()
}

/** Writes a group chunk: outerType + BE size + innerType + body + optional pad. */
private fun groupChunk(outerType: String, innerType: String, body: ByteArray = byteArrayOf()): ByteString {
    val inner = Buffer().apply {
        writeString(innerType, Charsets.ISO_8859_1)
        write(body)
    }
    return rifxChunk(outerType, inner.readByteArray())
}

class RifxRootEncoderTest : FunSpec({

    context("RifxFormBodyEncoder") {

        test("encodes a local child chunk") {
            val core = RifxEncoderCore.newCore {
                addLocalEncoder<String>(id("INAM")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = RifxFormBodyEncoder(core) { s: String ->
                listMultimapOf(id("INAM") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            dest.readByteString() shouldBe rifxChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes a nested LIST child") {
            val core = RifxEncoderCore.newCore {
                addListEncoder(id("INFO")) { _: Unit -> listMultimapOf() }
            }
            val encoder = RifxFormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("INFO") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("LIST", "INFO")
        }

        test("encodes multiple children in insertion order") {
            val core = RifxEncoderCore.newCore {
                addLocalEncoder<UInt>(id("RATE")) { value, dest -> dest.writeInt(value.toInt()) }
                addLocalEncoder<UInt>(id("BITS")) { value, dest -> dest.writeInt(value.toInt()) }
            }
            val encoder = RifxFormBodyEncoder(core) { pair: Pair<UInt, UInt> ->
                listMultimapOf(
                    id("RATE") to pair.first as Any,
                    id("BITS") to pair.second as Any,
                )
            }
            val dest = Buffer()
            encoder.encode(44100u to 16u, dest)
            val expected = Buffer().apply {
                write(rifxChunk("RATE", Buffer().apply { writeInt(44100) }.readByteArray()))
                write(rifxChunk("BITS", Buffer().apply { writeInt(16) }.readByteArray()))
            }.readByteString()
            dest.readByteString() shouldBe expected
        }

        test("throws when no encoder is registered for the chunk type") {
            val core = RifxEncoderCore.newCore {}
            val encoder = RifxFormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("fmt ") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("RifxListBodyEncoder") {

        test("encodes a local child chunk") {
            val core = RifxEncoderCore.newCore {
                addLocalEncoder<String>(id("INAM")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = RifxListBodyEncoder(core) { s: String ->
                listMultimapOf(id("INAM") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            dest.readByteString() shouldBe rifxChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes a nested LIST child") {
            val core = RifxEncoderCore.newCore {
                addListEncoder(id("adtl")) { _: Unit -> listMultimapOf() }
            }
            val encoder = RifxListBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("adtl") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("LIST", "adtl")
        }

        test("throws when no encoder is registered for the chunk type") {
            val core = RifxEncoderCore.newCore {}
            val encoder = RifxListBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("INAM") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("RifxRootEncoder") {

        test("encodes a WAVE form with a local child chunk") {
            val encoder = RifxRootEncoder.newEncoder<String> {
                root = RifxRootEncoder.Root(id("WAVE"))
                encoder(RifxFormBodyEncoder(RifxEncoderCore.newCore {
                    addLocalEncoder<String>(id("INAM")) { value, dest ->
                        dest.writeString(value, Charsets.ISO_8859_1)
                    }
                }) { s: String -> listMultimapOf(id("INAM") to s as Any) })
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            val expectedBody = rifxChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
            dest.readByteString() shouldBe groupChunk("RIFX", "WAVE", expectedBody.toByteArray())
        }

        test("encodes an empty WAVE form") {
            val encoder = RifxRootEncoder.newEncoder<Unit> {
                root = RifxRootEncoder.Root(id("WAVE"))
                encoder(RifxFormBodyEncoder(RifxEncoderCore.newCore {}) { _: Unit -> listMultimapOf() })
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("RIFX", "WAVE")
        }

        test("size fields in the output are big-endian") {
            val encoder = RifxRootEncoder.newEncoder<Unit> {
                root = RifxRootEncoder.Root(id("WAVE"))
                encoder(RifxFormBodyEncoder(RifxEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("DATA")) { _, dest -> dest.write(ByteArray(4)) }
                }) { _: Unit -> listMultimapOf(id("DATA") to Unit as Any) })
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            // Skip "RIFX" FourCC (4 bytes), read 4-byte size as big-endian
            dest.skip(4)
            val size = dest.readInt() // big-endian read
            // Body = "WAVE"(4) + "DATA"(4) + size(4) + data(4) = 16 bytes
            size shouldBe 16
        }
    }

    context("round-trip") {
        test("encode then parse produces the original value") {
            val formType = id("WAVE")
            val encoder = RifxRootEncoder.newEncoder<String> {
                root = RifxRootEncoder.Root(formType)
                encoder(RifxFormBodyEncoder(RifxEncoderCore.newCore {
                    addLocalEncoder<String>(id("INAM")) { value, dest ->
                        dest.writeString(value, Charsets.ISO_8859_1)
                    }
                }) { s: String -> listMultimapOf(id("INAM") to s as Any) })
            }
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(formType)
                core {
                    addFormParser(formType) { chunks ->
                        val local = chunks[id("INAM")].single()
                        (local as com.kelvsyc.rifflet.riff.RiffLocalChunk).data.data.utf8()
                    }
                }
            }
            val dest = Buffer()
            encoder.encode("hello", dest)
            parser.parse(dest) shouldBe "hello"
        }
    }
})
