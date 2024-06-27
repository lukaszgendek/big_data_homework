package org.example.homework.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HexFormat;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AggregationResultDeserializer implements Deserializer<AggregationResult> {
    private static final Logger log = LoggerFactory.getLogger(AggregationResultDeserializer.class);

    @Override
    public AggregationResult deserialize(String s, byte[] bytes) {
        try {
            return deserialize(new String(bytes, UTF_8));
        } catch (Exception e) {
            log.error("Error when deserializing a message {}", HexFormat.of().formatHex(bytes), e);
            return new AggregationResult(BigInteger.ZERO);
        }
    }

    public static AggregationResult deserialize(String string) {
        try {
            return new AggregationResult(new BigInteger(string));
        } catch (NumberFormatException e) {
            log.error("Error when deserializing a message {}", string, e);
            return new AggregationResult(BigInteger.ZERO);
        }
    }

}
