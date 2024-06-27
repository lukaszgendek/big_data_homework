package org.example.homework.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.example.homework.Main;
import org.example.homework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.time.Instant.ofEpochSecond;


import static org.example.homework.kafka.AggregationResultDeserializer.deserialize;
import static org.example.homework.kafka.AggregationResultSerializer.serializeToString;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * How to aggregate messages via `groupByKey()`, `windowedBy()`, `aggregate()` and `suppress`
 * to implement estimation of arithmetic average of data.
 */
public class WindowAggregate {
    public static Topology createTopology(Configuration configuration) {
        var builder = new StreamsBuilder();
        builder.stream(configuration.inputTopic(),
                        Consumed.with(Serdes.String(), Serdes.String())
                                .withName("READ_COUNT"))
                // Group the records based on the existing key of records.
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(configuration.windowSize()))
                // For each key, aggregate the record values
                .aggregate(() -> new AggregationResult(BigInteger.ZERO),
                        (key, newValue, oldValue)
                                -> addNewRecord(newValue, oldValue),
                        Named.as("AGGREGATE"),
                        Materialized.<String, AggregationResult, WindowStore<Bytes, byte[]>>as(configuration.storeName())
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new AggregationResultSerde())

                )
                .suppress(Suppressed.untilWindowCloses(
                                Suppressed.BufferConfig.unbounded().shutDownWhenFull())
                        .withName("SUPPRESS"))
                .toStream(Named.as("CONVERT_TABLE_TO_STREAM"))

                .map((key, value) -> new KeyValue<>(firstToken(key.key()), secondToken(key.key()) + "-" + serializeToString(value)), Named.as("EXTRACT_KEY")).to(configuration.outputTopic(), Produced.with(Serdes.String(), Serdes.String()).withName("WRITE_TOTAL_COUNT_PER_HOUR"));
        return builder.build();
    }

    private static String secondToken(String key) {
        int i = key.indexOf('-');
        if (i == -1)
            return key;
        return key.substring(i + 1);
    }

    private static String firstToken(String key) {
        int i = key.indexOf('-');
        if (i == -1)
            return key;
        return key.substring(0, i);
    }

    private static AggregationResult addNewRecord(String newString, AggregationResult aggregation) {
        AggregationResult newValue = deserialize(newString);
        BigInteger total = aggregation.total();
        return new AggregationResult(total.add(newValue.total()));
    }

}