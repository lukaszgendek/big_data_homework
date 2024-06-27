package org.example.homework.file;

import org.example.homework.data.FileData;
import org.example.homework.kafka.PathAndFileName;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.homework.parquet.ParquetUserAgentsRetriever.extractUserAgents;

public class FileDataReader {

    public static FileData readFileData(PathAndFileName p, Optional<String> userAgent) throws IOException {
        var userAgents = extractUserAgents(p.path().toAbsolutePath().toString());
        var filteredUserAgents = getFilteredUserAgents(userAgent, userAgents);

        return new FileData(p.fileName(), countAggregation(filteredUserAgents));
    }

    private static List<String> getFilteredUserAgents(Optional<String> userAgent, List<String> userAgents) {
        return userAgent.map(s -> userAgents.stream().filter(
                s::equals
        ).collect(Collectors.toList())).orElse(userAgents);
    }

    private static Map<String, Long> countAggregation(List<String> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

}
