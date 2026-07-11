package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3MhlsParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.MHLS, 0x0000, body, body.size.toUInt())

private fun mhlsBinary(vararg addresses: UInt): Buffer = Buffer().apply {
    writeIntLe(addresses.size)
    for (addr in addresses) writeIntLe(addr.toInt())
}

class T3MhlsParserTest : FunSpec({
    test("zero entries produces empty list") {
        val block = T3MhlsParser.parse(rawBlock(mhlsBinary()))
        block.methodAddresses shouldBe emptyList()
    }

    test("multiple addresses are parsed in ascending order") {
        val block = T3MhlsParser.parse(rawBlock(mhlsBinary(0x1000u, 0x2000u, 0x3000u)))
        block.methodAddresses shouldBe listOf(0x1000u, 0x2000u, 0x3000u)
    }
})
