package com.kelvsyc.rifflet.civ3.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validWorkerJob(name: String = "Mine"): WorkerJob = WorkerJob(name = name)

class WorkerJobTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val job = validWorkerJob()

        job.civilopediaEntry shouldBe ""
        job.turnsToComplete shouldBe 0
        job.required shouldBe null
        job.requiredResources shouldBe mutableListOf(null, null)
        job.order shouldBe ""
    }

    test("throws if requiredResources is not exactly 2 elements") {
        shouldThrow<IllegalArgumentException> {
            validWorkerJob().copy(requiredResources = mutableListOf(null))
        }
    }

    test("tfrmRequiredResourcesOf builds a front-packed 2-element list") {
        val resource = Resource(name = "Wine")

        tfrmRequiredResourcesOf(resource) shouldBe mutableListOf(resource, null)
    }

    test("tfrmRequiredResourcesOf allows the same resource twice") {
        val resource = Resource(name = "Wine")

        tfrmRequiredResourcesOf(resource, resource) shouldBe mutableListOf(resource, resource)
    }

    test("tfrmRequiredResourcesOf throws if given more than 2 resources") {
        val a = Resource(name = "Wine")
        val b = Resource(name = "Silk")
        val c = Resource(name = "Gems")

        shouldThrow<IllegalArgumentException> {
            tfrmRequiredResourcesOf(a, b, c)
        }
    }
})
