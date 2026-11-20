package com.project.strategy;

import com.project.dto.GroupByResponse;
import com.project.exception.InvalidQueryException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupByStrategyTest {

    private final GroupByStrategy strategy = new GroupByStrategy();

    private final List< Map< String, Object > > sampleRecords = List.of(
        Map.of("id", 1, "name", "John Doe", "age", 30, "department", "Engineering"),
        Map.of("id", 2, "name", "Jane Smith", "age", 25, "department", "Engineering"),
        Map.of("id", 3, "name", "Alice Brown", "age", 28, "department", "Marketing")
    );

    @Test
    void shouldGroupRecordsByDepartment() {
        QueryCriteria criteria = new QueryCriteria("department", null, SortOrder.ASC);

        GroupByResponse response = strategy.execute(sampleRecords, criteria);

        assertThat(response.groupedRecords()).containsOnlyKeys("Engineering", "Marketing");
        assertThat(response.groupedRecords().get("Engineering")).hasSize(2);
        assertThat(response.groupedRecords().get("Marketing")).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenFieldDoesNotExist() {
        QueryCriteria criteria = new QueryCriteria("nonExistent", null, SortOrder.ASC);

        assertThatThrownBy(() -> strategy.execute(sampleRecords, criteria))
            .isInstanceOf(InvalidQueryException.class)
            .hasMessageContaining("nonExistent");
    }
}