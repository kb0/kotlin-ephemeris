package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.PlanetPosition
import com.kbapps.ephemeris.type.ElevationType
import com.kbapps.ephemeris.type.TwilightType
import kotlin.math.*

abstract class PlanetModel {
    enum class Type {
        SOLAR,
        LUNAR
    }

    abstract fun twilights(): Map<TwilightType, Array<ElevationType>>

    protected abstract fun calculateGeoPosition(jdTCorr: Double): DoubleArray

    abstract fun calculationAccuracy(): Int

    fun calculatePlanetPosition(
        jd: Double,
        jdCoef: Double,
        latitudeInDeg: Double,
        longitudeInDeg: Double
    ): PlanetPosition {
        val planetPosition =
            PlanetPosition(jd, jdCoef, longitudeInDeg * Constants.DEG_TO_RAD, latitudeInDeg * Constants.DEG_TO_RAD)


        val geoPos = calculateGeoPosition(planetPosition.jdCoef)
        planetPosition.sLongitude = geoPos[0]
        planetPosition.sLatitude = geoPos[1]
        planetPosition.sDistance = geoPos[2]
        planetPosition.angularRadius = geoPos[3]

        val planetLongitudeRad = planetPosition.sLongitude * Constants.DEG_TO_RAD
        val planetLatitudeRad = planetPosition.sLatitude * Constants.DEG_TO_RAD
        val planetDistance = planetPosition.sDistance


        // Ecliptic to equatorial coordinates
        val t2 = planetPosition.jdCoef / 100.0
        var tmp = t2 * (27.87 + t2 * (5.79 + t2 * 2.45))
        tmp = t2 * (-249.67 + t2 * (-39.05 + t2 * (7.12 + tmp)))
        tmp = t2 * (-1.55 + t2 * (1999.25 + t2 * (-51.38 + tmp)))
        tmp = t2 * (-4680.93 + tmp) / 3600.0
        val angle = (23.4392911111111 + tmp) * Constants.DEG_TO_RAD // obliquity

        val cl = cos(planetLatitudeRad)
        val x = planetDistance * cos(planetLongitudeRad) * cl
        var y = planetDistance * sin(planetLongitudeRad) * cl
        var z = planetDistance * sin(planetLatitudeRad)
        tmp = y * cos(angle) - z * sin(angle)
        z = y * sin(angle) + z * cos(angle)
        y = tmp

        // Obtain local apparent sidereal time
        val jd0 = floor(planetPosition.jd - 0.5) + 0.5
        val t0 = (jd0 - Constants.J2000) / Constants.JULIAN_DAYS_PER_CENTURY
        val secs = (planetPosition.jd - jd0) * Constants.SECONDS_PER_DAY
        var gmst = ((-6.2e-6 * t0 + 9.3104e-2) * t0 + 8640184.812866) * t0 + 24110.54841
        val msday =
            1.0 + ((-1.86e-5 * t0 + 0.186208) * t0 + 8640184.812866) / (Constants.SECONDS_PER_DAY * Constants.JULIAN_DAYS_PER_CENTURY)
        gmst = (gmst + msday * secs) * (15.0 / 3600.0) * Constants.DEG_TO_RAD
        val lst = gmst + planetPosition.observerLongitudeInRad

        // Obtain topocentric rectangular coordinates, set radiusAU = 0 for geocentric calculations
        val xTopo = x - Constants.EARTH_RADIUS_AU * cos(planetPosition.observerLatitudeInRad) * cos(lst)
        val yTopo = y - Constants.EARTH_RADIUS_AU * cos(planetPosition.observerLatitudeInRad) * sin(lst)
        val zTopo = z - Constants.EARTH_RADIUS_AU * sin(planetPosition.observerLatitudeInRad)

        // Obtain topocentric equatorial coordinates
        var ra = 0.0
        var dec = Constants.PI_OVER_TWO
        if (zTopo < 0.0)
            dec = -dec
        if (yTopo != 0.0 || xTopo != 0.0) {
            ra = atan2(yTopo, xTopo)
            dec = atan2(zTopo / sqrt(xTopo * xTopo + yTopo * yTopo), 1.0)
        }
        val dist = sqrt(xTopo * xTopo + yTopo * yTopo + zTopo * zTopo)


        // setup Greenwich Mean Sidereal Time
        planetPosition.gmst = gmst

        // setup equatorial	(geocentric based) coordinates
        planetPosition.declination = dec
        planetPosition.rightAscension = ra

        // setup horizontal (observer based) coordinates
        val angh = lst - ra


        // Obtain azimuth and geometric alt
        val sinLat = sin(planetPosition.observerLatitudeInRad)
        val cosLat = cos(planetPosition.observerLatitudeInRad)
        val sinDec = sin(dec)
        val cosDec = cos(dec)
        val h = sinLat * sinDec + cosLat * cosDec * cos(angh)
        var alt = asin(h)
        val azy = sin(angh)
        val azx = cos(angh) * sinLat - sinDec * cosLat / cosDec

        // Get apparent elevation
        if (alt > -3 * Constants.DEG_TO_RAD) {
            val r =
                0.016667 * Constants.DEG_TO_RAD * abs(tan(Constants.PI_OVER_TWO - (alt * Constants.RAD_TO_DEG + 7.31 / (alt * Constants.RAD_TO_DEG + 4.4)) * Constants.DEG_TO_RAD))

            // refraction correction
            // @TODO http://www.esrl.noaa.gov/gmd/grad/solcalc/calcdetails.html
            val refr = r * (0.28 * 1010 / (10 + 273.0)) // Assuming pressure of 1010 mb and T = 10 C
            alt = min(alt + refr, Constants.PI_OVER_TWO) // This is not accurate, but acceptable
        }

        planetPosition.azimuthInRad = Math.PI + atan2(azy, azx)
        planetPosition.elevationInRad = alt

        // @TODO dist in AU, any correlation with distance from calculation position?
        planetPosition.au = dist

        return planetPosition
    }
}