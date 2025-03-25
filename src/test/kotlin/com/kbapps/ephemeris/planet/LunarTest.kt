package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.constellation.Zodiac
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

class LunarTest {
    @ParameterizedTest
    @MethodSource("zodiacalData")
    fun zodiacalTest(dt: LocalDateTime, sign: Zodiac) {
        val jdTimeStart = JulianDate.toJD(dt)

        val lunarDayPosition =
            Lunar().calculatePlanetPosition(
                jdTimeStart[0],
                JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
                30.0,
                30.0
            )
        assertEquals(sign, Zodiac.create(lunarDayPosition.sLongitude))
    }

    companion object {
        @JvmStatic
        fun zodiacalData(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(LocalDateTime.of(2020, 1, 1, 12, 0), Zodiac.PISCES),
                Arguments.arguments(LocalDateTime.of(2020, 2, 1, 12, 0), Zodiac.TAURUS),
                Arguments.arguments(LocalDateTime.of(2020, 3, 10, 3, 0), Zodiac.VIRGO),
                Arguments.arguments(LocalDateTime.of(2020, 3, 10, 12, 0), Zodiac.LIBRA),
                Arguments.arguments(LocalDateTime.of(2020, 10, 6, 12, 0), Zodiac.GEMINI),
            )
        }
    }
}