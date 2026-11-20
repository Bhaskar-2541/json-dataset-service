package com.project.strategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.dto.GroupByResponse;
import com.project.exception.InvalidQueryException;

@Component
public class GroupByStrategy implements QueryOperationStrategy {

    @Override
    public boolean supports(QueryCriteria criteria) {
        return criteria.hasGroupBy() && !criteria.hasSortBy();
    }

    @Override
    public GroupByResponse execute(List< Map<String,Object>> records, QueryCriteria criteria) {
        String field = criteria.groupBy().trim();

        boolean fieldExists = records.stream().anyMatch(record -> record.containsKey(field));
        if (!fieldExists) {
            throw new InvalidQueryException(
                "Field '" + field + "' does not exist in any record of this dataset."
            );
        }

        Map<String, List<Map<String,Object>>> grouped = records.stream()
            .collect(Collectors.groupingBy(
                record -> {
                    Object value = record.get(field);
                    return value == null ? "null" : String.valueOf(value);
                },
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return new GroupByResponse(grouped);
    }
}