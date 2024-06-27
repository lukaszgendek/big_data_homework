package org.example.homework.file;

import org.example.homework.data.FileName;
import org.junit.jupiter.api.Test;

import static org.example.homework.file.FileNameParser.parseFileName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileNameParserTest {
    @Test
    public void checkFileNameParsing() {
        String filename = "clicks_processed_dk_20220527113145108_163644805-163644809_1.parquet";
        FileName fileName = parseFileName(filename);
        assertEquals("20220527", fileName.date());
        assertEquals("113145108", fileName.time());
        assertEquals("clicks", fileName.kind());
    }
}
