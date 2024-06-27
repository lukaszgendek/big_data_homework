package org.example.homework.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AggregationResultSerializer implements Serializer<AggregationResult> {
    private static final Logger log = LoggerFactory.getLogger(AggregationResultSerializer.class);

    @Override
    public byte[] serialize(String s, AggregationResult data) {
        return serializeToString(data).getBytes(UTF_8);
    }

    public static String serializeToString(AggregationResult data) {
        return data.total().toString();
    }
}
