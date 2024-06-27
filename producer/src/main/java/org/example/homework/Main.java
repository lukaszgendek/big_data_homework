package org.example.homework;

import org.example.homework.configuration.ConfigurationReader;
import org.example.homework.kafka.ProducerTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "This program takes one argument: the path to an environment configuration file.");
        }
        System.out.println("Application started arg " + args[0]);
        var props = ConfigurationReader.loadProperties(args[0]);
        var configuration = ConfigurationReader.readConfiguration(props);
        checkDirExists(configuration.processedDir());
        ProducerTask task = new ProducerTask(props, configuration);
        new Timer().scheduleAtFixedRate(task, 0, configuration.intervalPeriodMs());
        Runtime.getRuntime().addShutdownHook(new Thread(task::cancel));
    }

    private static void checkDirExists(String processedDir) {
        var processedDirectory = Paths.get(processedDir);
        if (!Files.exists(processedDirectory))
            throw new IllegalStateException("Folder " + processedDir + " does not exist.");
    }

}