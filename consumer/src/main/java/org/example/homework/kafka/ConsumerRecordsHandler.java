package org.example.homework.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface ConsumerRecordsHandler<K, V> {
    void process(ConsumerRecords<K, V> consumerRecords);
}