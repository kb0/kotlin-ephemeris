package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.constellation.Zodiac
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SolarTest {
    @Test
    fun zodiacalTest() {
        val dt = LocalDateTime.of(2020, 1, 1, 0, 0)
        LongRange(0, 365).forEach {
            val jdTimeStart = JulianDate.toJD(dt.plusDays(it))
            val solarDayPosition =
                Solar().calculatePlanetPosition(
                    jdTimeStart[0],
                    JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
                    30.0,
                    30.0
                )

            println("${dt.plusDays(it).toLocalDate()} - ${Zodiac.create(solarDayPosition.sLongitude)}")
        }
    }
}