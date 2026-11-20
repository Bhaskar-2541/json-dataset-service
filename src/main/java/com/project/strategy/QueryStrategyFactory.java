package com.project.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.exception.InvalidQueryException;

@Component
public class QueryStrategyFactory {

    private final List<QueryOperationStrategy> strategies;

    public QueryStrategyFactory(List<QueryOperationStrategy> strategies) {
        this.strategies = strategies;
    }

    public QueryOperationStrategy getStrategy(QueryCriteria criteria) {
        if (!criteria.hasGroupBy() && !criteria.hasSortBy()) {
            throw new InvalidQueryException(
                "Missing query parameter. Please provide either 'groupBy' or 'sortBy'."
            );
        }
        if (criteria.hasGroupBy() && criteria.hasSortBy()) {
            throw new InvalidQueryException(
                "Parameters 'groupBy' and 'sortBy' cannot be used together. Please provide only one."
            );
        }

        return strategies.stream()
            .filter(strategy -> strategy.supports(criteria))
            .findFirst()
            .orElseThrow(() -> new InvalidQueryException("Unsupported query operation."));
    }
}
