package org.example.homework.kafka;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static org.example.homework.kafka.DateTimeParser.parseDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeParserTest {
    @Test
    public void checkDateTimeParsing() {
        assertEquals(LocalDateTime.of(2022, 5, 26, 11, 32, 12, 45 * 1000 * 1000).atZone(UTC).toInstant(),
                parseDateTime("20220526", "113212045"));
    }

}
