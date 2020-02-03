package com.kbapps.ephemeris

import com.kbapps.ephemeris.planet.Lunar
import com.kbapps.ephemeris.planet.PlanetMilestone
import com.kbapps.ephemeris.planet.Solar
import org.junit.jupiter.api.Test
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class CustomCaseTest {

    @Test
    fun calculateSolarEventPosition() {
        val dateTime = ZonedDateTime.of(2004, 1, 1, 0, 0, 0, 1, ZoneOffset.ofHours(3));
        val latitude = 55.0;
        val longitude = 37.0;

        for (i in 1..500) {
            val time = dateTime.plusHours(i.toLong() * 2)
            val solarData = PlanetMilestone.from(Solar(), time, latitude, longitude)
            val lunarData = PlanetMilestone.from(Lunar(), time, latitude, longitude)

            println("$time - solar - ${solarData.currentPosition.sLatitude}, lunar - ${lunarData.currentPosition.sLatitude}")
        }
    }

}