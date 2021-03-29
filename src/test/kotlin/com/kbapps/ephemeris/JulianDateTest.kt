package com.kbapps.ephemeris

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class CalculatorTest {
    @Test
    fun calculateJD() {
        assertArrayEquals(doubleArrayOf(2305082.5, 113.77082315816187), JulianDate.toJD(1599, 1, 1, 0, 0, 0))
        assertArrayEquals(doubleArrayOf(2455197.5, 60.69655723264441), JulianDate.toJD(2010, 1, 1, 0, 0, 0))

        assertArrayEquals(
            doubleArrayOf(2305082.5, 113.77082315816187),
            JulianDate.toJD(LocalDateTime.of(1599, 1, 1, 0, 0, 0))
        )
        assertArrayEquals(
            doubleArrayOf(2455197.5, 60.69655723264441),
            JulianDate.toJD(LocalDateTime.of(2010, 1, 1, 0, 0, 0))
        )

        assertEquals("12:00 AM", JulianDate.toTimeString(2305083.5, ZoneOffset.UTC, is12HoursFormat = true))
        assertEquals(
            "12:00:00 PM",
            JulianDate.toTimeString(2305083.0, ZoneOffset.UTC, is12HoursFormat = true, withSeconds = true)
        )

        assertEquals("00:00", JulianDate.toTimeString(2305083.5, ZoneOffset.UTC, is12HoursFormat = false))
        assertEquals(
            "12:00:00",
            JulianDate.toTimeString(2305083.0, ZoneOffset.UTC, is12HoursFormat = false, withSeconds = true)
        )
    }
}