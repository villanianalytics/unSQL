package com.github.villanianalytics.unsql.aggregate.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.model.Result;

/**
 * The Class Distinct.
 */
public class Distinct implements AggregateFunction {
	
	/**
	 * Checks if is valid.
	 *
	 * @param selectClause the select clause
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String selectClause) {
		return selectClause.startsWith("distinct ");
	}

	/**
	 * Extract element to select.
	 *
	 * @param selectClause the select clause
	 * @return the string
	 */
	@Override
	public String extractElementToSelect(String selectClause) {
		return selectClause.replace("distinct ", "").trim();
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
		Set<String> distinctList = new HashSet<>();
		
		results.forEach(result -> {
			Map<String, String> mapResult = result.getResults().entrySet().stream().filter(entrySet -> {
				if (distinctList.contains(entrySet.getValue())) {
					return false;
				}
				
				distinctList.add(entrySet.getValue());
				return true;
			}).collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));
			
			result.setResults(mapResult);
		});
		
		return results.stream().filter(jrs -> jrs.getResults().size() > 0).collect(Collectors.toList());
	}
}
