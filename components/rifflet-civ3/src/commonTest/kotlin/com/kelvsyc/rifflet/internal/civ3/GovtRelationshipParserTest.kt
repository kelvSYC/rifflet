package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GovtRelationship
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun relationshipBinary(canBribe: Int = 1, propagandaModifier: Int = 20, resistanceModifier: Int = 30): Buffer =
    Buffer().apply {
        writeIntLe(canBribe)
        writeIntLe(propagandaModifier)
        writeIntLe(resistanceModifier)
    }

class GovtRelationshipParserTest : FunSpec({

    test("well-formed 12-byte entry is parsed into all three fields") {
        val relationship = GovtRelationshipParser.parse(relationshipBinary())
        relationship shouldBe GovtRelationship(canBribe = 1, propagandaModifier = 20, resistanceModifier = 30)
    }
})
