package com.kbapps.ephemeris.planet

import com.kbapps.ephemeris.Constants
import com.kbapps.ephemeris.type.ElevationType
import com.kbapps.ephemeris.type.TwilightType
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class Lunar : PlanetModel() {
    override fun twilights(): Map<TwilightType, Array<ElevationType>> {
        return emptyMap()
    }

    public override fun calculateGeoPosition(jdTCorr: Double): DoubleArray {
        val sAnomaly = Solar.getSAnomaly(jdTCorr)

        //val lon = sAnomaly[0]
        val sanomaly = sAnomaly[1]

        // MOON PARAMETERS (Formulae from "Calendrical Calculations")
        val phase =
            Calculator.normalizeRadians((297.8502042 + 445267.1115168 * jdTCorr - 0.00163 * jdTCorr * jdTCorr + jdTCorr * jdTCorr * jdTCorr / 538841 - jdTCorr * jdTCorr * jdTCorr * jdTCorr / 65194000) * Constants.DEG_TO_RAD)

        // Anomalistic phase
        val anomaly =
            (134.9634114 + 477198.8676313 * jdTCorr + .008997 * jdTCorr * jdTCorr + jdTCorr * jdTCorr * jdTCorr / 69699 - jdTCorr * jdTCorr * jdTCorr * jdTCorr / 14712000) * Constants.DEG_TO_RAD

        // Degrees from ascending node
        val node =
            (93.2720993 + 483202.0175273 * jdTCorr - 0.0034029 * jdTCorr * jdTCorr - jdTCorr * jdTCorr * jdTCorr / 3526000 + jdTCorr * jdTCorr * jdTCorr * jdTCorr / 863310000) * Constants.DEG_TO_RAD

        val e = 1.0 - (.002495 + 7.52E-06 * (jdTCorr + 1.0)) * (jdTCorr + 1.0)

        // Now longitude, with the three main correcting terms of evection,
        // variation, and equation of year, plus other terms (error<0.01 deg)
        // P. Duffet's MOON program taken as reference
        var l =
            218.31664563 + 481267.8811958 * jdTCorr - .00146639 * jdTCorr * jdTCorr + jdTCorr * jdTCorr * jdTCorr / 540135.03 - jdTCorr * jdTCorr * jdTCorr * jdTCorr / 65193770.4
        l += 6.28875 * sin(anomaly) + 1.274018 * sin(2 * phase - anomaly) + .658309 * sin(2 * phase)
        l += 0.213616 * sin(2 * anomaly) - e * .185596 * sin(sanomaly) - 0.114336 * sin(2 * node)
        l += .058793 * sin(2 * phase - 2 * anomaly) + .057212 * e * sin(2 * phase - anomaly - sanomaly) + .05332 * sin(
            2 * phase + anomaly
        )
        l += .045874 * e * sin(2 * phase - sanomaly) + .041024 * e * sin(anomaly - sanomaly) - .034718 * sin(
            phase
        ) - e * .030465 * sin(sanomaly + anomaly)
        l += .015326 * sin(2 * (phase - node)) - .012528 * sin(2 * node + anomaly) - .01098 * sin(2 * node - anomaly) + .010674 * sin(
            4 * phase - anomaly
        )
        l += .010034 * sin(3 * anomaly) + .008548 * sin(4 * phase - 2 * anomaly)
        l += -e * .00791 * sin(sanomaly - anomaly + 2 * phase) - e * .006783 * sin(2 * phase + sanomaly) + .005162 * sin(
            anomaly - phase
        ) + e * .005 * sin(sanomaly + phase)
        l += .003862 * sin(4 * phase) + e * .004049 * sin(anomaly - sanomaly + 2 * phase) + .003996 * sin(
            2 * (anomaly + phase)
        ) + .003665 * sin(2 * phase - 3 * anomaly)
        var longitude = l

        // Let's add nutation here also
        val m1 = (124.90 - 1934.134 * jdTCorr + 0.002063 * jdTCorr * jdTCorr) * Constants.RAD_TO_DEG
        val m2 = (201.11 + 72001.5377 * jdTCorr + 0.00057 * jdTCorr * jdTCorr) * Constants.RAD_TO_DEG
        val d = -.0047785 * sin(m1) - .0003667 * sin(m2)
        longitude += d

        // Now Moon parallax
        var parallax = .950724 + .051818 * cos(anomaly) + .009531 * cos(2 * phase - anomaly)
        parallax += .007843 * cos(2 * phase) + .002824 * cos(2 * anomaly)
        parallax += 0.000857 * cos(2 * phase + anomaly) + e * .000533 * cos(2 * phase - sanomaly)
        parallax += e * .000401 * cos(2 * phase - anomaly - sanomaly) + e * .00032 * cos(anomaly - sanomaly) - .000271 * cos(
            phase
        )
        parallax += -e * .000264 * cos(sanomaly + anomaly) - .000198 * cos(2 * node - anomaly)

        // So Moon distance in Earth radii is, more or less,
        val distance = 1.0 / sin(parallax * Constants.DEG_TO_RAD)

        // Ecliptic latitude with nodal phase (error<0.01 deg)
        l = 5.128189 * sin(node) + 0.280606 * sin(node + anomaly) + 0.277693 * sin(anomaly - node)
        l += .173238 * sin(2 * phase - node) + .055413 * sin(2 * phase + node - anomaly)
        l += .046272 * sin(2 * phase - node - anomaly) + .032573 * sin(2 * phase + node)
        l += .017198 * sin(2 * anomaly + node) + .009267 * sin(2 * phase + anomaly - node)
        l += .008823 * sin(2 * anomaly - node) + e * .008247 * sin(2 * phase - sanomaly - node) + .004323 * sin(
            2 * (phase - anomaly) - node
        )
        l += .0042 * sin(2 * phase + node + anomaly) + e * .003372 * sin(node - sanomaly - 2 * phase)
        val latitude = l

        return doubleArrayOf(
            longitude,
            latitude,
            distance * Constants.EARTH_RADIUS / Constants.AU,
            atan(1737.4 / (distance * Constants.EARTH_RADIUS))
        )
    }

    override fun calculationAccuracy(): Int {
        return 7
    }
}
