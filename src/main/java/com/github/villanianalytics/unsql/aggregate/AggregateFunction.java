package com.github.villanianalytics.unsql.aggregate;

import java.util.List;

import com.github.villanianalytics.unsql.model.Result;

/**
 * The Interface AggregateFunction.
 */
public interface AggregateFunction {

	/**
	 * Checks if is valid function.
	 *
	 * @param selectClause the select clause
	 * @return true, if is valid
	 */
	public boolean isValid(String selectClause);

	/**
	 * Extract element to select.
	 *
	 * @param selectClause the select clause
	 * @return the string
	 */
	public String extractElementToSelect(String selectClause);

	/**
	 * Apply.
	 *
	 * @param results the results
	 * @param selectClause the select clause
	 * @return the list
	 */
	public List<Result> apply(List<Result> results, String selectClause);
}
