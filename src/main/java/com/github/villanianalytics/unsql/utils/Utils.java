package com.github.villanianalytics.unsql.utils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Utils.
 */
public final class Utils {

	public static final String REG_ALL = ".*";
	public static final String REG_ARRAY = "\\[.*?\\]";
	public static final String REG_ARRAY_OR_NOTHING = "(\\[.*?\\]|)";
	public static final String REG_ARRAY_OR_OBJECT = "(\\[[0-9]{1,3}\\].|.)";
	public static final String REG_END_OF_OBJECT = "(\\.|\\[|\\=)";
	
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
		String patternString = fromElement.replace(".", Utils.REG_ARRAY_OR_OBJECT) + Utils.REG_ARRAY;
		Pattern pattern = Pattern.compile(patternString);

		Optional<String> result = results.stream().filter(x -> {
			Matcher m = pattern.matcher(x);
			return m.lookingAt();
		}).findFirst();

		return result.isPresent();
	}
	
	public static String getHeaderName(String key) {
		return key.substring(key.lastIndexOf('.') + 1);
	}
}
