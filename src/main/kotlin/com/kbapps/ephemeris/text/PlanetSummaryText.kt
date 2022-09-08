package com.kbapps.ephemeris.text


import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.planet.LunarState
import com.kbapps.ephemeris.planet.PlanetMilestone
import com.kbapps.ephemeris.type.DirectionType
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

data class PlanetSummaryText(
    val daytime: Int?,

    // lunar extra summary
    var lunarState: LunarState? = null,

    val riseDatetime: ZonedDateTime?,
    val riseAzimuth: Double?,
    val dropDatetime: ZonedDateTime?,
    val dropAzimuth: Double?,
    val transitDatetime: ZonedDateTime?,
    val transitElevation: Double?
) {

    companion object {

        fun from(
            planetMilestone: PlanetMilestone,
            zoneId: ZoneId
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

            return PlanetSummaryText(
                daytime = if (dayDuration == -1) null else dayDuration,

                riseDatetime = JulianDate.toUTC(rise?.jd)?.withZoneSameInstant(zoneId),
                riseAzimuth = rise?.azimuthInDeg,

                dropDatetime = JulianDate.toUTC(drop?.jd)?.withZoneSameInstant(zoneId),
                dropAzimuth = drop?.azimuthInDeg,

                transitDatetime = JulianDate.toUTC(transit?.jd)?.withZoneSameInstant(zoneId),
                transitElevation = transit?.elevationInDeg
            )
        }
    }

}
