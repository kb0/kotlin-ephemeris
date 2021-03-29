package com.kbapps.ephemeris.text


import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.PlanetPosition
import com.kbapps.ephemeris.planet.PlanetMilestone
import com.kbapps.ephemeris.type.DirectionType
import com.kbapps.ephemeris.type.TwilightType
import java.time.ZoneOffset
import java.util.*

/**
 * User: kb
 * Date: 02.03.2016
 * Time: 19:16
 */
object TwilightSummaryText {
    @Throws(Exception::class)
    fun twilight(
        planetMilestone: PlanetMilestone,
        twilights: Array<TwilightType>,
        zoneOffset: ZoneOffset,
        is12HoursFormat: Boolean
    ): Map<TwilightType, Array<String>> {
        val twilightsData = HashMap<TwilightType, Array<String>>()

        var start: PlanetPosition?
        var end: PlanetPosition?

        for (twilightType in twilights) {
            val elevations = Constants.TWILIGHTS[twilightType]
            assert(elevations!!.size == 2)

            start = planetMilestone.getElevationPositions(DirectionType.RISE)!![elevations[0]]
            end = planetMilestone.getElevationPositions(DirectionType.RISE)?.get(elevations[1])
            val riseStartTime = JulianDate.toTimeString(start?.jd, zoneOffset, is12HoursFormat)
            val riseEndTime = JulianDate.toTimeString(end?.jd, zoneOffset, is12HoursFormat)

            start = planetMilestone.getElevationPositions(DirectionType.DROP)?.get(elevations[0])
            end = planetMilestone.getElevationPositions(DirectionType.DROP)?.get(elevations[1])
            val dropStartTime = JulianDate.toTimeString(start?.jd, zoneOffset, is12HoursFormat)
            val dropEndTime = JulianDate.toTimeString(end?.jd, zoneOffset, is12HoursFormat)

            twilightsData[twilightType] = arrayOf(
                "$riseStartTime - $riseEndTime".trim { it <= ' ' }.replace("^-$".toRegex(), ""),
                "$dropEndTime - $dropStartTime".trim { it <= ' ' }.replace("^-$".toRegex(), "")
            )
        }

        return twilightsData
    }
}
