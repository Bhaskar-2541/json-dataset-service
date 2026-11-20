package com.project.dto;

import java.util.List;
import java.util.Map;

public record SortByResponse(List<Map<String,Object>> sortedRecords
		) {}
