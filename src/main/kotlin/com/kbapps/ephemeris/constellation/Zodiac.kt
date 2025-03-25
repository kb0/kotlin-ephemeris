package com.kbapps.ephemeris.constellation

import com.kbapps.ephemeris.planet.Calculator

enum class Zodiac(val sign: String, val degreeFrom: Double, val degreeTo: Double) {
    ARIES("♈︎", 0.0, 30.0),
    TAURUS("♉︎", 30.0, 60.0),
    GEMINI("♊︎", 60.0, 90.0),
    CANCER("♋︎", 90.0, 120.0),
    LEO("♌︎", 120.0, 150.0),
    VIRGO("♍︎", 150.0, 180.0),
    LIBRA("♎︎", 180.0, 210.0),
    SCORPIO("♏︎", 210.0, 240.0),
    SAGITTARIUS("♐︎", 240.0, 270.0),
    CAPRICORN("♑︎", 270.0, 300.0),
    AQUARIUS("♒︎", 300.0, 330.0),
    PISCES("♓︎", 330.0, 360.0 + 1.0);

    companion object {
        fun create(angle: Double): Zodiac {
            val normalized = Calculator.normalizeDegree(angle)
            return entries.first {
                normalized >= it.degreeFrom && normalized < it.degreeTo
            }
        }
    }
}