package com.github.villanianalytics.unsql.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.aggregate.impl.Everything;
import com.github.villanianalytics.unsql.model.Result;
import com.github.villanianalytics.unsql.utils.Utils;

/**
 * The Class ExtractResult.
 */
public class ExtractResult {

	/** The aggreate functions. */
	private List<AggregateFunction> aggreateFunctions;
	
	/**
	 * Instantiates a new extract result.
	 *
	 * @param aggreateFunctions the aggreate functions
	 */
	public ExtractResult(List<AggregateFunction> aggreateFunctions) {
		this.aggreateFunctions = aggreateFunctions;
	}
	
	
	/**
	 * Extract.
	 *
	 * @param fromElement the from element
	 * @param selectElements the select elements
	 * @param resultList the result list
	 * @return the list
	 */
	public List<Result> extract(String fromElement, List<String> selectElements,
			List<String> resultList) {
		Map<String, List<String>> orderJsonArray = groupResultsList(fromElement, resultList);

		List<Result> results = new ArrayList<>();
		for (Entry<String, List<String>> entry : orderJsonArray.entrySet()) {
			if (!StringUtils.isEmpty(entry.getKey())) {
				results.add(extractFromObj(fromElement, selectElements, entry.getValue()));
			}
		}
		
		if (selectElements.size() == 1 && isRoot(selectElements.get(0))) computeResults(results);

		AggregateFunction aggregateFunction = getAggreateFunction(selectElements);
		if (aggregateFunction != null) {
			return aggregateFunction.apply(results, selectElements.get(0));
		}

		return results;
	}
	
	
	/**
	 * Compute results.
	 *
	 * @param results the results
	 */
	private void computeResults(List<Result> results) {
		Set<String> headers = new HashSet<>();
		
		results.forEach(r -> headers.addAll(r.getHeaders()));
		
		results.forEach(r -> headers.forEach(header -> {
				if (!r.getResultsByPattern().containsKey(header)) {
					Map<String, String> values= new HashMap<>();
					values.put(header, "");
					
					r.getResultsByPattern().put(header, values);
				}
			})
		);
	}
	

	/**
	 * Extract from obj.
	 *
	 * @param fromElement the from element
	 * @param selectElements the select elements
	 * @param jsonList the json list
	 * @return the result
	 */
	private Result extractFromObj(String fromElement, List<String> selectElements, List<String> jsonList) {
		Map<String, String> resultSet = new LinkedHashMap<>();
		Map<String, Map<String, String>> resultByPattern = new LinkedHashMap<>();
		Map<String, Pattern> elementsToSelect = generateSelectPatterns(selectElements, fromElement);
		
		for (Entry<String,Pattern> entry : elementsToSelect.entrySet()) {
			Map<String, String> rset = new LinkedHashMap<>();
			Pattern p = entry.getValue();
			boolean found = false;
			for (String element : jsonList) {
				Matcher m = p.matcher(element);
				if (m.lookingAt()) {
					String[] parts = element.split("=", 2);
					rset.put(parts[0], parts[1]);
					found = true;
				}
			}
			
			if (!found) rset.put(entry.getKey(), "");
			
			if (isRoot(entry.getKey())) {
				for (Entry<String,String> e : rset.entrySet()) {
					Map<String, String> subResult = new LinkedHashMap<>();
					subResult.put(e.getKey(), e.getValue());
					resultByPattern.put(Utils.getHeaderName(e.getKey()), subResult);
				}
			} else {
				resultByPattern.put(entry.getKey(), rset);	
			}
			resultSet.putAll(rset);
		}
		
		return new Result(resultSet, resultByPattern);
	}
	

	/**
	 * Generate select patterns.
	 *
	 * @param selectElements the select elements
	 * @param fromElement the from element
	 * @return the list
	 */
	private Map<String, Pattern> generateSelectPatterns(List<String> selectElements, String fromElement) {
		Map<String, Pattern> patterns = new LinkedHashMap<>();
		String preparedFrom = fromElement.equalsIgnoreCase(Everything.EVERYTHING_SYMBOL) ? Utils.REG_ALL : fromElement.replace(".", Utils.REG_ARRAY_OR_OBJECT) + Utils.REG_ARRAY_OR_OBJECT;

		AggregateFunction aggregateFunction = getAggreateFunction(selectElements);
		
		for(String element : selectElements) {
			if (aggregateFunction != null) {
				element = aggregateFunction.extractElementToSelect(element);
			}

			String patternStr = preparedFrom + (element.replace(".", Utils.REG_ARRAY_OR_OBJECT)).trim() + Utils.REG_END_OF_OBJECT;
			patterns.put(element, Pattern.compile(patternStr));
		}
		
		return patterns;
	}

	/**
	 * Gets the aggreate function.
	 *
	 * @param selectElements the select elements
	 * @return the aggreate function
	 */
	private AggregateFunction getAggreateFunction(List<String> selectElements) {
		if (selectElements.size() > 1)
			return null;

		return aggreateFunctions.stream().filter(f -> f.isValid(selectElements.get(0))).findFirst().orElse(null);
	}

	/**
	 * Group results list.
	 *
	 * @param fromElement the from element
	 * @param resultList the result list
	 * @return the map
	 */
	private Map<String, List<String>> groupResultsList(String fromElement, List<String> resultList) {
		if(isRoot(fromElement)) return groupRootResults(resultList);
		
		String orderPatternStr = fromElement.replace(".", Utils.REG_ARRAY_OR_OBJECT) + "(\\[.*?\\]|.|=)";
		Pattern orderPattern = Pattern.compile(orderPatternStr);

		return resultList.stream().collect(Collectors.groupingBy(s -> {
			String matcherValue = "";
			Matcher matcher = orderPattern.matcher(s);
			if (matcher.find()) {
				matcherValue = matcher.group(0);

			}
			return matcherValue;
		}, Collectors.toList()));
	}
	
	/**
	 * Group root results.
	 *
	 * @param resultList the result list
	 * @return the map
	 */
	private Map<String, List<String>> groupRootResults(List<String> resultList) {
		return resultList.stream().collect(Collectors.groupingBy(s -> "*", Collectors.toList()));
	}
	
	/**
	 * Checks if is root.
	 *
	 * @param fromElement the from element
	 * @return true, if is root
	 */
	private boolean isRoot(String fromElement) {
		return fromElement.equalsIgnoreCase(Everything.EVERYTHING_SYMBOL) 
				|| fromElement.equalsIgnoreCase(Utils.REG_ALL);
	}
}
