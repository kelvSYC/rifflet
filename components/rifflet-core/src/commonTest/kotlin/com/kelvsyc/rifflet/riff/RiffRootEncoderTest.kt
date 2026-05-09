package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.listMultimapOf
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun id(name: String) = ChunkId(name)

/** Writes a bare RIFF chunk: typeId (4 bytes) + LE size (4 bytes) + body + optional pad. */
private fun riffChunk(type: String, body: ByteArray = byteArrayOf()): ByteString {
    val buf = Buffer().apply {
        writeString(type, Charsets.ISO_8859_1)
        writeIntLe(body.size)
        write(body)
        if (body.size % 2 != 0) writeByte(0)
    }
    return buf.readByteString()
}

/** Writes a group chunk: outerType + LE size + innerType + body + optional pad. */
private fun groupChunk(outerType: String, innerType: String, body: ByteArray = byteArrayOf()): ByteString {
    val inner = Buffer().apply {
        writeString(innerType, Charsets.ISO_8859_1)
        write(body)
    }
    return riffChunk(outerType, inner.readByteArray())
}

class RiffRootEncoderTest : FunSpec({

    context("RiffFormBodyEncoder") {

        test("encodes a local child chunk") {
            val core = RiffEncoderCore.newCore {
                addLocalEncoder<String>(id("INAM")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = RiffFormBodyEncoder(core) { s: String ->
                listMultimapOf(id("INAM") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            dest.readByteString() shouldBe riffChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes a nested LIST child") {
            val core = RiffEncoderCore.newCore {
                addListEncoder(id("INFO")) { _: Unit -> listMultimapOf() }
            }
            val encoder = RiffFormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("INFO") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("LIST", "INFO")
        }

        test("encodes multiple children in insertion order") {
            val core = RiffEncoderCore.newCore {
                addLocalEncoder<UInt>(id("RATE")) { value, dest -> dest.writeIntLe(value.toInt()) }
                addLocalEncoder<UInt>(id("BITS")) { value, dest -> dest.writeIntLe(value.toInt()) }
            }
            val encoder = RiffFormBodyEncoder(core) { pair: Pair<UInt, UInt> ->
                listMultimapOf(
                    id("RATE") to pair.first as Any,
                    id("BITS") to pair.second as Any,
                )
            }
            val dest = Buffer()
            encoder.encode(44100u to 16u, dest)
            val expected = Buffer().apply {
                write(riffChunk("RATE", Buffer().apply { writeIntLe(44100) }.readByteArray()))
                write(riffChunk("BITS", Buffer().apply { writeIntLe(16) }.readByteArray()))
            }.readByteString()
            dest.readByteString() shouldBe expected
        }

        test("throws when no encoder is registered for the chunk type") {
            val core = RiffEncoderCore.newCore {}
            val encoder = RiffFormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("fmt ") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("RiffListBodyEncoder") {

        test("encodes a local child chunk") {
            val core = RiffEncoderCore.newCore {
                addLocalEncoder<String>(id("INAM")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = RiffListBodyEncoder(core) { s: String ->
                listMultimapOf(id("INAM") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            dest.readByteString() shouldBe riffChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes a nested LIST child") {
            val core = RiffEncoderCore.newCore {
                addListEncoder(id("adtl")) { _: Unit -> listMultimapOf() }
            }
            val encoder = RiffListBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("adtl") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("LIST", "adtl")
        }

        test("throws when no encoder is registered for the chunk type") {
            val core = RiffEncoderCore.newCore {}
            val encoder = RiffListBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("INAM") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("RiffRootEncoder") {

        test("encodes a WAVE form with a local child chunk") {
            val encoder = RiffRootEncoder.newEncoder<String> {
                root = RiffRootEncoder.Root(id("WAVE"))
                encoder(RiffFormBodyEncoder(RiffEncoderCore.newCore {
                    addLocalEncoder<String>(id("INAM")) { value, dest ->
                        dest.writeString(value, Charsets.ISO_8859_1)
                    }
                }) { s: String -> listMultimapOf(id("INAM") to s as Any) })
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            val expectedBody = riffChunk("INAM", "test".toByteArray(Charsets.ISO_8859_1))
            dest.readByteString() shouldBe groupChunk("RIFF", "WAVE", expectedBody.toByteArray())
        }

        test("encodes an empty WAVE form") {
            val encoder = RiffRootEncoder.newEncoder<Unit> {
                root = RiffRootEncoder.Root(id("WAVE"))
                encoder(RiffFormBodyEncoder(RiffEncoderCore.newCore {}) { _: Unit -> listMultimapOf() })
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("RIFF", "WAVE")
        }
    }
})
