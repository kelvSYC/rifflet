package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.diplomat]/[com.kelvsyc.rifflet.civ3.spy] —
 * see that file's KDoc for what each bit means.
 */
var EspionageMission.diplomat: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ missionFlags }, { missionFlags = it }, 0)
var EspionageMission.spy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ missionFlags }, { missionFlags = it }, 1)
