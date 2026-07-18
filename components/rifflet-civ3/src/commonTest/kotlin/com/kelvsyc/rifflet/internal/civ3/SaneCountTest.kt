package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class SaneCountTest : FunSpec({

    test("a count whose required bytes fit exactly is accepted") {
        val buffer = Buffer().apply { write(ByteArray(12)) }
        buffer.requireSaneCount(3, 4L, "test.field") shouldBe 3
    }

    test("a count one byte short of its required bytes throws RiffletParseException") {
        val buffer = Buffer().apply { write(ByteArray(11)) }
        shouldThrow<RiffletParseException> { buffer.requireSaneCount(3, 4L, "test.field") }
    }

    test("a negative count throws RiffletParseException") {
        val buffer = Buffer().apply { write(ByteArray(12)) }
        shouldThrow<RiffletParseException> { buffer.requireSaneCount(-1, 4L, "test.field") }
    }

    test("a zero count is always accepted regardless of remaining bytes") {
        val buffer = Buffer()
        buffer.requireSaneCount(0, 4L, "test.field") shouldBe 0
    }

    test("a huge count against a small buffer throws quickly rather than attempting to allocate") {
        val buffer = Buffer().apply { write(ByteArray(4)) }
        shouldThrow<RiffletParseException> { buffer.requireSaneCount(Int.MAX_VALUE, 4L, "test.field") }
    }
})
