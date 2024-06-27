package org.example.homework.kafka;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

public class DateTimeParser {
    public static Instant parseDateTime(String date, String time) {
        return LocalDateTime.of(LocalDate.parse(date, BASIC_ISO_DATE), parseTime(time)).atZone(UTC).toInstant();
    }

    private static LocalTime parseTime(String time) {
        return LocalTime.parse(time, new DateTimeFormatterBuilder()
                .appendValue(HOUR_OF_DAY, 2)
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .appendFraction(NANO_OF_SECOND, 0, 9, false)
                .toFormatter());
    }
}
