package org.example.homework.data;

import java.util.Map;

public record FileData(FileName fileName, Map<String, Long> userAgentCounts){}