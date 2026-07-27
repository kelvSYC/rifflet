package com.kelvsyc.rifflet.civ3

/**
 * The government's unit support costs.
 *
 * Corresponds to the Conquests Rules Editor's `Governments` tab's "Unit Support Costs" groupbox,
 * in its entirety.
 *
 * @param freeUnits The "Free" field: units supported for free before [unitCost] applies.
 * @param freeUnitsPerTown The "Free Units Per" group's "Town" field.
 * @param freeUnitsPerCity The "Free Units Per" group's "City" field.
 * @param freeUnitsPerMetropolis The "Free Units Per" group's "Metropolis" field.
 * @param unitCost The "Cost/Unit" field: shield upkeep per unit beyond the free allowance.
 */
data class GovtUnitSupportCosts(
    val freeUnits: Int,
    val freeUnitsPerTown: Int,
    val freeUnitsPerCity: Int,
    val freeUnitsPerMetropolis: Int,
    val unitCost: Int,
)
