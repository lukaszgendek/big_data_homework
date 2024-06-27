package org.example.homework.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Future;


public class KafkaSender {

    private final Producer<String, String> producer;
    private final String outTopic;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public KafkaSender(final Producer<String, String> producer,
                       final String topic) {
        this.producer = producer;
        this.outTopic = topic;
    }

    public Future<RecordMetadata> produce(String key, String value, Instant instant) {
        log.info("Sending kafka message: key: {}, message: {}, timestamp: {}", key, value, instant.toEpochMilli());
        return producer.send(new ProducerRecord<>(outTopic, null, instant.toEpochMilli(), key, value));
    }

    public Future<RecordMetadata> produce(String key, String value) {
        return producer.send(new ProducerRecord<>(outTopic, key, value));
    }

    public void shutdown() {
        producer.close();
    }
}
