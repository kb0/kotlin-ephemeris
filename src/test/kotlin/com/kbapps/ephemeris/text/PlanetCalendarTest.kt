package com.kbapps.ephemeris.text

import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.planet.Lunar
import com.kbapps.ephemeris.planet.LunarState
import com.kbapps.ephemeris.planet.PlanetModel
import com.kbapps.ephemeris.planet.Solar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PlanetCalendarTest {

    @ParameterizedTest
    @CsvFileSource(resources = ["/solar_data.csv"], numLinesToSkip = 1)
    internal fun calculateSolarCalendar(
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
            PlanetModel.Type.SOLAR,
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
    internal fun calculateLunarCalendar(
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
            PlanetModel.Type.LUNAR,
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
    internal fun calculateLunarStateCalendar(
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
        age: Double
    ) {
        val grDate = ZonedDateTime.parse(date)
        val jdTimeStart = JulianDate.toJD(grDate)

        val solarPosition = Solar().calculatePlanetPosition(
            jdTimeStart[0],
            JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
            latitude,
            longitude
        )
        val lunarPosition = Lunar().calculatePlanetPosition(
            jdTimeStart[0],
            JulianDate.tFromJD(jdTimeStart[0], jdTimeStart[1]),
            latitude,
            longitude
        )

        val lunarState = LunarState.from(lunarPosition, solarPosition)
        assertEquals(age, lunarState.days, 0.01)
    }

    private fun testCalculator(
        model: PlanetModel.Type,
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
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val grDate = ZonedDateTime.parse(date)

        val calendarData = PlanetCalendar.calendar(grDate, 1, latitude, longitude)

        val test = calendarData[0][model]
        riseTime?.let {
            assertEquals(
                LocalTime.parse(riseTime).format(formatter),
                test?.riseDatetime?.toLocalTime()?.format(formatter),
                test.toString()
            )
            assertEquals(riseAz!!, test?.riseAzimuth!!, 0.01)
        }

        dropTime?.let {
            assertEquals(
                LocalTime.parse(dropTime).format(formatter),
                test?.dropDatetime?.toLocalTime()?.format(formatter)
            )
            assertEquals(dropAz!!, test?.dropAzimuth!!, 0.01)
        }

        transitTime?.let {
            assertEquals(
                LocalTime.parse(transitTime).format(formatter),
                test?.transitDatetime?.toLocalTime()?.format(formatter)
            )
            assertEquals(transitEl!!, test?.transitElevation!!, 0.01)
        }
    }


}