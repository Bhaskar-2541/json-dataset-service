package com.project.dto;

import java.util.List;
import java.util.Map;

public record GroupByResponse(
    Map<String, List<Map<String, Object>>> groupedRecords
) {}