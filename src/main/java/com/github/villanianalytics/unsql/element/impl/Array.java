package com.github.villanianalytics.unsql.element.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.villanianalytics.unsql.element.Element;

/**
 * The Class Array.
 */
public class Array implements Element {

	/**
	 * Checks if is valid.
	 *
	 * @param element the element
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String element) {
		return (element.startsWith("[") && element.endsWith("]"));
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
		return Arrays.asList(element.trim().substring(1, element.length() - 2));
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
		return new ArrayList<>();
	}
}
