package com.kbapps.ephemeris.planet

enum class LunarPhase {
    NEW_MOON,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS,
    FULL_MOON,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT;


    companion object {

        fun from(phaseStart: LunarState, phaseEnd: LunarState): LunarPhase {
            return from(phaseStart.phase, phaseEnd.phase)
        }

        fun from(phaseStart: Double, phaseEnd: Double): LunarPhase {
            if (phaseStart >= 0.75 && phaseEnd <= 0.25) {
                return NEW_MOON
            }

            if (phaseStart <= 0.25 && phaseEnd >= 0.25) {
                return FIRST_QUARTER
            }

            if (phaseStart <= 0.50 && phaseEnd >= 0.50) {
                return FULL_MOON
            }

            if (phaseStart <= 0.75 && phaseEnd >= 0.75) {
                return LAST_QUARTER
            }

            if (phaseEnd < 0.25) {
                return WAXING_CRESCENT
            }
            if (phaseEnd < 0.50) {
                return WAXING_GIBBOUS
            }
            return if (phaseEnd < 0.75) {
                WANING_GIBBOUS
            } else WANING_CRESCENT

        }
    }
}
