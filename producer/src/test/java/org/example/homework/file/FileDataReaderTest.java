package org.example.homework.file;

import org.example.homework.data.FileData;
import org.example.homework.data.FileName;
import org.example.homework.kafka.PathAndFileName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.example.homework.file.FileDataReader.readFileData;
import static org.example.homework.file.FileNameParser.parseFileName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDataReaderTest {
    @Test
    public void checkDataReading() throws IOException {
        String filename = "clicks_processed_dk_20220527113145108_163644805-163644809_1.parquet";
        FileName fileName = parseFileName(filename);
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).getFile());
        Path path = file.toPath();
        FileData data = readFileData(new PathAndFileName(path, fileName), Optional.of("some user agent"));
        assertEquals("20220527", data.fileName().date());
        assertEquals("113145108", data.fileName().time());
        assertEquals("clicks", data.fileName().kind());
        HashMap<String, Long> expectedMap = new HashMap<>();
        expectedMap.put("some user agent", 5L);
        assertEquals(expectedMap, data.userAgentCounts());
    }
}
