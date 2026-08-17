package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validWorldMap(flags: Int = 0): WorldMap = WorldMap(flags = flags)

class WorldMapFlagsTest : FunSpec({

    test("xWrapping is settable and backed by flags bit 0") {
        val map = validWorldMap()

        map.xWrapping shouldBe false
        map.xWrapping = true
        map.xWrapping shouldBe true
        map.flags shouldBe (1 shl 0)
    }

    test("yWrapping is settable and backed by flags bit 1") {
        val map = validWorldMap()

        map.yWrapping = true
        map.flags shouldBe (1 shl 1)
        map.yWrapping shouldBe true
    }

    test("polarIceCaps is settable and backed by flags bit 2") {
        val map = validWorldMap()

        map.polarIceCaps = true
        map.flags shouldBe (1 shl 2)
        map.polarIceCaps shouldBe true
    }

    test("setting xWrapping/yWrapping/polarIceCaps preserves other flags bits") {
        val map = validWorldMap(flags = 1 shl 5) // an unrelated, unnamed bit already set

        map.xWrapping = true
        map.yWrapping = true
        map.polarIceCaps = true

        map.flags shouldBe ((1 shl 5) or (1 shl 0) or (1 shl 1) or (1 shl 2))
    }

    test("clearing xWrapping clears only that bit") {
        val map = validWorldMap()
        map.xWrapping = true
        map.yWrapping = true

        map.xWrapping = false

        map.xWrapping shouldBe false
        map.yWrapping shouldBe true
    }
})
