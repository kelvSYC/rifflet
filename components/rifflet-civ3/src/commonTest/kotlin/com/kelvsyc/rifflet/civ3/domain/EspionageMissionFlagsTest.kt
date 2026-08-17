package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validMission(missionFlags: Int = 0): EspionageMission =
    EspionageMission(name = "", missionFlags = missionFlags)

class EspionageMissionFlagsTest : FunSpec({

    test("diplomat is settable and backed by missionFlags bit 0") {
        val mission = validMission()

        mission.diplomat shouldBe false
        mission.diplomat = true
        mission.diplomat shouldBe true
        mission.missionFlags shouldBe 1
    }

    test("spy is settable and backed by missionFlags bit 1") {
        val mission = validMission()

        mission.spy = true
        mission.missionFlags shouldBe (1 shl 1)
        mission.spy shouldBe true
    }

    test("setting diplomat/spy preserves other missionFlags bits") {
        val mission = validMission(missionFlags = 1 shl 5) // an unrelated, unnamed bit already set

        mission.diplomat = true
        mission.spy = true

        mission.missionFlags shouldBe ((1 shl 5) or 1 or (1 shl 1))
    }

    test("clearing diplomat clears only that bit") {
        val mission = validMission()
        mission.diplomat = true
        mission.spy = true

        mission.diplomat = false

        mission.diplomat shouldBe false
        mission.spy shouldBe true
    }
})
