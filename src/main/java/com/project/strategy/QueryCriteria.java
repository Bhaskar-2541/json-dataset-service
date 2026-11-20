package com.project.strategy;

public record QueryCriteria(
    String groupBy,
    String sortBy,
    SortOrder order
) {
    public boolean hasGroupBy() {
        return groupBy != null && !groupBy.isBlank();
    }

    public boolean hasSortBy() {
        return sortBy != null && !sortBy.isBlank();
    }
}