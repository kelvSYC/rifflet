package com.kelvsyc.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ListMultimapTest : FunSpec({

    context("construction") {
        test("emptyListMultimap produces an empty multimap") {
            val m = emptyListMultimap<String, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("listMultimapOf with no arguments produces an empty multimap") {
            listMultimapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("listMultimapOf with a single pair") {
            val m = listMultimapOf("a" to 1)
            m.size shouldBe 1
            m["a"] shouldBe listOf(1)
        }

        test("toListMultimap from Iterable") {
            val m = listOf("a" to 1, "b" to 2, "a" to 3).toListMultimap()
            m.size shouldBe 3
            m["a"] shouldBe listOf(1, 3)
        }

        test("toListMultimap from Sequence") {
            val m = sequenceOf("a" to 1, "b" to 2, "a" to 3).toListMultimap()
            m.size shouldBe 3
            m["a"] shouldBe listOf(1, 3)
        }
    }

    context("duplicate pair preservation") {
        test("duplicate key-value pairs are preserved in entries") {
            val m = listMultimapOf("a" to 1, "a" to 1)
            m.size shouldBe 2
            m.entries.toList() shouldBe listOf("a" to 1, "a" to 1)
        }

        test("size counts all pairs including duplicates") {
            val m = listMultimapOf("a" to 1, "a" to 2, "a" to 1, "b" to 3)
            m.size shouldBe 4
        }

        test("get returns all values for a key including duplicates") {
            val m = listMultimapOf("a" to 1, "a" to 2, "a" to 1)
            m["a"] shouldBe listOf(1, 2, 1)
        }

        test("size is consistent with entries size") {
            val m = listMultimapOf("a" to 1, "a" to 1, "b" to 2)
            m.size shouldBe m.entries.size
        }
    }

    context("insertion order") {
        test("values for a key appear in insertion order") {
            val m = listMultimapOf("a" to 3, "a" to 1, "a" to 2)
            m["a"] shouldBe listOf(3, 1, 2)
        }

        test("entries appear in key-first-occurrence order, then value-insertion order within each key") {
            val m = listMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.entries.toList() shouldBe listOf("a" to 1, "a" to 3, "b" to 2)
        }

        test("keys appear in first-occurrence order") {
            val m = listMultimapOf("b" to 1, "a" to 2, "c" to 3, "a" to 4)
            m.keys.toList() shouldBe listOf("b", "a", "c")
        }

        test("values appear in key-first-occurrence order, then value-insertion order within each key") {
            val m = listMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.values.toList() shouldBe listOf(1, 3, 2)
        }
    }

    context("properties") {
        val m = listMultimapOf("a" to 1, "b" to 2, "a" to 3, "c" to 4)

        test("size is the total number of pairs") {
            m.size shouldBe 4
        }

        test("keys returns the set of distinct keys") {
            m.keys shouldBe setOf("a", "b", "c")
        }

        test("values returns all values in key-then-insertion order") {
            m.values.toList() shouldBe listOf(1, 3, 2, 4)
        }

        test("entries returns all pairs in key-then-insertion order") {
            m.entries.toList() shouldBe listOf("a" to 1, "a" to 3, "b" to 2, "c" to 4)
        }

        test("asMap maps each key to its value list") {
            m.asMap shouldBe mapOf("a" to listOf(1, 3), "b" to listOf(2), "c" to listOf(4))
        }

        test("isEmpty returns false for a non-empty multimap") {
            m.isEmpty().shouldBeFalse()
        }

        test("isNotEmpty returns true for a non-empty multimap") {
            m.isNotEmpty().shouldBeTrue()
        }
    }

    context("access") {
        val m = listMultimapOf("a" to 1, "a" to 2, "b" to 3)

        test("get returns the value list for a present key") {
            m["a"] shouldBe listOf(1, 2)
        }

        test("get returns an empty list for an absent key") {
            m["z"] shouldBe emptyList()
        }

        test("containsKey returns true for a present key") {
            m.containsKey("a").shouldBeTrue()
        }

        test("containsKey returns false for an absent key") {
            m.containsKey("z").shouldBeFalse()
        }

        test("contains operator reflects containsKey") {
            ("a" in m).shouldBeTrue()
            ("z" in m).shouldBeFalse()
        }

        test("containsValue returns true when the value is present under any key") {
            m.containsValue(1).shouldBeTrue()
        }

        test("containsValue returns false when the value is absent") {
            m.containsValue(99).shouldBeFalse()
        }

        test("containsEntry returns true for a present key-value pair") {
            m.containsEntry("a", 1).shouldBeTrue()
        }

        test("containsEntry returns false when the value is not associated with the key") {
            m.containsEntry("a", 99).shouldBeFalse()
        }

        test("containsEntry returns false when the key is absent") {
            m.containsEntry("z", 1).shouldBeFalse()
        }
    }

    context("null and empty helpers") {
        test("isNullOrEmpty returns true for null") {
            val m: ListMultimap<String, Int>? = null
            m.isNullOrEmpty().shouldBeTrue()
        }

        test("isNullOrEmpty returns true for an empty multimap") {
            emptyListMultimap<String, Int>().isNullOrEmpty().shouldBeTrue()
        }

        test("orEmpty returns the same multimap when non-null") {
            val m: ListMultimap<String, Int>? = listMultimapOf("a" to 1)
            m.orEmpty().size shouldBe 1
        }

        test("orEmpty returns an empty multimap when null") {
            val m: ListMultimap<String, Int>? = null
            m.orEmpty().isEmpty().shouldBeTrue()
        }
    }

    context("filter operations") {
        val m = listMultimapOf("a" to 1, "a" to 2, "b" to 3, "c" to 4)

        test("filter retains only matching pairs") {
            val result = m.filter { (_, v) -> v > 1 }
            result.entries.toList() shouldBe listOf("a" to 2, "b" to 3, "c" to 4)
        }

        test("filterKeys retains entries with matching keys") {
            val result = m.filterKeys { it != "b" }
            result.entries.toList() shouldBe listOf("a" to 1, "a" to 2, "c" to 4)
        }

        test("filterValues retains entries with matching values") {
            val result = m.filterValues { it % 2 == 0 }
            result.entries.toList() shouldBe listOf("a" to 2, "c" to 4)
        }

        test("filterValues omits keys that have no remaining values") {
            val result = listMultimapOf("a" to 1, "b" to 2).filterValues { it > 5 }
            result.isEmpty().shouldBeTrue()
        }
    }

    context("map operations") {
        test("mapValues transforms each value, preserving key structure") {
            val m = listMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val result = m.mapValues { it * 10 }
            result.entries.toList() shouldBe listOf("a" to 10, "a" to 20, "b" to 30)
        }

        test("mapKeys transforms each key") {
            val m = listMultimapOf("a" to 1, "b" to 2)
            val result = m.mapKeys { it.uppercase() }
            result.entries.toList() shouldBe listOf("A" to 1, "B" to 2)
        }

        test("mapKeys concatenates value lists when keys collide, in entries order") {
            val m = listMultimapOf("a" to 1, "b" to 2, "c" to 3)
            val result = m.mapKeys { "x" }
            result["x"] shouldBe listOf(1, 2, 3)
        }
    }

    context("plus operator") {
        val m = listMultimapOf("a" to 1, "b" to 2)

        test("plus a pair appends it under the existing key") {
            val result = m + ("a" to 3)
            result.entries.toList() shouldBe listOf("a" to 1, "a" to 3, "b" to 2)
        }

        test("plus another multimap concatenates value lists for colliding keys") {
            val other = listMultimapOf("a" to 3, "c" to 4)
            val result = m + other
            result.entries.toList() shouldBe listOf("a" to 1, "a" to 3, "b" to 2, "c" to 4)
        }

        test("plus an iterable of pairs appends all of them") {
            val result = m + listOf("b" to 3, "c" to 4)
            result.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "b" to 3, "c" to 4)
        }
    }

    context("minus operator") {
        val m = listMultimapOf("a" to 1, "a" to 2, "b" to 3, "c" to 4)

        test("minus a key removes all pairs for that key") {
            val result = m - "a"
            result.entries.toList() shouldBe listOf("b" to 3, "c" to 4)
        }

        test("minus an iterable of keys removes all pairs for those keys") {
            val result = m - listOf("a", "c")
            result.entries.toList() shouldBe listOf("b" to 3)
        }

        test("minus an absent key returns an equivalent multimap") {
            (m - "z") shouldBe m
        }
    }

    context("equality and hashCode") {
        test("two multimaps with the same content are equal") {
            listMultimapOf("a" to 1, "b" to 2) shouldBe listMultimapOf("a" to 1, "b" to 2)
        }

        test("multimaps with different values are not equal") {
            listMultimapOf("a" to 1) shouldNotBe listMultimapOf("a" to 2)
        }

        test("multimaps with different duplicate counts are not equal") {
            listMultimapOf("a" to 1, "a" to 1) shouldNotBe listMultimapOf("a" to 1)
        }

        test("equal multimaps have the same hashCode") {
            val a = listMultimapOf("a" to 1, "b" to 2)
            val b = listMultimapOf("a" to 1, "b" to 2)
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("toString") {
        test("toString expresses the multimap as an ordered list of pairs") {
            val m = listMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.toString() shouldBe "[(a, 1), (a, 3), (b, 2)]"
        }

        test("empty multimap toString is an empty list") {
            emptyListMultimap<String, Int>().toString() shouldBe "[]"
        }
    }
})
