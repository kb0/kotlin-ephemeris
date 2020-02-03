package com.kbapps.ephemeris

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.floor

object JulianDate {
    fun toJD(dateTime: ZonedDateTime): DoubleArray {
        return toJD(LocalDateTime.ofInstant(dateTime.toInstant(), ZoneOffset.UTC))
    }

    fun toJD(dateTime: LocalDateTime): DoubleArray {
        return toJD(
            dateTime.year,
            dateTime.monthValue,
            dateTime.dayOfMonth,
            dateTime.hour,
            dateTime.minute,
            dateTime.second
        )
    }

    @Throws(Exception::class)
    fun toJD(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DoubleArray {

        // The conversion formulas are from Meeus, chapter 7.
        var m = month
        var y = year
        if (m < 3) {
            y--
            m += 12
        }

        val a = y / 100
        val b = 2 - a + a / 4

        val dayFraction = (hour + (minute + second / 60.0) / 60.0) / 24.0

        val jd =
            dayFraction + (365.25 * (y + 4716)).toInt().toDouble() + (30.6001 * (m + 1)).toInt().toDouble() + day.toDouble() + b.toDouble() - 1524.5

        if (jd < 2299160.0 && jd >= 2299150.0) {
            throw Exception("invalid julian day $jd.")
        }

        var ttMinusUT = 0.0
        if (year > -600 && year < 2200) {
            val x = year + (month - 1 + day / 30.0) / 12.0
            val x2 = x * x
            val x3 = x2 * x
            val x4 = x3 * x

            ttMinusUT = if (year < 1600) {
                10535.328003326353 - 9.995238627481024 * x + 0.003067307630020489 * x2 - 7.76340698361363E-6 * x3 + 3.1331045394223196E-9 * x4 +
                        8.225530854405553E-12 * x2 * x3 - 7.486164715632051E-15 * x4 * x2 + 1.9362461549678834E-18 * x4 * x3 - 8.489224937827653E-23 * x4 * x4
            } else {
                -1027175.3477559977 + 2523.256625418965 * x - 1.885686849058459 * x2 + 5.869246227888417E-5 * x3 + 3.3379295816475025E-7 * x4 +
                        1.7758961671447929E-10 * x2 * x3 - 2.7889902806153024E-13 * x2 * x4 + 1.0224295822336825E-16 * x3 * x4 - 1.2528102370680435E-20 * x4 * x4
            }
        }

        return doubleArrayOf(jd, ttMinusUT)
    }

    /**
     * Transforms a Julian day to a common date.
     *
     * @param jd The Julian day.
     * @return A set of integers: year, month, day, hour, minute, second.
     * @throws Exception If the input date does not exists.
     */
    fun toUTC(jd: Double): LocalDateTime {
        if (jd < 2299160.0 && jd >= 2299150.0) {
            throw Exception("Invalid julian day $jd. This date does not exist.")
        }

        // The conversion formulas are from Meeus, chapter 7.
        val z = floor(jd + 0.5)
        val f = jd + 0.5 - z
        var a = z
        if (z >= 2299161) {
            val aA = ((z - 1867216.25) / 36524.25).toInt()
            a += 1 + aA - aA / 4
        }
        val b = a + 1524
        val c = ((b - 122.1) / 365.25).toInt()
        val d = (c * 365.25).toInt()
        val e = ((b - d) / 30.6001).toInt()

        val exactDay = f + b - d - (30.6001 * e).toInt()
        val day = exactDay.toInt()
        val month = if (e < 14) e - 1 else e - 13
        val year = if (month > 2) (c - 4715) - 1 else (c - 4715)
        val h = ((exactDay - day) * Constants.SECONDS_PER_DAY) / 3600.0

        val hour = h.toInt()
        val m = (h - hour) * 60.0
        val minute = m.toInt()
        val second = ((m - minute) * 60.0).toInt()

        return LocalDateTime.of(year, month, day, hour, minute, second)
    }

    fun tFromJD(jd: Double, ttMinusUT: Double): Double {
        return (jd + ttMinusUT / Constants.SECONDS_PER_DAY - Constants.J2000) / Constants.JULIAN_DAYS_PER_CENTURY
    }

    fun toUTC(jd: Double?): ZonedDateTime? {
        if (jd == null) {
            return null
        }

        return toUTC(jd).atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneOffset.UTC)
    }

    fun toTimeString(jd: Double?, zoneOffset: ZoneOffset, is12HoursFormat: Boolean): String {
        return toTimeString(jd, zoneOffset, is12HoursFormat, false)
    }

    fun toTimeString(jd: Double?, zoneOffset: ZoneOffset, is12HoursFormat: Boolean, withSeconds: Boolean): String {
        if (jd == null) {
            return ""
        }

        val date = toUTC(jd).atZone(ZoneOffset.UTC)
            .withZoneSameInstant(zoneOffset)

        if (is12HoursFormat) {
            return date.format(DateTimeFormatter.ofPattern(if (withSeconds) "hh:mm:ss a" else "hh:mm a"))
        }

        return date.format(DateTimeFormatter.ofPattern(if (withSeconds) "HH:mm:ss" else "HH:mm"))
    }
}