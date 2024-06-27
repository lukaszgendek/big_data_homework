package org.example.homework.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class ConfigurationReader {
    public static Properties loadProperties(String fileName) throws IOException {
        var input = new FileInputStream(fileName);
        return loadProperties(input);
    }

    public static Properties loadProperties(InputStream input) throws IOException {
        var envProps = new Properties();
        envProps.load(input);
        input.close();

        return envProps;
    }
    public static Configuration readConfiguration(Properties properties) {
        var inputDir = getStringProperty(properties, "input_dir");
        var processedDir = getStringProperty(properties, "processed_dir");
        var intervalPeriodMs = getLongProperty(properties, "interval_period_ms");
        var producerTopicName = getStringProperty(properties, "producer_topic_name");
        return new Configuration(inputDir, processedDir, intervalPeriodMs, producerTopicName,
                getStringOptionalProperty(properties, "user_agent"));
    }

    private static String getStringProperty(Properties properties, String paramName) {
        var value = properties.getProperty(paramName);
        if (value == null)
            throw new IllegalArgumentException("Missing parameter " + paramName);
        return value;
    }

    private static Optional<String> getStringOptionalProperty(Properties properties, String paramName) {
        var value = properties.getProperty(paramName);
        return (value != null) ? Optional.of(value) : Optional.empty();
    }

    private static long getLongProperty(Properties properties, String paramName) {
        var value = getStringProperty(properties, paramName);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Could not parse long parameter " + paramName, e);
        }
    }
}
