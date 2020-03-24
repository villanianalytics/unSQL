package com.github.villanianalytics.unsql.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.aggregate.impl.Everything;
import com.github.villanianalytics.unsql.utils.Utils;

/**
 * The Class FromFilter.
 */
public class FromFilter {

	/**
	 * Filter json by from element.
	 *
	 * @param results the results
	 * @param fromElement the from element
	 * @return the map
	 */
	public Map<String, List<String>> filterJsonByFromElement(List<String> results, String fromElement) {
		
		if (Everything.EVERYTHING_SYMBOL.equalsIgnoreCase(fromElement)) {
			 Map<String, List<String>> map = new HashMap<>();
			 map.put(Everything.EVERYTHING_SYMBOL, results);
			return map;
		}
		
		String arrayPattern = fromElement.replace(".", Utils.REG_ARRAY_OR_OBJECT) + Utils.REG_ARRAY;
		String objectPattern = fromElement.replace(".", Utils.REG_ARRAY_OR_OBJECT) + Utils.REG_ARRAY_OR_NOTHING;

		return extractFromArray(Utils.isFromAnArray(fromElement, results) ? arrayPattern : objectPattern, results);
	}

	/**
	 * Extract from array.
	 *
	 * @param patternString the pattern string
	 * @param jsonArray the json array
	 * @return the map
	 */
	private Map<String, List<String>> extractFromArray(String patternString, List<String> jsonArray) {
		Pattern pattern = Pattern.compile(patternString);

		Map<String, List<String>> resultsArray = jsonArray.stream().collect(Collectors.groupingBy(s -> {
			String matcherValue = "";
			Matcher matcher = pattern.matcher(s);
			if (matcher.find()) {
				matcherValue = matcher.group(0);

			}
			return matcherValue;
		}, Collectors.toList()));

		resultsArray.keySet().removeIf(s -> !StringUtils.isNotEmpty(s));

		return resultsArray;
	}
}
