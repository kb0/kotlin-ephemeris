package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.JulianDate
import com.kbapps.ephemeris.PlanetPosition
import com.kbapps.ephemeris.type.ElevationType
import kotlin.math.*

object Calculator {
    /**
     * Reduce an angle in radians to the range (0 - 2 Pi).
     *
     * @param rad Value in radians.
     * @return The reduced radian value.
     */
    fun normalizeRadians(rad: Double): Double {
        var r = rad

        if (r < 0 && r >= -Constants.TWO_PI) return r + Constants.TWO_PI
        if (r >= Constants.TWO_PI && r < Constants.FOUR_PI)
            return r - Constants.TWO_PI
        if (r >= 0 && r < Constants.TWO_PI) return r

        r -= Constants.TWO_PI * floor(r * Constants.TWO_PI_INVERSE)
        if (r < 0.0) r += Constants.TWO_PI

        return r
    }


    /**
     * @param position           - planet position
     * @param verticalAngleInDeg - df
     * 0 - transit point
     * +90 - horizontal level at "rise"
     * -90 - horizontal level at "drop"
     * @return double
     */
    private fun calculateTimeForVerticalAngle(
        jdOfDay: Double,
        jdPrev: Double?,
        position: PlanetPosition,
        verticalAngleInDeg: Double
    ): Double {
        val lst = position.gmst + position.observerLongitudeInRad

        var horizontalAngle = 0.0
        if (verticalAngleInDeg != 0.0) {
            // Compute cosine of horizontal angle
            horizontalAngle =
                (sin(Math.toRadians(90.0 - abs(verticalAngleInDeg))) - sin(position.observerLatitudeInRad) * sin(
                    position.declination
                )) / (cos(position.observerLatitudeInRad) * cos(position.declination))
            if (abs(horizontalAngle) > 1.0) {
                return -1.0
            }

            horizontalAngle = abs(acos(horizontalAngle)) * sign(verticalAngleInDeg)
        }

        // position.jd - should be start of the current day

        // Make calculations for rise and set
        val eventTime1 = position.jd +
                Constants.celestialHoursToEarthTime * normalizeRadians(position.rightAscension - horizontalAngle - lst)
        val eventTime2 = position.jd +
                Constants.celestialHoursToEarthTime * (normalizeRadians(position.rightAscension - horizontalAngle - lst) - Constants.TWO_PI)

        if (jdPrev != null) {
            return if (abs(jdPrev - eventTime1) < abs(jdPrev - eventTime2)) {
                eventTime1
            } else {
                eventTime2
            }
        }

        // Obtain the current events in time. Preference should be given to the closest event
        // in time to the current calculation time (so that iteration in other method will converge)
        return if (min(eventTime1, eventTime2) > jdOfDay) {
            min(eventTime1, eventTime2)
        } else {
            max(eventTime1, eventTime2)
        }
    }

    fun calculateEventPosition(
        timeTTdUT: Double,
        planetModel: PlanetModel, sourcePlanetPosition: PlanetPosition,
        verticalAngleInDeg: Double
    ): PlanetPosition? {

        var accuratePlanetPosition: PlanetPosition? = null

        var step = -1.0
        var eventJD =
            calculateTimeForVerticalAngle(sourcePlanetPosition.jd, null, sourcePlanetPosition, verticalAngleInDeg)

        for (i in 0 until planetModel.calculationAccuracy()) {
            if (eventJD == -1.0 || java.lang.Double.isInfinite(eventJD) || java.lang.Double.isNaN(eventJD)) {
                return null
            }

            val tCurrent = JulianDate.tFromJD(eventJD, timeTTdUT)

            // calculatePlanetPosition new planet position
            accuratePlanetPosition = planetModel.calculatePlanetPosition(
                eventJD,
                tCurrent,
                sourcePlanetPosition.observerLatitudeInDeg,
                sourcePlanetPosition.observerLongitudeInDeg
            )
            val newEventJD =
                calculateTimeForVerticalAngle(
                    sourcePlanetPosition.jd,
                    eventJD,
                    accuratePlanetPosition,
                    verticalAngleInDeg
                )

            step = abs(eventJD - newEventJD)
            eventJD = newEventJD

            if (step < (1.0 / Constants.SECONDS_PER_DAY) * 0.1) {
                break
            }
        }

        if (step > 1.0 / Constants.SECONDS_PER_DAY) {
            return null
        }

        return if (verticalAngleInDeg == 0.0 && accuratePlanetPosition != null && accuratePlanetPosition.elevationInDeg < 0) {
            null
        } else accuratePlanetPosition

    }

    fun elevationTypeToVerticalAngle(elevationType: ElevationType, angularRadius: Double): Double {
        return when (elevationType) {
            ElevationType.DEG_ABOVE_6 -> 90.0 - 6

            ElevationType.HORIZON -> 90.0 + Math.toDegrees(Math.toRadians(34.0 / 60.0) + angularRadius)

            ElevationType.DEG_BELOW_4 -> 90.0 + 4.0
            ElevationType.DEG_BELOW_6 -> 90.0 + 6.0
            ElevationType.DEG_BELOW_12 -> 90.0 + 12.0
            ElevationType.DEG_BELOW_18 -> 90.0 + 18.0

            ElevationType.TRANSIT -> 0.0
        }
    }
}
