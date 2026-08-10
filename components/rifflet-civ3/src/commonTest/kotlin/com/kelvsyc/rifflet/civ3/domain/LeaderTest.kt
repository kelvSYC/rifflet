package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validLeader(name: String = "Caesar"): Leader = Leader(name = name)

class LeaderTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val leader = validLeader()

        leader.humanPlayer shouldBe false
        leader.customCivData shouldBe false
        leader.civilization shouldBe LeaderCivilization.Unrestricted
        leader.genderOfLeaderName shouldBe Gender.MALE
        leader.government shouldBe null
        leader.difficulty shouldBe LeaderDifficulty.Unrestricted
        leader.initialEra shouldBe null
        leader.startCash shouldBe 0
        leader.color shouldBe 0
        leader.startUnits shouldBe mutableListOf()
        leader.startingTechnologies shouldBe mutableListOf()
        leader.skipFirstTurn shouldBe false
        leader.startEmbassies shouldBe false
    }

    test("fields are mutable after construction") {
        val leader = validLeader()

        leader.civilization = LeaderCivilization.Random
        leader.difficulty = LeaderDifficulty.Preset(null)
        leader.startUnits.add(StartUnit(quantity = 2, unitType = null))

        leader.civilization shouldBe LeaderCivilization.Random
        leader.difficulty shouldBe LeaderDifficulty.Preset(null)
        leader.startUnits shouldBe mutableListOf(StartUnit(quantity = 2, unitType = null))
    }
})
