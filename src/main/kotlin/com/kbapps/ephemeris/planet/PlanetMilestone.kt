package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.PlanetPosition
import com.kbapps.ephemeris.type.DirectionType
import com.kbapps.ephemeris.type.ElevationType
import org.threeten.bp.ZonedDateTime

data class PlanetMilestone(
    val startOfDayPosition: PlanetPosition,
    val currentPosition: PlanetPosition,
    var transitPosition: PlanetPosition? = null
) {
    private val elevationPositions: MutableMap<DirectionType, MutableMap<ElevationType, PlanetPosition?>> =
        mutableMapOf(
            DirectionType.RISE to mutableMapOf(),
            DirectionType.DROP to mutableMapOf()
        )

    fun getHorizontalPosition(directionType: DirectionType): PlanetPosition? {
        return elevationPositions[directionType]?.get(ElevationType.HORIZON)
    }

    fun getElevationPositions(directionType: DirectionType): Map<ElevationType, PlanetPosition?>? {
        return elevationPositions[directionType]
    }

    companion object {

        fun from(
            planetModel: PlanetModel,
            dateTime: ZonedDateTime,
            latitudeInDeg: Double,
            longitudeInDeg: Double
        ): PlanetMilestone {
            // start of current day
            val jdTimeStartOfDay = JulianDate.toJD(dateTime.withHour(0).withMinute(0).withSecond(0).withNano(1))
            // current time
            val jdTimeCurrentTime = JulianDate.toJD(dateTime)

            val planetMilestone = PlanetMilestone(
                startOfDayPosition =
                planetModel.calculatePlanetPosition(
                    jdTimeStartOfDay[0],
                    JulianDate.tFromJD(jdTimeStartOfDay[0], jdTimeStartOfDay[1]), latitudeInDeg, longitudeInDeg
                ),
                currentPosition =
                planetModel.calculatePlanetPosition(
                    jdTimeCurrentTime[0],
                    JulianDate.tFromJD(jdTimeCurrentTime[0], jdTimeCurrentTime[1]), latitudeInDeg, longitudeInDeg
                )
            )

            val angularRadius = planetMilestone.startOfDayPosition.angularRadius

            // calculate "transit" position
            planetMilestone.transitPosition = Calculator.calculateEventPosition(
                jdTimeStartOfDay[1], planetModel, planetMilestone.startOfDayPosition,
                Calculator.elevationTypeToVerticalAngle(ElevationType.TRANSIT, angularRadius)
            )

            // calculate "rise" position
            planetMilestone.elevationPositions[DirectionType.RISE]?.set(
                ElevationType.HORIZON, Calculator.calculateEventPosition(
                    jdTimeStartOfDay[1], planetModel, planetMilestone.startOfDayPosition,
                    +1.0 * Calculator.elevationTypeToVerticalAngle(ElevationType.HORIZON, angularRadius)
                )
            )

            // calculate "drop" position
            planetMilestone.elevationPositions[DirectionType.DROP]?.set(
                ElevationType.HORIZON, Calculator.calculateEventPosition(
                    jdTimeStartOfDay[1], planetModel,
                    planetMilestone.startOfDayPosition,
                    -1.0 * Calculator.elevationTypeToVerticalAngle(ElevationType.HORIZON, angularRadius)
                )
            )

            // calculate twilights
            for (twilight in planetModel.twilights().entries) {
                for (twilightElevation in twilight.value) {
                    val verticalAngle =
                        Calculator.elevationTypeToVerticalAngle(twilightElevation, angularRadius)

                    planetMilestone.elevationPositions[DirectionType.RISE]?.set(
                        twilightElevation,
                        Calculator.calculateEventPosition(
                            jdTimeStartOfDay[1],
                            planetModel,
                            planetMilestone.startOfDayPosition,
                            +1.0 * verticalAngle
                        )
                    )

                    planetMilestone.elevationPositions[DirectionType.DROP]?.set(
                        twilightElevation,
                        Calculator.calculateEventPosition(
                            jdTimeStartOfDay[1],
                            planetModel,
                            planetMilestone.startOfDayPosition,
                            -1.0 * verticalAngle
                        )
                    )
                }
            }


            return planetMilestone
        }
    }
}
