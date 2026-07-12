package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validWsizEntry(reserved: ByteString = ByteString.of(*ByteArray(24))) = WsizEntry(
    optimalNumberOfCities = 12,
    techRate = 4,
    reserved = reserved,
    name = "Standard",
    height = 60,
    distanceBetweenCivs = 6,
    numberOfCivs = 7,
    width = 80,
)

class WsizEntryTest : FunSpec({

    test("a 24-byte reserved field is accepted") {
        validWsizEntry().reserved.size shouldBe 24
    }

    test("a reserved field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validWsizEntry(reserved = ByteString.of(1, 2, 3)) }
    }
})
