package org.example.homework.kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.example.homework.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWritingRecordsHandler implements ConsumerRecordsHandler<String, String> {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private final Path path;

    public FileWritingRecordsHandler(final Path path) {
        this.path = path;
    }

    @Override
    public void process(final ConsumerRecords<String, String> consumerRecords) {
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            log.info("Message read {} {} {} {}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value(), Instant.ofEpochMilli(consumerRecord.timestamp()));
        }
        try {
            Files.write(path, getFileEntries(consumerRecords), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getFileEntries(ConsumerRecords<String, String> consumerRecords) {
        return StreamSupport.stream(consumerRecords.spliterator(), false).map(
                consumerRecord -> "topic: " + consumerRecord.topic() + ", partition: " + consumerRecord.partition()
                        + ", offset " + consumerRecord.offset() + ", key: " + consumerRecord.key() + ", value: " +
                        consumerRecord.value() + ", timestamp " + Instant.ofEpochMilli(consumerRecord.timestamp())
        ).collect(Collectors.toList());
    }
}
