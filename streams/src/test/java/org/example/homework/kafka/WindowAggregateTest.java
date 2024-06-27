package org.example.homework.kafka;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Properties;

import static java.time.LocalDateTime.of;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.Arrays.asList;


import org.apache.kafka.common.serialization.*;
import org.apache.kafka.test.TestUtils;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.test.TestRecord;
import org.example.homework.configuration.ConfigurationReader;
import org.junit.jupiter.api.Test;

import static org.example.homework.kafka.WindowAggregate.createTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * How to aggregate messages via `groupByKey()`, `windowedBy()`, `aggregate()` and `suppress`
 * to implement estimation of arithmetic sum of data.
 */
public class WindowAggregateTest {
    private static final String APPLICATION_NAME = "window-aggregation-test";
    private final static String TEST_CONFIG_FILE = "application.properties";

    @Test
    public void shouldCalculateWindowCount() throws IOException {
        var inputRecords = asList(
                // first window
                new TestRecord<>("some user agent-impressions", "4",
                        ins(2022, 5, 26, 11, 32, 12, 45)),
                // second window
                new TestRecord<>("some user agent-impressions", "7",
                        ins(2022, 5, 26, 19, 32, 4, 695)),
                new TestRecord<>("some user agent-impressions", "3",
                        ins(2022, 5, 26, 19, 32, 4, 903)),
                // third window
                new TestRecord<>("some user agent-clicks", "5",
                        ins(2022, 5, 27, 11, 31, 45, 108)),
                new TestRecord<>("some user agent-clicks", "5",
                        ins(2022, 5, 27, 11, 31, 45, 201)),
                // fourth window
                new TestRecord<>("some user agent-clicks", "7",
                        ins(2022, 5, 27, 12, 1, 43, 730)),
                new TestRecord<>("some user agent-clicks", "3",
                        ins(2022, 5, 27, 12, 1, 43, 900)),
                new TestRecord<>("some user agent-impressions", "4",
                        ins(2022, 5, 27, 12, 31, 54, 212)),
                new TestRecord<>("some user agent-impressions", "6",
                        ins(2022, 5, 27, 12, 31, 54, 402)),
                new TestRecord<>("some user agent-clicks", "7",
                        ins(2022, 5, 27, 12, 31, 54, 754)),
                new TestRecord<>("some user agent-clicks", "3",
                        ins(2022, 5, 27, 12, 31, 54, 813))
        );
        var expectedOutputRecords = asList(
                // first window aggregation
                new TestRecord<>("some user agent", "impressions-4",
                        ins(2022, 5, 26, 11, 32, 12, 45)),
                // second window aggregation
                new TestRecord<>("some user agent", "impressions-10",
                        ins(2022, 5, 26, 19, 32, 4, 903)),
                // third window aggregation
                new TestRecord<>("some user agent", "clicks-10",
                        ins(2022, 5, 27, 11, 31, 45, 201))
                // records from the fourth window are not taken into consideration since the window is not yet closed
                // (new records may be produced that affect the aggregation result)
        );

        // Step 1: Create the topology and its configuration.
        var config = this.getClass().getClassLoader()
                .getResourceAsStream(TEST_CONFIG_FILE);
        var props = ConfigurationReader.loadProperties(config);
        var configuration = ConfigurationReader.readConfiguration(props);
        var topology = createTopology(configuration);
        System.out.println("*** TOPOLOGY ***");
        System.out.println("Enter https://zz85.github.io/kafka-streams-viz/ to visualize");
        System.out.println(topology.describe());

        try (var topologyTestDriver = new TopologyTestDriver(topology,
                createTopologyConfiguration())) {
            // Step 2: Setup input and output topics.
            var input = topologyTestDriver
                    .createInputTopic(configuration.inputTopic(), new StringSerializer(), new StringSerializer());
            var output = topologyTestDriver
                    .createOutputTopic(configuration.outputTopic(), new StringDeserializer(), new StringDeserializer());
            // Step 2a: Setup changelog topic
            var changelog = topologyTestDriver.
                    createOutputTopic(APPLICATION_NAME +
                            "-" +
                            configuration.storeName() +
                            "-changelog", windowedStringDeserializer(configuration.windowSize()), new AggregationResultDeserializer());
            // Step 2: Write the input.
            input.pipeRecordList(inputRecords);
            // Step 3: Validate the output.
            var outputRecords = output.readRecordsToList();
            assertEquals(expectedOutputRecords, outputRecords);
            // Step 3: Display the store and output content.
            System.out.println("*** STORE ***");
            System.out.println(changelog.readRecordsToList());

            System.out.println("*** OUTPUT ***");
            System.out.println(outputRecords);

        }
    }

    public static Instant ins(int year, int month, int dayOfMonth, int hour, int minute, int second, int millis) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, millis * 1000 * 1000).atZone(UTC).toInstant();
    }

    private static Deserializer<Windowed<String>> windowedStringDeserializer(Duration windowSize) {
        var stringTimeWindowedDeserializer = new TimeWindowedDeserializer<>(new StringDeserializer(), windowSize.toMillis());
        stringTimeWindowedDeserializer.setIsChangelogTopic(true);
        return stringTimeWindowedDeserializer;
    }

    private Properties createTopologyConfiguration() {
        var properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Use a temporary directory for storing state, which will be automatically removed after the test.
        properties.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());
        return properties;
    }

}