package com.kbapps.ephemeris.type

enum class TwilightType {
    /**
     * Event ID for calculation of rising and setting times for astronomical
     * twilight. In this case, the calculated time will be the time when the
     * center of the object is at -18 degrees of geometrical elevation below the
     * astronomical horizon. At this time astronomical observations are possible
     * because the sky is dark enough.
     */
    ASTRONOMICAL,
    /**
     * Event ID for calculation of rising and setting times for nautical
     * twilight. In this case, the calculated time will be the time when the
     * center of the object is at -12 degrees of geometric elevation below the
     * astronomical horizon.
     */
    NAUTICAL,
    /**
     * Event ID for calculation of rising and setting times for civil twilight.
     * In this case, the calculated time will be the time when the center of the
     * object is at -6 degrees of geometric elevation below the astronomical
     * horizon.
     */
    CIVIL,

    /**
     * The golden hour is the period of time the color of the sky goes from red and orange to yellow or, as its name
     * suggests, golden tones, having a warm color temperature. Lighting is soft, diffused and with little contrast,
     * since the sun is low in the sky.
     *
     * @link http://www.photopills.com/articles/understanding-golden-hour-blue-hour-and-twilights
     */
    GOLDEN_HOURS,

    /**
     * During the blue hour the sky has a deep blue hue with a cold color temperature and saturated colors.
     * At the beginning (evening) and at the end (morning), a gradient of colors, from blue to orange, can be seen
     * right in the place of sunset and sunrise.
     *
     * @link http://www.photopills.com/articles/understanding-golden-hour-blue-hour-and-twilights
     */
    BLUE_HOURS
}
