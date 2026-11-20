package com.project.strategy;

import com.project.exception.InvalidQueryException;

public enum SortOrder {

	ASC,
    DESC;

    public static SortOrder from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        return switch (value.trim().toLowerCase()) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw new InvalidQueryException(
                "Invalid sort order: '" + value + "'. Supported values are 'asc' and 'desc'."
            );
        };
    }
}
