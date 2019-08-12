package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.PlanetPosition
import kotlin.math.cos

data class LunarState(
    val phase: Double,
    val days: Double,
    val illumination: Double,
    val dimension: Double
) {

    var phaseName: LunarPhase? = null

    companion object {

        fun from(lunar: PlanetPosition, solar: PlanetPosition): LunarState {
            val phaseAngle =
                Calculator.normalizeRadians((lunar.sLongitude - solar.sLongitude) * Constants.DEG_TO_RAD)

            val phase = phaseAngle / Constants.TWO_PI
            val days = phaseAngle * Constants.LUNAR_PERIOD / Constants.TWO_PI
            val illumination = 1.0 - (1.0 + cos(phaseAngle)) / 2.0

            // calculate lunar dimension in percent from average size
            val dimension = lunar.angularRadius / 0.00457249 * 100.0

            return LunarState(phase, days, illumination, dimension)
        }
    }
}
