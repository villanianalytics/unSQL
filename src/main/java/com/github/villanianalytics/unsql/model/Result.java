package com.github.villanianalytics.unsql.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The Class Result.
 */
public class Result {

	/** The results. */
	private Map<String, String> results;
	
	private Map<String, Map<String, String>> resultsByPattern;
	
	/**
	 * Instantiates a new result.
	 *
	 * @param results the results
	 */
	
	public Result(Map<String, String> results) {
		this.setResults(results);
		this.setResultsByPattern(new HashMap<String, Map<String,String>>());
	}
	
	public Result(Map<String, String> results, Map<String, Map<String, String>> resultsByPatern) {
		this.setResults(results);
		this.setResultsByPattern(resultsByPatern);
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
	
	public String getResultByPattern(List<String> headers, String delimiter) {
		List<String> re = new LinkedList<>();
		
		for (String header : headers) {
			re.add(getResultStringByPattern(header));
		}
		
		return String.join(delimiter, re);
	}
	
	public String getResultStringByPattern(String p) {
		return String.join(",", resultsByPattern.get(p).values());
	}
	
	public List<String> getHeaders() {
		List<String> headers = new LinkedList<>();
		
		for (String key : resultsByPattern.keySet()) {
			headers.add(key);
		}
		
		return headers;
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

	public Map<String, Map<String, String>> getResultsByPattern() {
		return resultsByPattern;
	}

	public void setResultsByPattern(Map<String, Map<String, String>> resultsByPattern) {
		this.resultsByPattern = resultsByPattern;
	}
}
