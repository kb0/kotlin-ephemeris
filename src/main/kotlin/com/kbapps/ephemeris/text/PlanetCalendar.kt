package com.kbapps.ephemeris.text

import com.kbapps.ephemeris.planet.*
import java.time.ZonedDateTime

object PlanetCalendar {

    @Throws(Exception::class)
    fun calendar(
        localDateTime: ZonedDateTime,
        days: Int,
        latitude: Double,
        longitude: Double,
        is12HoursFormat: Boolean
    ): List<Map<PlanetModel.Type, PlanetSummaryText>> {
        val calendarSummary = ArrayList<Map<PlanetModel.Type, PlanetSummaryText>>()

        var day = localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(1)

        val solar = Solar()
        val lunar = Lunar()

        for (i in 0 until days + 1) {
            val daySummary = HashMap<PlanetModel.Type, PlanetSummaryText>()

            val solarData = PlanetMilestone.from(solar, day, latitude, longitude)
            val lunarData = PlanetMilestone.from(lunar, day, latitude, longitude)

            daySummary[PlanetModel.Type.SOLAR] =
                PlanetSummaryText.from(solarData, localDateTime.zone)
            daySummary[PlanetModel.Type.LUNAR] =
                PlanetSummaryText.from(lunarData, localDateTime.zone)

            // calculate lunar phase
            val lunarState = LunarState.from(lunarData.currentPosition, solarData.currentPosition)

            daySummary[PlanetModel.Type.LUNAR]?.lunarState = lunarState

            calendarSummary.add(daySummary)
            if (i > 0) {
                calendarSummary[i - 1][PlanetModel.Type.LUNAR]?.lunarState?.let { lastLunarState ->
                    // setup phase name for previous day
                    calendarSummary[i - 1][PlanetModel.Type.LUNAR]?.lunarState!!.phaseName =
                        LunarPhase.from(lastLunarState, lunarState)
                }
            }

            // next iteration
            day = day.plusDays(1)
        }

        // remove extra day (extra day for lunar phase name
        calendarSummary.removeAt(calendarSummary.size - 1)

        return calendarSummary
    }
}
