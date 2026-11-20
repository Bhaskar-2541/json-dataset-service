package com.project.strategy;

import java.util.List;
import java.util.Map;

public interface QueryOperationStrategy {

	boolean supports(QueryCriteria criteria);

	Object execute(List< Map< String, Object > > records, QueryCriteria criteria);
}
