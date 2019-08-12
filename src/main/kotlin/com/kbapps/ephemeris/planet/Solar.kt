package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.type.ElevationType
import com.kbapps.ephemeris.type.TwilightType
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class Solar : PlanetModel() {
    override fun twilights(): Map<TwilightType, Array<ElevationType>> {
        return mapOf(
            TwilightType.GOLDEN_HOURS to arrayOf(ElevationType.DEG_BELOW_4, ElevationType.DEG_ABOVE_6),
            TwilightType.BLUE_HOURS to arrayOf(ElevationType.DEG_BELOW_6, ElevationType.DEG_BELOW_4),

            TwilightType.CIVIL to arrayOf(ElevationType.DEG_BELOW_6, ElevationType.HORIZON),
            TwilightType.NAUTICAL to arrayOf(ElevationType.DEG_BELOW_12, ElevationType.DEG_BELOW_6),

            TwilightType.ASTRONOMICAL to arrayOf(ElevationType.DEG_BELOW_18, ElevationType.DEG_BELOW_12)
        )
    }


    public override fun calculateGeoPosition(jdTCorr: Double): DoubleArray {
        val sAnomaly = getSAnomaly(jdTCorr)

        val lon = sAnomaly[0]
        val sanomaly = sAnomaly[1]

        val c = (1.9146 - .004817 * jdTCorr - .000014 * jdTCorr * jdTCorr) * sin(sanomaly)
        +(.019993 - .000101 * jdTCorr) * sin(2 * sanomaly)
        +.00029 * sin(3.0 * sanomaly) // Correction to the mean ecliptic longitude

        // Now, let calculatePlanetPosition nutation and aberration
        val m1 = (124.90 - 1934.134 * jdTCorr + 0.002063 * jdTCorr * jdTCorr) * Constants.RAD_TO_DEG
        val m2 = (201.11 + 72001.5377 * jdTCorr + 0.00057 * jdTCorr * jdTCorr) * Constants.RAD_TO_DEG
        val d = -.00569 - .0047785 * sin(m1) - .0003667 * sin(m2)

        val sLongitude = lon + c + d // apparent longitude (error<0.003 deg)
        val sLatitude = 0.0 // Sun's ecliptic latitude is always negligible
        val ecc = .016708617 - 4.2037E-05 * jdTCorr - 1.236E-07 * jdTCorr * jdTCorr // Eccentricity
        val v = sanomaly + c * Constants.DEG_TO_RAD // True anomaly
        val sDistance = 1.000001018 * (1.0 - ecc * ecc) / (1.0 + ecc * cos(v)) // In UA

        return doubleArrayOf(sLongitude, sLatitude, sDistance, atan(696000 / (Constants.AU * sDistance)))
    }

    override fun calculationAccuracy(): Int {
        return 5
    }

    companion object {
        internal fun getSAnomaly(t: Double): DoubleArray {
            // SUN PARAMETERS (Formulae from "Calendrical Calculations")
            val lon = 280.46645 + 36000.76983 * t + .0003032 * t * t
            val anomaly = 357.5291 + 35999.0503 * t - .0001559 * t * t - 4.8E-07 * t * t * t
            return doubleArrayOf(lon, anomaly * Constants.DEG_TO_RAD)
        }
    }
}
