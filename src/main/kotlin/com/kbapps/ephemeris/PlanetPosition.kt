package com.kbapps.ephemeris

data class PlanetPosition(
    val jd: Double,
    val jdCoef: Double,

    val observerLongitudeInRad: Double,
    val observerLatitudeInRad: Double,

    // Greenwich Mean Sidereal Time
    var gmst: Double = 0.0,

    var sLongitude: Double = 0.0,
    var sLatitude: Double = 0.0,
    var sDistance: Double = 0.0,

    var rightAscension: Double = 0.0,
    var declination: Double = 0.0,
    var au: Double = 0.0,

    var azimuthInRad: Double = 0.0,
    var elevationInRad: Double = 0.0,

    var angularRadius: Double = 0.0
) {

    val azimuthInDeg: Double
        get() = azimuthInRad * Constants.RAD_TO_DEG


    val elevationInDeg: Double
        get() = elevationInRad * Constants.RAD_TO_DEG


    val observerLongitudeInDeg: Double
        get() = observerLongitudeInRad * Constants.RAD_TO_DEG


    val observerLatitudeInDeg: Double
        get() = observerLatitudeInRad * Constants.RAD_TO_DEG

}
