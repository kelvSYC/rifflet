package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private enum class ParseIndexedTestFixture { ONLY }

class ParseIndexedTest : FunSpec({

    test("parses every item in order when none throw") {
        val items = listOf(Buffer().apply { writeByte(1) }, Buffer().apply { writeByte(2) }, Buffer().apply { writeByte(3) })
        val result = items.parseIndexed(Civ3SectionIds.GOOD) { it.readByte().toInt() }
        result shouldBe listOf(1, 2, 3)
    }

    test("enriches a Civ3EnumDecodeException thrown mid-list with the section and offending index") {
        val items = listOf(Buffer(), Buffer(), Buffer())
        var calls = 0
        val e = shouldThrow<Civ3EnumDecodeException> {
            items.parseIndexed(Civ3SectionIds.GOOD) { _ ->
                if (calls++ == 1) {
                    decodeEnum("test.field", -1, ParseIndexedTestFixture.entries)
                } else {
                    ParseIndexedTestFixture.ONLY
                }
            }
        }
        e.section shouldBe Civ3SectionIds.GOOD
        e.index shouldBe 1
        e.field shouldBe "test.field"
    }
})
