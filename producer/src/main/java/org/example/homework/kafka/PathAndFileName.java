package org.example.homework.kafka;

import org.example.homework.data.FileName;

import java.nio.file.Path;

public record PathAndFileName(Path path, FileName fileName) {
    String date() {
        return fileName.date();
    }
    String time() {
        return fileName.time();
    }
}
