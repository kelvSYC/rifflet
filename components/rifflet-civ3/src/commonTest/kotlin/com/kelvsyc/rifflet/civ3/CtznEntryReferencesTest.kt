package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validCtznEntry(prerequisite: Int = 0): CtznEntry = CtznEntry(
    defaultCitizen = 0,
    singularName = "",
    civilopediaEntry = "",
    pluralName = "",
    prerequisite = prerequisite,
    luxuries = 0,
    research = 0,
    taxes = 0,
    corruption = 0,
    construction = 0,
)

private fun validTechEntry(): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = 0,
    prerequisite2 = 0,
    prerequisite3 = 0,
    prerequisite4 = 0,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

class CtznEntryReferencesTest : FunSpec({

    test("prerequisiteTech resolves against the TECH list") {
        val tech = validTechEntry()
        validCtznEntry(prerequisite = 0).prerequisiteTech(listOf(tech)) shouldBe tech
        validCtznEntry(prerequisite = 5).prerequisiteTech(emptyList()) shouldBe null
    }
})
