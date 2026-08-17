package com.kelvsyc.rifflet.civ3.domain

/**
 * One `CTZN` citizen type's naming and economic-output parameters, mutable — the domain-layer
 * counterpart to [com.kelvsyc.rifflet.civ3.CtznEntry].
 *
 * @param isDefault Whether this is the citizen type new citizens start as. Exactly one real
 *   `CTZN` entry has this set — see `validateCtznDefaultCount`.
 * @param prerequisite The technology required before this citizen type becomes available, if any.
 *   The default citizen type needs none — see `validateCtznDefaultPrerequisite`.
 */
data class Citizen(
    var singularName: String,
    var pluralName: String,
    var civilopediaEntry: String = "",
    var isDefault: Boolean = false,
    var prerequisite: Tech? = null,
    var luxuries: Int = 0,
    var research: Int = 0,
    var taxes: Int = 0,
    var corruption: Int = 0,
    var construction: Int = 0,
)
