package org.example.homework;

import org.apache.kafka.streams.KafkaStreams;
import org.example.homework.configuration.ConfigurationReader;
import org.example.homework.kafka.WindowAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "This program takes one argument: the path to an environment configuration file.");
        }
        System.out.println("Application started arg " + args[0]);
        var props = ConfigurationReader.loadProperties(args[0]);
        var configuration = ConfigurationReader.readConfiguration(props);
        var topology = WindowAggregate.createTopology(configuration);
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }
}