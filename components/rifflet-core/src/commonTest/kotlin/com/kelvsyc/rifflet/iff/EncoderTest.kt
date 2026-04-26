package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.listMultimapOf
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun id(name: String) = ChunkId(name)

/** Writes a bare IFF chunk: typeId (4 bytes) + big-endian size (4 bytes) + body + optional pad. */
private fun iffChunk(type: String, body: ByteArray = byteArrayOf()): ByteString {
    val buf = Buffer().apply {
        writeString(type, Charsets.ISO_8859_1)
        writeInt(body.size)
        write(body)
        if (body.size % 2 != 0) writeByte(0)
    }
    return buf.readByteString()
}

/** Writes a group chunk: outerType + size + innerType + body + optional pad. */
private fun groupChunk(outerType: String, innerType: String, body: ByteArray = byteArrayOf()): ByteString {
    val inner = Buffer().apply {
        writeString(innerType, Charsets.ISO_8859_1)
        write(body)
    }
    return iffChunk(outerType, inner.readByteArray())
}

/** Writes a PROP chunk: "PROP" + size + formType + local chunks. */
private fun propChunk(formType: String, vararg localChunks: ByteString): ByteString {
    val body = Buffer().apply {
        writeString(formType, Charsets.ISO_8859_1)
        localChunks.forEach { write(it) }
    }
    return iffChunk("PROP", body.readByteArray())
}

private val emptyShrdChunk = iffChunk("SHRD", byteArrayOf(0, 0, 0, 0))

