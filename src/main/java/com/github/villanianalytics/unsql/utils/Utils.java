package com.github.villanianalytics.unsql.utils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Utils.
 */
public class Utils {

	/**
	 * Instantiates a new utils.
	 */
	private Utils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Checks if is from an array.
	 *
	 * @param fromElement the from element
	 * @param results the results
	 * @return true, if is from an array
	 */
	public static boolean isFromAnArray(String fromElement, List<String> results) {
		String patternString = fromElement.replace(".", "(\\[.*?\\].|.)") + "\\[.*?\\]";
		Pattern pattern = Pattern.compile(patternString);

		Optional<String> result = results.stream().filter(x -> {
			Matcher m = pattern.matcher(x);
			return m.lookingAt();
		}).findFirst();

		return result.isPresent();
	}
}
