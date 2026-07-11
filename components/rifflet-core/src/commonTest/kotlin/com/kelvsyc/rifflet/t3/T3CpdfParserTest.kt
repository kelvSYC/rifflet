package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3CpdfParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.CPDF, 0x0000, body, body.size.toUInt())

private fun cpdfBinary(vararg pools: Pair<UInt, UInt>): Buffer = Buffer().apply {
    writeShortLe(pools.size)
    for ((pageCount, pageSize) in pools) {
        writeIntLe(pageCount.toInt())
        writeIntLe(pageSize.toInt())
    }
}

class T3CpdfParserTest : FunSpec({
    test("zero pools produces empty list") {
        val block = T3CpdfParser.parse(rawBlock(cpdfBinary()))
        block.pools shouldBe emptyList()
    }

    test("single pool: pageCount and pageSize round-trip") {
        val block = T3CpdfParser.parse(rawBlock(cpdfBinary(10u to 512u)))
        block.pools.size shouldBe 1
        block.pools[0].pageCount shouldBe 10u
        block.pools[0].pageSize shouldBe 512u
    }

    test("multiple pools: all entries parsed in order") {
        val block = T3CpdfParser.parse(rawBlock(cpdfBinary(10u to 512u, 5u to 1024u)))
        block.pools.size shouldBe 2
        block.pools[0].pageCount shouldBe 10u
        block.pools[0].pageSize shouldBe 512u
        block.pools[1].pageCount shouldBe 5u
        block.pools[1].pageSize shouldBe 1024u
    }

    test("poolEntry: poolId 1 returns first pool") {
        val block = CpdfBlock(listOf(CpdfPoolEntry(10u, 512u), CpdfPoolEntry(5u, 1024u)))
        block.poolEntry(1)?.pageCount shouldBe 10u
        block.poolEntry(1)?.pageSize shouldBe 512u
    }

    test("poolEntry: poolId 2 returns second pool") {
        val block = CpdfBlock(listOf(CpdfPoolEntry(10u, 512u), CpdfPoolEntry(5u, 1024u)))
        block.poolEntry(2)?.pageCount shouldBe 5u
    }

    test("poolEntry: poolId 0 returns null") {
        val block = CpdfBlock(listOf(CpdfPoolEntry(10u, 512u)))
        block.poolEntry(0) shouldBe null
    }

    test("poolEntry: poolId beyond range returns null") {
        val block = CpdfBlock(listOf(CpdfPoolEntry(10u, 512u)))
        block.poolEntry(2) shouldBe null
    }
})
