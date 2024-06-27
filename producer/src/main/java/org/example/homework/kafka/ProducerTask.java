package org.example.homework.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.example.homework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.example.homework.file.FileNameParser.parseFileName;
import static org.example.homework.file.FileDataReader.readFileData;
import static org.example.homework.kafka.DateTimeParser.parseDateTime;

public class ProducerTask extends TimerTask {

    private final KafkaSender kafkaSender;
    private final String inputDirectory;
    private final String processedDirectory;
    private final Logger log = LoggerFactory.getLogger(ProducerTask.class);
    private final Optional<String> userAgent;

    public ProducerTask(Properties props, Configuration configuration) {
        this.inputDirectory = configuration.inputDir();
        this.processedDirectory = configuration.processedDir();
        this.userAgent = configuration.userAgent();
        this.kafkaSender = new KafkaSender(new KafkaProducer<>(props), configuration.producerTopicName());
    }

    @Override
    public void run() {
        try {
            log.info("Reading folder content ...");
            try (Stream<Path> stream = Files.list(Paths.get(inputDirectory))) {

                var paths = stream.toList();

                var pathAndFileNames = parseFileNamesAndSortByDateAndTime(paths);
                for (var pf : pathAndFileNames) {
                    log.info("Processing {}", pf.path().getFileName());

                    var fileData = readFileData(pf, userAgent);
                    var userAgentCounts = fileData.userAgentCounts();
                    for (var mapEntry : userAgentCounts.entrySet()) {
                        var messageKey = mapEntry.getKey() + "-" + fileData.fileName().kind();
                        var messageValue = Long.toString(mapEntry.getValue());
                        var info = fileData.fileName();
                        var instant = parseDateTime(info.date(), info.time());
                        log.info("Producing kafka message: key: {}, message: {}, timestamp: {}", messageKey, messageValue, instant);
                        kafkaSender.produce(messageKey, messageValue, instant).get();
                    }
                }
                for (Path p : paths) {
                    var resolved = Path.of(processedDirectory + "/" + p.getFileName());
                    log.info("Moving {} to {}", p, resolved);
                    Files.move(p, resolved, REPLACE_EXISTING);
                }
            }

        } catch (Throwable e) {
            log.error("Unexpected error", e);
        }

    }

    private static List<PathAndFileName> parseFileNamesAndSortByDateAndTime(List<Path> paths) {
        return paths.stream().map(p ->
                        new PathAndFileName(p, parseFileName(p.getFileName().toString()))).filter(
                        p -> p.fileName() != null
                ).sorted(Comparator.comparing(PathAndFileName::date).thenComparing(PathAndFileName::time))
                .collect(Collectors.toList());
    }

}
