package com.github.villanianalytics.unsql.element.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.element.Element;

/**
 * The Class JsonElement.
 */
public class JsonElement implements Element {

	/**
	 * Checks if is valid.
	 *
	 * @param element the element
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String element) {
		if (StringUtils.isNumeric(element))
			return false;

		if (element.startsWith("'") && element.endsWith("'"))
			return false;

		if (element.startsWith("\"") && element.endsWith("\""))
			return false;

		return !(element.startsWith("(") && element.endsWith(")"));
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @param element the element
	 * @return the value
	 */
	@Override
	public List<String> getValue(String value, String element) {
		Pattern pattern = generatePattern(element.trim());
		List<String> results = new ArrayList<>();
		List<String> elements = Arrays.asList(cleanValue(value).split(","));
		for (String ele : elements) {
			Matcher m = pattern.matcher(ele.toLowerCase());
			if (m.lookingAt()) {
				results.add(ele.replace(m.group(0), ""));
			}
		}
		return results;
	}

	/**
	 * Generate pattern.
	 *
	 * @param elementOne the element one
	 * @return the pattern
	 */
	private Pattern generatePattern(String elementOne) {
		String pattern = ".*" + elementOne.replace(".", "(\\[.*\\].|.)") + "=";
		return Pattern.compile(pattern);
	}

	/**
	 * Gets the element.
	 *
	 * @param value the value
	 * @param element the element
	 * @return the element
	 */
	@Override
	public List<String> getElement(String value, String element) {
		Pattern pattern = generatePatternElement(element.trim());
		List<String> results = new ArrayList<>();

		List<String> elements = Arrays.asList(cleanValue(value).split(","));

		for (String ele : elements) {
			Matcher m = pattern.matcher(ele.toLowerCase());
			if (m.lookingAt()) {
				results.add(m.group(0));
			}
		}
		return results;
	}

	/**
	 * Generate pattern element.
	 *
	 * @param elementOne the element one
	 * @return the pattern
	 */
	private Pattern generatePatternElement(String elementOne) {
		String pattern = ".*" + elementOne.replace(".", "(\\[.*\\].|.)");
		return Pattern.compile(pattern);
	}

	/**
	 * Clean value.
	 *
	 * @param value the value
	 * @return the string
	 */
	private String cleanValue(String value) {
		return value.startsWith("[") ? value.substring(1, value.length() - 1) : value;
	}
}
