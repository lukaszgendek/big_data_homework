package org.example.homework.configuration;

import java.util.Optional;

public record Configuration(String inputDir,
                            String processedDir,
                            long intervalPeriodMs,
                            String producerTopicName,
                            Optional<String> userAgent
) {
}
