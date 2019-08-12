package com.kbapps.ephemeris

import com.kbapps.ephemeris.type.ElevationType
import com.kbapps.ephemeris.type.TwilightType

object Constants {
    /**
     * Radians to degrees.
     */
    const val RAD_TO_DEG = 180.0 / Math.PI

    /**
     * Degrees to radians.
     */
    const val DEG_TO_RAD = 1.0 / RAD_TO_DEG

    /**
     * Radians to hours.
     */
    private const val RAD_TO_HOUR = 180.0 / (15.0 * Math.PI)

    /**
     * Radians to days.
     */
    private const val RAD_TO_DAY = RAD_TO_HOUR / 24.0

    /**
     * Astronomical Unit in km. As defined by JPL.
     */
    const val AU = 149597870.691

    /**
     * Earth equatorial radius in km. IERS 2003 Conventions.
     */
    const val EARTH_RADIUS = 6378.1366

    const val EARTH_RADIUS_AU = EARTH_RADIUS / AU


    /**
     * 2.0 * Pi.
     */
    const val TWO_PI = 2.0 * Math.PI

    /**
     * The inverse of 2.0 * Pi.
     */
    const val TWO_PI_INVERSE = 1.0 / (2.0 * Math.PI)
    /**
     * 4.0 * Pi.
     */
    const val FOUR_PI = 4.0 * Math.PI
    /**
     * Pi / 2.0.
     */
    const val PI_OVER_TWO = Math.PI / 2.0


    /**
     * Length of a sidereal day in days according to IERS Conventions.
     */
    private const val SIDEREAL_DAY_LENGTH = 1.00273781191135448

    /**
     * Julian century conversion constant = 100 * days per year.
     */
    const val JULIAN_DAYS_PER_CENTURY = 36525.0

    /**
     * Seconds in one day.
     */
    const val SECONDS_PER_DAY = 86400.0

    /**
     * Our default epoch.
     * The Julian Day which represents noon on 2000-01-01.
     */
    const val J2000 = 2451545.0


    const val celestialHoursToEarthTime = RAD_TO_DAY / SIDEREAL_DAY_LENGTH


    var LUNAR_PERIOD = 29.530588853


    val TWILIGHTS: Map<TwilightType, Array<ElevationType>> = mapOf(
        TwilightType.GOLDEN_HOURS to arrayOf(ElevationType.DEG_BELOW_4, ElevationType.DEG_ABOVE_6),
        TwilightType.BLUE_HOURS to arrayOf(ElevationType.DEG_BELOW_6, ElevationType.DEG_BELOW_4),

        TwilightType.CIVIL to arrayOf(ElevationType.DEG_BELOW_6, ElevationType.HORIZON),
        TwilightType.NAUTICAL to arrayOf(ElevationType.DEG_BELOW_12, ElevationType.DEG_BELOW_6),

        TwilightType.ASTRONOMICAL to arrayOf(ElevationType.DEG_BELOW_18, ElevationType.DEG_BELOW_12)
    )
}
