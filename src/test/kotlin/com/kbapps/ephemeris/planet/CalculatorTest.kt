package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.Utils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class CalculatorTest {

    @ParameterizedTest
    @CsvFileSource(resources = ["/solar_data.csv"], numLinesToSkip = 1)
    fun calculateSolarEventPosition(
        date: String,
        latitude: Double,
        longitude: Double,
        azimuth: Double,
        elevation: Double,
        riseTime: String?,
        riseAz: Double?,
        transitTime: String?,
        transitEl: Double?,
        dropTime: String?,
        dropAz: Double?,
        dayTime: String?
    ) {
        testCalculator(
            Solar(),
            date,
            latitude,
            longitude,
            azimuth,
            elevation,
            riseTime,
            riseAz,
            transitTime,
            transitEl,
            dropTime,
            dropAz,
            dayTime
        )
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/lunar_data.csv"], numLinesToSkip = 1)
    fun calculateLunarEventPosition(
        date: String,
        latitude: Double,
        longitude: Double,
        azimuth: Double,
        elevation: Double,
        riseTime: String?,
        riseAz: Double?,
        transitTime: String?,
        transitEl: Double?,
        dropTime: String?,
        dropAz: Double?,
        dayTime: String?,
        age: Double?
    ) {
        testCalculator(
            Lunar(),
            date,
            latitude,
            longitude,
            azimuth,
            elevation,
            riseTime,
            riseAz,
            transitTime,
            transitEl,
            dropTime,
            dropAz,
            dayTime
        )
    }

    private fun testCalculator(
        model: PlanetModel,
        date: String,
        latitude: Double,
        longitude: Double,
        azimuth: Double,
        elevation: Double,
        riseTime: String?,
        riseAz: Double?,
        transitTime: String?,
        transitEl: Double?,
        dropTime: String?,
        dropAz: Double?,
        dayTime: String?
    ) {
        val grDate = ZonedDateTime.parse(date)

        val jdTimeStart =
            JulianDate.toJD(grDate.withHour(0).withMinute(0).withSecond(0).withNano(1))
        val planetDayPosition =
            model.calculatePlanetPosition(
                jdTimeStart[0],
                JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
                latitude,
                longitude
            )

        val jdTime = JulianDate.toJD(grDate)
        val planetPosition =
            model.calculatePlanetPosition(
                jdTime[0],
                JulianDate.tFromJD(jdTime[0], jdTime[1]),
                latitude,
                longitude
            )

        // calculate transit time
        val transitPosition =
            Calculator.calculateEventPosition(jdTime[1], model, planetDayPosition, 0.0)
        if (transitTime != null) {
            assertNotNull(transitPosition)
            assertEquals(
                LocalTime.parse(transitTime),
                Utils.toLocalTime(transitPosition?.jd, grDate.offset),
                "incorrect transit time"
            )
            assertEquals(
                transitEl ?: 0.0,
                transitPosition!!.elevationInDeg,
                0.01,
                "incorrect transit elevation"
            )
        } else {
            assertNull(transitPosition)
        }

        // refraction correction
        val verticalAngle = Math.toRadians(-(34.0 / 60.0)) - planetDayPosition.angularRadius

        // calculate rise time
        val risePosition = Calculator.calculateEventPosition(
            jdTime[1], model, planetDayPosition, 90 - verticalAngle * Constants.RAD_TO_DEG
        )

        if (riseTime != null) {
            assertNotNull(risePosition)
            assertEquals(
                LocalTime.parse(riseTime),
                Utils.toLocalTime(risePosition?.jd, grDate.offset),
                "incorrect rise time"
            )
            assertEquals(riseAz ?: 0.0, risePosition!!.azimuthInDeg, 0.01, "incorrect rise azimuth")
        } else {
            assertNull(risePosition)
        }


        // calculate rise time
        val dropPosition = Calculator.calculateEventPosition(
            jdTime[1], model, planetDayPosition, -90 + verticalAngle * Constants.RAD_TO_DEG
        )

        if (dropTime != null) {
            assertNotNull(dropPosition)
            assertEquals(
                LocalTime.parse(dropTime),
                Utils.toLocalTime(dropPosition?.jd, grDate.offset),
                "incorrect drop time"
            )
            assertEquals(dropAz ?: 0.0, dropPosition!!.azimuthInDeg, 0.01, "incorrect drop azimuth")
        } else {
            assertNull(dropPosition)
        }
    }


    @Test
    fun testEquinox() {
        val latitude = 55.0;
        val longitude = 37.0;

        IntRange(0, 365 * 12).forEach {
            val grDate = ZonedDateTime.of(2022, 3, 20, 0, 0, 0, 1, ZoneId.systemDefault())
                .plusHours(4 * it.toLong())

            val jdTimeStart =
                JulianDate.toJD(grDate.withHour(0).withMinute(0).withSecond(0).withNano(1))

            val dat = Solar().calculatePlanetPosition(
                jdTimeStart[0],
                JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
                latitude,
                longitude
            )

            println(
                arrayOf(
                    grDate.toLocalDateTime(),
                    dat.jd,
                    dat.rightAscension,
                    dat.rightAscension * 180.0 / Math.PI
                )
                    .joinToString(";")
                    .replace(".", ",")
            )
        }
    }

    @Test
    fun testYearMilestone() {
        this.javaClass.classLoader.getResourceAsStream("solar_equinox_solstice.txt")
            ?.bufferedReader()
            ?.lines()
            ?.skip(1)
            ?.filter {
                it.length > 10
            }
            ?.forEach {
                val year = it.substring(1, 5).trim()

                mapOf(
                    "EQUINOX_SPRING" to it.substring(11, 24),
                    "EQUINOX_AUTUMN" to it.substring(47, 60),
                    "SOLSTICE_SUMMER" to it.substring(29, 42),
                    "SOLSTICE_WINTER" to it.substring(65, 78)
                ).forEach { (name, str) ->
                    val date = LocalDateTime.parse(
                        year + " " + str.trim(),
                        DateTimeFormatter.ofPattern("yyyy MMM dd  HH:mm", Locale.US)
                    )

                    println("ts(${date.atZone(ZoneOffset.UTC).toEpochSecond()}) to $name,")
                }
            }
    }
}