package com.github.villanianalytics.unsql.model;

import java.util.Map;

/**
 * The Class Result.
 */
public class Result {

	/** The results. */
	private Map<String, String> results;
	
	/**
	 * Instantiates a new result.
	 *
	 * @param results the results
	 */
	public Result(Map<String, String> results) {
		this.setResults(results);
	}
	
	/**
	 * Gets the string.
	 *
	 * @param fieldName the field name
	 * @return the string
	 */
	public String getString(String fieldName) {
		return String.valueOf(getResults().get(fieldName));
	}

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public Map<String, String> getResults() {
		return results;
	}

	/**
	 * Sets the results.
	 *
	 * @param results the results
	 */
	public void setResults(Map<String, String> results) {
		this.results = results;
	}
}
