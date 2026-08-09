package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodResourceType

/**
 * A tradeable natural resource and its city-output bonuses — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.GoodEntry].
 *
 * @param name This resource's name.
 * @param civilopediaEntry This resource's Civilopedia entry key.
 * @param type Whether this is a Bonus, Luxury, or Strategic resource.
 * @param appearanceRatio How commonly this resource appears on the map. The Rules Editor disables
 *   this for Bonus resources — always `0` when [type] is [GoodResourceType.BONUS].
 * @param disappearanceProbability The chance this resource disappears over time. The Rules Editor
 *   disables this for Bonus resources — always `0` when [type] is [GoodResourceType.BONUS].
 * @param icon This resource's icon index.
 * @param prerequisite The technology required before this resource can appear/be used, if any.
 * @param foodBonus This resource's food output bonus.
 * @param shieldsBonus This resource's shield output bonus.
 * @param commerceBonus This resource's commerce output bonus.
 */
data class Resource(
    var name: String,
    var civilopediaEntry: String = "",
    var type: GoodResourceType = GoodResourceType.BONUS,
    var appearanceRatio: Int = 0,
    var disappearanceProbability: Int = 0,
    var icon: Int = 0,
    var prerequisite: Tech? = null,
    var foodBonus: Int = 0,
    var shieldsBonus: Int = 0,
    var commerceBonus: Int = 0,
)
