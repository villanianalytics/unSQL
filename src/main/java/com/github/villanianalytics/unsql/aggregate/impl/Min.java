package com.github.villanianalytics.unsql.aggregate.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.model.Result;

/**
 * The Class Min.
 */
public class Min implements AggregateFunction {
	
	/**
	 * Checks if is valid.
	 *
	 * @param selectClause the select clause
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String selectClause) {
		return selectClause.startsWith("min(");
	}

	/**
	 * Extract element to select.
	 *
	 * @param selectClause the select clause
	 * @return the string
	 */
	@Override
	public String extractElementToSelect(String selectClause) {
		return StringUtils.substringBetween(selectClause, "(", ")").trim();
	}

	/**
	 * Apply.
	 *
	 * @param results the results
	 * @param selectClause the select clause
	 * @return the list
	 */
	@Override
	public List<Result> apply(List<Result> results, String selectClause) {
		OptionalDouble minVal = results.stream().mapToDouble(result -> {
			List<String> obj = result.getResults().values().stream().collect(Collectors.toList());
			return Double.valueOf(obj.get(0));
		}).min();
		
		Map<String, String> map = new HashMap<>();
		map.put(selectClause, String.valueOf(minVal.getAsDouble()));
		
		Map<String, Map<String, String>> resultsByPattern = new HashMap<>();
		resultsByPattern.put(selectClause, map);
		return Arrays.asList(new Result(map, resultsByPattern));

	}
}
