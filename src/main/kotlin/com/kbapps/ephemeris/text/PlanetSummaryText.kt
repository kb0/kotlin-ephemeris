package com.kbapps.ephemeris.text


import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.planet.LunarState
import com.kbapps.ephemeris.planet.PlanetMilestone
import com.kbapps.ephemeris.type.DirectionType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

data class PlanetSummaryText(
    val daytime: String,

    // lunar extra summary
    var lunarState: LunarState? = null,

    val riseTime: String,
    val riseDatetime: ZonedDateTime?,
    val riseAzimuth: Double?,
    val dropTime: String,
    val dropDatetime: ZonedDateTime?,
    val dropAzimuth: Double?,
    val transitTime: String,
    val transitDatetime: ZonedDateTime?,
    val transitElevation: Double?
) {

    companion object {

        private const val DURATION_FORMAT = "%d:%02d"

        fun from(
            planetMilestone: PlanetMilestone,
            zoneOffset: ZoneOffset,
            is12HoursFormat: Boolean
        ): PlanetSummaryText {
            val rise = planetMilestone.getHorizontalPosition(DirectionType.RISE)
            val drop = planetMilestone.getHorizontalPosition(DirectionType.DROP)
            val transit = planetMilestone.transitPosition

            var dayDuration: Int = -1
            if (rise == null && drop == null && transit != null) {
                dayDuration = (if (transit.elevationInDeg > 0) Constants.SECONDS_PER_DAY.roundToInt() else 0)
            } else if (rise != null && drop != null) {
                dayDuration = if (drop.jd > rise.jd) {
                    (abs(drop.jd - rise.jd) * Constants.SECONDS_PER_DAY).roundToInt()
                } else {
                    (Constants.SECONDS_PER_DAY - abs(drop.jd - rise.jd) * Constants.SECONDS_PER_DAY).roundToInt()
                }
            }

            // planetSummary.daytime = (dayDuration == -1 ? "" : String.format("%d:%02d:%02d", dayDuration / 3600, (dayDuration % 3600) / 60, (dayDuration % 60)));
            //planetSummary.riseDate = (rise != null ? simpleDateFormat.format(DateUtils.toCalendar(rise.jd).getTime()) : "");
            //planetSummary.dropDate = (rise != null ? simpleDateFormat.format(DateUtils.toCalendar(rise.jd).getTime()) : "");

            return PlanetSummaryText(
                daytime = if (dayDuration == -1) "" else String.format(
                    DURATION_FORMAT,
                    dayDuration / 3600,
                    dayDuration % 3600 / 60
                ),

                riseTime = JulianDate.toTimeString(rise?.jd, zoneOffset, is12HoursFormat),
                riseDatetime = JulianDate.toUTC(rise?.jd),
                riseAzimuth = rise?.azimuthInDeg,

                dropTime = JulianDate.toTimeString(drop?.jd, zoneOffset, is12HoursFormat),
                dropDatetime = JulianDate.toUTC(drop?.jd),
                dropAzimuth = drop?.azimuthInDeg,

                transitTime = JulianDate.toTimeString(transit?.jd, zoneOffset, is12HoursFormat),
                transitDatetime = JulianDate.toUTC(transit?.jd),
                transitElevation = transit?.elevationInDeg
            )
        }
    }

}
