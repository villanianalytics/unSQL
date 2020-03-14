package com.github.villanianalytics.unsql.element.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.element.Element;

/**
 * The Class Numeric.
 */
public class Numeric implements Element{

	/**
	 * Checks if is valid.
	 *
	 * @param element the element
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String element) {
		return StringUtils.isNumeric(element);
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
		return Arrays.asList(element.trim());
	}

	/**
	 * Gets the element.
	 *
	 * @param value the value
	 * @param whereClause the where clause
	 * @return the element
	 */
	@Override
	public List<String> getElement(String value, String whereClause) {
		return new ArrayList<>();
	}
}