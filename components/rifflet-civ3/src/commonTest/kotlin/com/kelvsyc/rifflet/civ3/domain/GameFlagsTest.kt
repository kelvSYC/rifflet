package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validGame(): Game = Game()

class GameFlagsTest : FunSpec({

    test("dominationVictoryEnabled (bit 0) and wonderVictoryEnabled (bit 16, highest flags bit) are independently settable") {
        val game = validGame()

        game.dominationVictoryEnabled = true
        game.wonderVictoryEnabled = true
        game.dominationVictoryEnabled = false

        game.dominationVictoryEnabled shouldBe false
        game.wonderVictoryEnabled shouldBe true
        game.flags shouldBe (1 shl 16)
    }

    test("acceleratedProduction (bit 9) and allowCulturalConversions (bit 15) round-trip independently") {
        val game = validGame()

        game.acceleratedProduction = true
        game.allowCulturalConversions = true

        game.flags shouldBe ((1 shl 9) or (1 shl 15))
        game.acceleratedProduction shouldBe true
        game.allowCulturalConversions shouldBe true
    }
})
