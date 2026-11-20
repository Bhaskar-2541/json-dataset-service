package com.project.strategy;

import com.project.dto.SortByResponse;
import com.project.exception.InvalidQueryException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortByStrategyTest {

    private final SortByStrategy strategy = new SortByStrategy();

    private final List< Map< String, Object > > sampleRecords = List.of(
        Map.of("id", 1, "name", "John Doe", "age", 30),
        Map.of("id", 2, "name", "Jane Smith", "age", 25),
        Map.of("id", 3, "name", "Alice Brown", "age", 100)
    );

    @Test
    void shouldSortNumericallyAscending() {
        QueryCriteria criteria = new QueryCriteria(null, "age", SortOrder.ASC);

        SortByResponse response = strategy.execute(sampleRecords, criteria);

        assertThat(response.sortedRecords())
            .extracting(r -> r.get("age"))
            .containsExactly(25, 30, 100);
    }

    @Test
    void shouldSortNumericallyDescending() {
        QueryCriteria criteria = new QueryCriteria(null, "age", SortOrder.DESC);

        SortByResponse response = strategy.execute(sampleRecords, criteria);

        assertThat(response.sortedRecords())
            .extracting(r -> r.get("age"))
            .containsExactly(100, 30, 25);
    }

    @Test
    void shouldPlaceNullValuesLast() {
        Map< String, Object > missingAge = new HashMap<>();
        missingAge.put("id", 4);
        missingAge.put("name", "Bob");
        missingAge.put("age", null);

        List< Map< String, Object > > records = List.of(
            missingAge,
            Map.of("id", 1, "name", "John", "age", 30),
            Map.of("id", 2, "name", "Jane", "age", 25)
        );

        SortByResponse response = strategy.execute(records, new QueryCriteria(null, "age", SortOrder.ASC));

        assertThat(response.sortedRecords())
            .extracting(r -> r.get("age"))
            .containsExactly(25, 30, null);
    }

    @Test
    void shouldThrowExceptionWhenSortFieldDoesNotExist() {
        QueryCriteria criteria = new QueryCriteria(null, "salary", SortOrder.ASC);

        assertThatThrownBy(() -> strategy.execute(sampleRecords, criteria))
            .isInstanceOf(InvalidQueryException.class)
            .hasMessageContaining("salary");
    }
}