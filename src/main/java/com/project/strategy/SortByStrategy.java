package com.project.strategy;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.project.dto.SortByResponse;
import com.project.exception.InvalidQueryException;

@Component
public class SortByStrategy implements QueryOperationStrategy {

    @Override
    public boolean supports(QueryCriteria criteria) {
        return criteria.hasSortBy() && !criteria.hasGroupBy();
    }

    @Override
    public SortByResponse execute(List< Map< String, Object>> records, QueryCriteria criteria) {
        String field = criteria.sortBy().trim();

        boolean fieldExists = records.stream().anyMatch(record -> record.containsKey(field));
        if (!fieldExists) {
            throw new InvalidQueryException(
                "Field '" + field + "' does not exist in any record of this dataset."
            );
        }

        Comparator<Object> valueComparator = (v1, v2) -> {
            if (v1 instanceof Number n1 && v2 instanceof Number n2) {
                return new BigDecimal(n1.toString()).compareTo(new BigDecimal(n2.toString()));
            }
            if (v1 instanceof Boolean b1 && v2 instanceof Boolean b2) {
                return Boolean.compare(b1, b2);
            }
            return String.valueOf(v1).compareToIgnoreCase(String.valueOf(v2));
        };

        if (criteria.order() == SortOrder.DESC) {
            valueComparator = valueComparator.reversed();
        }

        Comparator<Map<String, Object>> recordComparator =
            Comparator.comparing(record -> record.get(field), Comparator.nullsLast(valueComparator));

        List<Map<String,Object>> sorted = records.stream()
            .sorted(recordComparator)
            .toList();

        return new SortByResponse(sorted);
    }
}