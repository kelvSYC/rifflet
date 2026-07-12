package com.kelvsyc.rifflet.internal.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString.Companion.encodeUtf8

class Civ3StringsTest : FunSpec({

    test("truncates at the first null byte, ignoring non-zero bytes after it") {
        val bytes = "Test".encodeUtf8().toByteArray() + byteArrayOf(0) + byteArrayOf(1, 2, 3)
        okio.ByteString.of(*bytes).truncateAtFirstNull() shouldBe "Test"
    }

    test("returns the full decoded string when there is no null byte") {
        "NoNull".encodeUtf8().truncateAtFirstNull() shouldBe "NoNull"
    }
})
