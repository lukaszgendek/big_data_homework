package org.example.homework.configuration;

import java.time.Duration;

public record Configuration(String inputTopic, String outputTopic, Duration windowSize, String storeName) {}