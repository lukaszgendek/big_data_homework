package org.example.homework;

import org.example.homework.kafka.ConsumerRecordsHandler;

import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.example.homework.kafka.FileWritingRecordsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private boolean keepConsuming = true;
    private final ConsumerRecordsHandler<String, String> recordsHandler;
    private final Consumer<String, String> consumer;

    public Main(final Consumer<String, String> consumer,
                final ConsumerRecordsHandler<String, String> recordsHandler) {
        this.consumer = consumer;
        this.recordsHandler = recordsHandler;
    }

    public void runConsume(final Properties consumerProps) {
        try {
            String consumerTopicName = consumerProps.getProperty("consumer_topic_names");
            List<String> consumerTopicNames = Arrays.asList(consumerTopicName.split(","));
            log.info("subscribing to {}", consumerTopicNames);
            consumer.subscribe(consumerTopicNames);
            while (keepConsuming) {
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
                log.info("received {}", consumerRecords.count());
                recordsHandler.process(consumerRecords);
            }
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        keepConsuming = false;
    }

    public static Properties loadProperties(String fileName) throws IOException {
        final Properties props = new Properties();
        final FileInputStream input = new FileInputStream(fileName);
        props.load(input);
        input.close();
        return props;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "This program takes one argument: the path to an environment configuration file.");
        }
        log.info("Application started arg. Config file {}", args[0]);

        final Properties consumerAppProps = Main.loadProperties(args[0]);
        final String filePath = consumerAppProps.getProperty("file_path");
        final Consumer<String, String> consumer = new KafkaConsumer<>(consumerAppProps);
        final ConsumerRecordsHandler<String, String> recordsHandler = new FileWritingRecordsHandler(Paths.get(filePath));
        final Main consumerApplication = new Main(consumer, recordsHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(consumerApplication::shutdown));

        consumerApplication.runConsume(consumerAppProps);
    }

}
