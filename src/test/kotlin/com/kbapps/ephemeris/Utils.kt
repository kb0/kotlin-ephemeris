package com.kbapps.ephemeris

import java.time.LocalTime
import java.time.ZoneOffset

/**
 * User: kb
 * Date: 24.02.2016
 * Time: 16:29
 */
object Utils {
    fun toLocalTime(jd: Double?, zoneOffset: ZoneOffset): LocalTime? {
        if (jd == null || jd == -1.0) {
            return null
        }

        return JulianDate.toUTC(jd).atZone(ZoneOffset.UTC)
            .withZoneSameInstant(zoneOffset).toLocalTime()
    }
}
