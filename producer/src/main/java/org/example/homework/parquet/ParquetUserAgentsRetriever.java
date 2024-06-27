package org.example.homework.parquet;

import org.apache.parquet.example.data.simple.SimpleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParquetUserAgentsRetriever {

    public static final String DEVICE_SETTINGS_KEY = "device_settings";
    public static final String USER_AGENT_KEY = "user_agent";

    public static List<String> extractUserAgents(String filePath) throws IOException {
        var entries = new ArrayList<String>();
        var parquet = ParquetReaderUtils.getParquetData(filePath);
        var data = parquet.data();
        for (var simpleGroup : data) {
            var deviceSettings = (SimpleGroup) simpleGroup.getGroup(DEVICE_SETTINGS_KEY, 0);
            var count = deviceSettings.getFieldRepetitionCount(USER_AGENT_KEY);
            if (count > 0)
                entries.add(deviceSettings.getString(USER_AGENT_KEY, 0));
        }
        return entries;
    }
}
