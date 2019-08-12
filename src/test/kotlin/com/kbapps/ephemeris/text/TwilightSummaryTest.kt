package com.kbapps.ephemeris.text

import com.kbapps.ephemeris.planet.PlanetMilestone
import com.kbapps.ephemeris.planet.Solar
import com.kbapps.ephemeris.type.TwilightType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.text.IsEqualIgnoringWhiteSpace
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class TwilightSummaryTest {
    @ParameterizedTest
    @CsvFileSource(resources = ["/solar_twilight_data.csv"], numLinesToSkip = 1)
    internal fun calculateTwilight(
        date: String,
        latitude: Double,
        longitude: Double,
        riseAstronomical: String,
        riseNautical: String,
        riseCivil: String,
        riseBlueHours: String,
        riseGoldenHours: String,
        dropAstronomical: String,
        dropNautical: String,
        dropCivil: String,
        dropBlueHours: String,
        dropGoldenHours: String
    ) {

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val grDate = org.threeten.bp.ZonedDateTime.parse(date)

        val solarData = PlanetMilestone.from(Solar(), grDate, latitude, longitude)

        val twilights = TwilightSummaryText.twilight(solarData, TwilightType.values(), ZoneOffset.ofHours(3), false)

        assertEquals(5, twilights.size)

        assertThat(
            (twilights[TwilightType.ASTRONOMICAL] ?: error("no data"))[0],
            IsEqualIgnoringWhiteSpace(riseAstronomical)
        )
        assertThat(
            (twilights[TwilightType.ASTRONOMICAL] ?: error("no data"))[1],
            IsEqualIgnoringWhiteSpace(dropAstronomical)
        )
        assertThat((twilights[TwilightType.NAUTICAL] ?: error("no data"))[0], IsEqualIgnoringWhiteSpace(riseNautical))
        assertThat((twilights[TwilightType.NAUTICAL] ?: error("no data"))[1], IsEqualIgnoringWhiteSpace(dropNautical))
        assertThat((twilights[TwilightType.CIVIL] ?: error("no data"))[0], IsEqualIgnoringWhiteSpace(riseCivil))
        assertThat((twilights[TwilightType.CIVIL] ?: error("no data"))[1], IsEqualIgnoringWhiteSpace(dropCivil))
        assertThat(
            (twilights[TwilightType.BLUE_HOURS] ?: error("no data"))[0],
            IsEqualIgnoringWhiteSpace(riseBlueHours)
        )
        assertThat(
            (twilights[TwilightType.BLUE_HOURS] ?: error("no data"))[1],
            IsEqualIgnoringWhiteSpace(dropBlueHours)
        )
        assertThat(
            (twilights[TwilightType.GOLDEN_HOURS] ?: error("no data"))[0],
            IsEqualIgnoringWhiteSpace(riseGoldenHours)
        )
        assertThat(
            (twilights[TwilightType.GOLDEN_HOURS] ?: error("no data"))[1],
            IsEqualIgnoringWhiteSpace(dropGoldenHours)
        )
    }
}