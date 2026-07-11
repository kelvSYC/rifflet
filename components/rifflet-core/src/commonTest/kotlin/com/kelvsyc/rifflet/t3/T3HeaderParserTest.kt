package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3HeaderParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex

private val VALID_MAGIC = "54332d696d6167650d0a1a".decodeHex()

/** Builds a valid 69-byte T3 preamble. */
private fun preambleBinary(
    magic: ByteString = VALID_MAGIC,
    version: Int = 2,
    reservedZero: ByteArray = ByteArray(28),
    buildHash: ByteArray = byteArrayOf(0x0A, 0x0B, 0x0C, 0x0D),
    timestamp: String = "Sun Aug 01 17:05:20 1999",
): Buffer = Buffer().apply {
    write(magic)
    writeShortLe(version)
    write(reservedZero)
    write(buildHash)
    writeString(timestamp, Charsets.US_ASCII)
}

class T3HeaderParserTest : FunSpec({

    test("well-formed preamble is parsed into version, build hash, and timestamp") {
        val source = preambleBinary()
        val header = T3HeaderParser.parse(source)
        header.version shouldBe 2
        header.buildHash shouldBe ByteString.of(0x0A, 0x0B, 0x0C, 0x0D)
        header.timestamp shouldBe "Sun Aug 01 17:05:20 1999"
        source.exhausted() shouldBe true
    }

    test("bad magic signature throws RiffletParseException") {
        val source = preambleBinary(magic = "00000000000000000000".decodeHex())
        shouldThrow<RiffletParseException> { T3HeaderParser.parse(source) }
    }

    test("non-zero reserved byte throws RiffletParseException") {
        val badReserved = ByteArray(28).also { it[10] = 0x01 }
        val source = preambleBinary(reservedZero = badReserved)
        shouldThrow<RiffletParseException> { T3HeaderParser.parse(source) }
    }
})
