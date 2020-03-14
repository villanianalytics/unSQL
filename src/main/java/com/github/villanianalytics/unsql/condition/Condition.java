package com.github.villanianalytics.unsql.condition;

import java.util.function.Predicate;

/**
 * The Interface Condition.
 */
public interface Condition {

	/**
	 * Checks if is valid.
	 *
	 * @param whereClause the where clause
	 * @return true, if is valid
	 */
	public boolean isValid(String whereClause);
	
	/**
	 * Generate.
	 *
	 * @param whereClause the where clause
	 * @return the predicate
	 */
	public Predicate<String> generate(String whereClause);
}
