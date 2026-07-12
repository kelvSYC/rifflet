package com.kelvsyc.rifflet.internal.pkware

import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encodeUtf8

class PkwareExplodeTest : FunSpec({

    test("reference PKWare Implode test vector decompresses to the documented plaintext") {
        // From blast.c's header comment: the corrected worked example from Ben Rudiak-Gould's
        // original comp.compression post (the distance in his original example was wrong; this
        // corrected byte stream decompresses to "AIAIAIAIAIAIA").
        val source = Buffer().write("00048224258f807f".decodeHex())
        explode(source) shouldBe "AIAIAIAIAIAIA".encodeUtf8()
    }

    test("literal-encoding flag greater than one throws RiffletParseException") {
        val source = Buffer().write("02".decodeHex())
        shouldThrow<RiffletParseException> { explode(source) }
    }

    test("dictionary size outside 4..6 throws RiffletParseException") {
        val source = Buffer().write("0007".decodeHex())
        shouldThrow<RiffletParseException> { explode(source) }
    }
})
