package org.example.homework.file;

import org.example.homework.data.FileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class FileNameParser {
    public static final int DATETIME_PART = 3;
    private static final Logger log = LoggerFactory.getLogger(FileNameParser.class);

    @Nullable
    public static FileName parseFileName(String fileName) {
        var parts = fileName.split("_");
        if (parts.length < DATETIME_PART) {
            log.error("Invalid file format " + fileName + ". Could not resolve datetime part");
            return null;
        }
        var datetimePart = parts[DATETIME_PART];
        if (datetimePart.length() < 8) {
            log.error("Invalid file format " + fileName + ". Could not extract date and time");
            return null;
        }
        var date = datetimePart.substring(0, 8);
        var time = datetimePart.substring(8);
        if (fileName.startsWith("impressions")) {
            return new FileName(date, time, "impressions");
        }
        else if (fileName.startsWith("clicks"))
            return new FileName(date, time, "clicks");
        else {
            log.error("Invalid file format " + fileName + " Neither impressions nor clicks");
            return null;
        }
    }

}
