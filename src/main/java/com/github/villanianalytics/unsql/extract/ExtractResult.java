package com.github.villanianalytics.unsql.extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.model.Result;

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
	
	
	public List<Result> extract(String fromElement, List<String> selectElements,
			List<String> resultList) {
		Map<String, List<String>> orderJsonArray = groupResultsList(fromElement, resultList);

		List<Result> results = new ArrayList<>();
		orderJsonArray.forEach((k, v) -> {
			if (StringUtils.isEmpty(k))
				return;
			results.add(extractFromObj(fromElement, selectElements, v));
		});

		AggregateFunction aggregateFunction = getAggreateFunction(selectElements);
		if (aggregateFunction != null) {
			return aggregateFunction.apply(results, selectElements.get(0));
		}

		return results;
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
		Map<String, String> resultSet = new HashMap<>();
		List<Pattern> elementsToSelect = generateSelectPatterns(selectElements, fromElement);

		jsonList.forEach(element -> elementsToSelect.forEach(pattern -> {
			Matcher m = pattern.matcher(element);
			if (!m.lookingAt())
				return;
			String[] parts = element.split("=", 2);
			resultSet.put(parts[0], parts[1]);
		}));

		return new Result(resultSet);
	}

	/**
	 * Generate select patterns.
	 *
	 * @param selectElements the select elements
	 * @param fromElement the from element
	 * @return the list
	 */
	private List<Pattern> generateSelectPatterns(List<String> selectElements, String fromElement) {
		String preparedFrom = fromElement.equalsIgnoreCase("*") ? ".*" : fromElement.replace(".", "(\\[\\d{1,3}\\].|.)") + "(\\[\\d{1,3}\\].|.)";

		AggregateFunction aggregateFunction = getAggreateFunction(selectElements);

		return selectElements.stream().map(element -> {
			if (aggregateFunction != null) {
				element = aggregateFunction.extractElementToSelect(element);
			}

			String patternStr = preparedFrom + element.trim();
			return Pattern.compile(patternStr);

		}).collect(Collectors.toList());
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
		String orderPatternStr = fromElement.equalsIgnoreCase("*") ? ".+?(?==)" : 
			fromElement.replace(".", "(\\[.*?\\].|.)") + "(\\[.*?\\]|.|=)";
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
}
