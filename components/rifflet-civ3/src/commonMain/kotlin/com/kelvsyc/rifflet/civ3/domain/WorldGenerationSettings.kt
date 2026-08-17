package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Age
import com.kelvsyc.rifflet.civ3.BarbarianActivity
import com.kelvsyc.rifflet.civ3.Climate
import com.kelvsyc.rifflet.civ3.Landform
import com.kelvsyc.rifflet.civ3.OceanCoverage
import com.kelvsyc.rifflet.civ3.Temperature

/**
 * One `WCHR` entry: the scenario's world-generation dropdown settings, mutable — the domain-layer
 * counterpart to [com.kelvsyc.rifflet.civ3.WchrEntry].
 *
 * @param climate This scenario's Climate dropdown pair. Same treatment applies to
 *   [barbarianActivity], [landform], [oceanCoverage], [temperature], and [age] — each a
 *   "Generate a Map" dialog dropdown's requested/rolled pair. See [GeneratedChoice].
 * @param worldSize This scenario's world-size preset, if it resolves. `null` when the wire index
 *   doesn't resolve against a real `WSIZ` preset — a real, editor-confirmed "no selection" state
 *   for a small number of real files, not a dangling-reference error.
 */
data class WorldGenerationSettings(
    var climate: GeneratedChoice<Climate> = GeneratedChoice(Climate.RANDOM, Climate.RANDOM),
    var barbarianActivity: GeneratedChoice<BarbarianActivity> = GeneratedChoice(BarbarianActivity.RANDOM, BarbarianActivity.RANDOM),
    var landform: GeneratedChoice<Landform> = GeneratedChoice(Landform.RANDOM, Landform.RANDOM),
    var oceanCoverage: GeneratedChoice<OceanCoverage> = GeneratedChoice(OceanCoverage.RANDOM, OceanCoverage.RANDOM),
    var temperature: GeneratedChoice<Temperature> = GeneratedChoice(Temperature.RANDOM, Temperature.RANDOM),
    var age: GeneratedChoice<Age> = GeneratedChoice(Age.RANDOM, Age.RANDOM),
    var worldSize: WorldSizePreset? = null,
)