class EncoderTest : FunSpec({

    context("FormBodyEncoder") {

        test("encodes a local child chunk") {
            val core = IffEncoderCore.newCore {
                addLocalEncoder<String>(id("NAME")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = FormBodyEncoder(core) { s: String ->
                listMultimapOf(id("NAME") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("test", dest)
            dest.readByteString() shouldBe iffChunk("NAME", "test".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes a nested FORM child") {
            val core = IffEncoderCore.newCore {
                addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
            }
            val encoder = FormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("SMPL") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("FORM", "SMPL")
        }

        test("encodes a nested LIST child") {
            val core = IffEncoderCore.newCore {
                addListEncoder(id("ALBM")) { _: Unit -> emptyList() }
            }
            val encoder = FormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("ALBM") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("LIST", "ALBM")
        }

        test("encodes a nested CAT child") {
            val core = IffEncoderCore.newCore {
                addCatEncoder(id("AIFF")) { _: Unit -> emptyList() }
            }
            val encoder = FormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("AIFF") to Unit as Any)
            }
            val dest = Buffer()
            encoder.encode(Unit, dest)
            dest.readByteString() shouldBe groupChunk("CAT ", "AIFF")
        }

        test("encodes multiple children in insertion order") {
            val core = IffEncoderCore.newCore {
                addLocalEncoder<String>(id("NAME")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
                addLocalEncoder<UInt>(id("RATE")) { value, dest ->
                    dest.writeInt(value.toInt())
                }
            }
            val encoder = FormBodyEncoder(core) { pair: Pair<String, UInt> ->
                listMultimapOf(
                    id("NAME") to pair.first as Any,
                    id("RATE") to pair.second as Any,
                )
            }
            val dest = Buffer()
            encoder.encode("test" to 44100u, dest)
            val rateBytes = Buffer().apply { writeInt(44100) }.readByteArray()
            val expected = Buffer().apply {
                write(iffChunk("NAME", "test".toByteArray(Charsets.ISO_8859_1)))
                write(iffChunk("RATE", rateBytes))
            }.readByteString()
            dest.readByteString() shouldBe expected
        }

        test("throws when no encoder is registered for the chunk type") {
            val core = IffEncoderCore.newCore {}
            val encoder = FormBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("NAME") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("ListBodyEncoder") {

        context("invoke") {

            test("encodes a FORM child") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder(core) { _: Unit -> listOf(id("SMPL") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("FORM", "SMPL")
            }

            test("encodes a LIST child") {
                val core = IffEncoderCore.newCore {
                    addListEncoder(id("ALBM")) { _: Unit -> emptyList() }
                }
                val encoder = ListBodyEncoder(core) { _: Unit -> listOf(id("ALBM") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("LIST", "ALBM")
            }

            test("encodes a CAT child") {
                val core = IffEncoderCore.newCore {
                    addCatEncoder(id("AIFF")) { _: Unit -> emptyList() }
                }
                val encoder = ListBodyEncoder(core) { _: Unit -> listOf(id("AIFF") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("CAT ", "AIFF")
            }

            test("encodes multiple items in order") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
                    addFormEncoder(id("MIDI")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder(core) { _: Unit ->
                    listOf(id("SMPL") to Unit, id("MIDI") to Unit, id("SMPL") to Unit)
                }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(groupChunk("FORM", "SMPL"))
                    write(groupChunk("FORM", "MIDI"))
                    write(groupChunk("FORM", "SMPL"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }

            test("throws when no group encoder is registered for the chunk type") {
                val core = IffEncoderCore.newCore {}
                val encoder = ListBodyEncoder(core) { _: Unit -> listOf(id("SMPL") to Unit) }
                shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
            }
        }

        context("withProperties") {

            test("writes PROP chunks before group chunks") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("TRAK")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addFormEncoder(id("TRAK")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { mapOf(id("TRAK") to Unit) },
                    disassembler = { listOf(id("TRAK") to Unit) },
                )
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(propChunk("TRAK", emptyShrdChunk))
                    write(groupChunk("FORM", "TRAK"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }

            test("writes no PROP chunks when properties disassembler returns empty map") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { emptyMap() },
                    disassembler = { listOf(id("SMPL") to Unit) },
                )
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("FORM", "SMPL")
            }

            test("writes multiple PROP chunks before group chunks") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("SMPL")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addPropEncoder(id("MIDI")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = {
                        linkedMapOf(id("SMPL") to Unit as Any, id("MIDI") to Unit as Any)
                    },
                    disassembler = { listOf(id("SMPL") to Unit) },
                )
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(propChunk("SMPL", emptyShrdChunk))
                    write(propChunk("MIDI", emptyShrdChunk))
                    write(groupChunk("FORM", "SMPL"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }

            test("throws when no PROP encoder is registered for the form type") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("TRAK")) { _: Unit -> listMultimapOf() }
                }
                val encoder = ListBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { mapOf(id("TRAK") to Unit) },
                    disassembler = { emptyList() },
                )
                shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
            }
        }
    }

    context("CatBodyEncoder") {

        context("invoke") {

            test("encodes a FORM child") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("AIFF")) { _: Unit -> listMultimapOf() }
                }
                val encoder = CatBodyEncoder(core) { _: Unit -> listOf(id("AIFF") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("FORM", "AIFF")
            }

            test("encodes a LIST child") {
                val core = IffEncoderCore.newCore {
                    addListEncoder(id("PACK")) { _: Unit -> emptyList() }
                }
                val encoder = CatBodyEncoder(core) { _: Unit -> listOf(id("PACK") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("LIST", "PACK")
            }

            test("encodes a CAT child") {
                val core = IffEncoderCore.newCore {
                    addCatEncoder(id("AIFF")) { _: Unit -> emptyList() }
                }
                val encoder = CatBodyEncoder(core) { _: Unit -> listOf(id("AIFF") to Unit) }
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("CAT ", "AIFF")
            }

            test("throws when no group encoder is registered for the chunk type") {
                val core = IffEncoderCore.newCore {}
                val encoder = CatBodyEncoder(core) { _: Unit -> listOf(id("AIFF") to Unit) }
                shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
            }
        }

        context("withProperties") {

            test("writes PROP chunks before group chunks") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("AIFF")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addFormEncoder(id("AIFF")) { _: Unit -> listMultimapOf() }
                }
                val encoder = CatBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { mapOf(id("AIFF") to Unit) },
                    disassembler = { listOf(id("AIFF") to Unit) },
                )
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(propChunk("AIFF", emptyShrdChunk))
                    write(groupChunk("FORM", "AIFF"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }

            test("writes no PROP chunks when properties disassembler returns empty map") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("AIFF")) { _: Unit -> listMultimapOf() }
                }
                val encoder = CatBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { emptyMap() },
                    disassembler = { listOf(id("AIFF") to Unit) },
                )
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe groupChunk("FORM", "AIFF")
            }

            test("throws when no PROP encoder is registered for the form type") {
                val core = IffEncoderCore.newCore {
                    addFormEncoder(id("AIFF")) { _: Unit -> listMultimapOf() }
                }
                val encoder = CatBodyEncoder.withProperties<Unit>(
                    core,
                    propertiesDisassembler = { mapOf(id("AIFF") to Unit) },
                    disassembler = { emptyList() },
                )
                shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
            }
        }
    }

    context("PropBodyEncoder") {

        test("encodes a local chunk into destination") {
            val core = IffEncoderCore.newCore {
                addLocalEncoder<String>(id("NAME")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
            }
            val encoder = PropBodyEncoder(core) { s: String ->
                listMultimapOf(id("NAME") to s as Any)
            }
            val dest = Buffer()
            encoder.encode("hi", dest)
            dest.readByteString() shouldBe iffChunk("NAME", "hi".toByteArray(Charsets.ISO_8859_1))
        }

        test("encodes multiple local chunks in insertion order") {
            val core = IffEncoderCore.newCore {
                addLocalEncoder<String>(id("NAME")) { value, dest ->
                    dest.writeString(value, Charsets.ISO_8859_1)
                }
                addLocalEncoder<UInt>(id("RATE")) { value, dest ->
                    dest.writeInt(value.toInt())
                }
            }
            val encoder = PropBodyEncoder(core) { pair: Pair<String, UInt> ->
                listMultimapOf(
                    id("NAME") to pair.first as Any,
                    id("RATE") to pair.second as Any,
                )
            }
            val dest = Buffer()
            encoder.encode("test" to 44100u, dest)
            val rateBytes = Buffer().apply { writeInt(44100) }.readByteArray()
            val expected = Buffer().apply {
                write(iffChunk("NAME", "test".toByteArray(Charsets.ISO_8859_1)))
                write(iffChunk("RATE", rateBytes))
            }.readByteString()
            dest.readByteString() shouldBe expected
        }

        test("throws when no local encoder is registered for the chunk type") {
            val core = IffEncoderCore.newCore {}
            val encoder = PropBodyEncoder(core) { _: Unit ->
                listMultimapOf(id("NAME") to Unit as Any)
            }
            shouldThrow<RiffletEncodeException> { encoder.encode(Unit, Buffer()) }
        }
    }

    context("IffEncoderCore.Builder") {

        context("addPropEncoder") {

            test("direct overload makes encoder accessible via propEncoders") {
                val propEncoder = PropBodyEncoder(IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                }) { _: Unit -> listMultimapOf(id("SHRD") to Unit as Any) }

                val core = IffEncoderCore.newCore {
                    addPropEncoder(id("SMPL"), propEncoder)
                }
                core.propEncoders[id("SMPL")] shouldBe propEncoder
            }

            test("lambda overload wires to the core's local encoders") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("SMPL")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                }
                @Suppress("UNCHECKED_CAST")
                val encoder = core.propEncoders.getValue(id("SMPL")) as PropBodyEncoder<Unit>
                val dest = Buffer()
                encoder.encode(Unit, dest)
                dest.readByteString() shouldBe emptyShrdChunk
            }
        }

        context("addListEncoder with propertiesDisassembler") {

            test("wires prop and group encoding through the same core") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("SMPL")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addFormEncoder(id("SMPL")) { _: Unit -> listMultimapOf() }
                    addListEncoder(
                        id("ALBM"),
                        propertiesDisassembler = { _: Unit -> mapOf(id("SMPL") to Unit) },
                        disassembler = { _: Unit -> listOf(id("SMPL") to Unit) },
                    )
                }
                @Suppress("UNCHECKED_CAST")
                val encoder = core.listEncoders.getValue(id("ALBM")) as ListBodyEncoder<Unit>
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(propChunk("SMPL", emptyShrdChunk))
                    write(groupChunk("FORM", "SMPL"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }
        }

        context("addCatEncoder with propertiesDisassembler") {

            test("wires prop and group encoding through the same core") {
                val core = IffEncoderCore.newCore {
                    addLocalEncoder<Unit>(id("SHRD")) { _, dest -> dest.writeInt(0) }
                    addPropEncoder(id("AIFF")) { _: Unit ->
                        listMultimapOf(id("SHRD") to Unit as Any)
                    }
                    addFormEncoder(id("AIFF")) { _: Unit -> listMultimapOf() }
                    addCatEncoder(
                        id("AIFF"),
                        propertiesDisassembler = { _: Unit -> mapOf(id("AIFF") to Unit) },
                        disassembler = { _: Unit -> listOf(id("AIFF") to Unit) },
                    )
                }
                @Suppress("UNCHECKED_CAST")
                val encoder = core.catEncoders.getValue(id("AIFF")) as CatBodyEncoder<Unit>
                val dest = Buffer()
                encoder.encode(Unit, dest)
                val expected = Buffer().apply {
                    write(propChunk("AIFF", emptyShrdChunk))
                    write(groupChunk("FORM", "AIFF"))
                }.readByteString()
                dest.readByteString() shouldBe expected
            }
        }
    }
})
