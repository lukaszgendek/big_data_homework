package org.example.homework.kafka;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.KeyValue;
import org.example.homework.configuration.ConfigurationReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class KafkaSenderTest {

    private final static String TEST_CONFIG_FILE = "application.properties";

    @Test
    public void testProduce() throws IOException {
        var stringSerializer = new StringSerializer();
        var mockProducer = new MockProducer<>(true, stringSerializer, stringSerializer);
        var config = this.getClass().getClassLoader()
                .getResourceAsStream(TEST_CONFIG_FILE);
        var props = ConfigurationReader.loadProperties(config);
        var configuration = ConfigurationReader.readConfiguration(props);
        var producerApp = new KafkaSender(mockProducer, configuration.producerTopicName());
        var records = Arrays.asList("foo-bar", "bar-foo", "baz-bar", "great-weather");

        records.forEach(record -> producerApp.produce(record.split("-")[0], record.split("-")[1]));

        var expectedList = Arrays.asList(KeyValue.pair("foo", "bar"),
                KeyValue.pair("bar", "foo"),
                KeyValue.pair("baz", "bar"),
                KeyValue.pair("great", "weather"));

        var actualList = mockProducer.history().stream().map(this::toKeyValue).collect(Collectors.toList());

        assertEquals(actualList, expectedList);
        producerApp.shutdown();
    }


    private KeyValue<String, String> toKeyValue(final ProducerRecord<String, String> producerRecord) {
        return KeyValue.pair(producerRecord.key(), producerRecord.value());
    }
}