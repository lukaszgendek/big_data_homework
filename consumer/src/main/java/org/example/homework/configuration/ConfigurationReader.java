package org.example.homework.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Properties;

public class ConfigurationReader {
    public static Properties loadProperties(String fileName) throws IOException {
        final FileInputStream input = new FileInputStream(fileName);
        return loadProperties(input);
    }

    public static Properties loadProperties(InputStream input) throws IOException {
        final Properties envProps = new Properties();
        envProps.load(input);
        input.close();

        return envProps;
    }
    public static Configuration readConfiguration(Properties properties) {
        String inputTopic = getStringProperty(properties, "input_topic");
        String outputTopic = getStringProperty(properties, "output_topic");
        Duration windowSize = getDurationProperty(properties, "window_size");
        String storeName = getStringProperty(properties, "output_topic");
        return new Configuration(inputTopic, outputTopic, windowSize, storeName);
    }

    private static String getStringProperty(Properties properties, String paramName) {
        String value = properties.getProperty(paramName);
        if (value == null)
            throw new IllegalArgumentException("Missing parameter " + paramName);
        return value;
    }

    private static Duration getDurationProperty(Properties properties, String paramName) {
        String text = getStringProperty(properties, paramName);
        try {
            return Duration.parse(text);
        } catch (DateTimeParseException e){
            throw new IllegalArgumentException("Could not parse Duration parameter " + text, e);
        }
    }
}
