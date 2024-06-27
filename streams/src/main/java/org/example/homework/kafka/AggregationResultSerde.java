package org.example.homework.kafka;

import org.apache.kafka.common.serialization.Serdes;

public class AggregationResultSerde extends Serdes.WrapperSerde<AggregationResult> {
    public AggregationResultSerde() {
        super(new AggregationResultSerializer(), new AggregationResultDeserializer());
    }
}