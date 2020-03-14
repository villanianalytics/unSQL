package com.github.villanianalytics.unsql.element.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.element.Element;

/**
 * The Class Other.
 */
public class Other implements Element {

	/**
	 * Checks if is valid.
	 *
	 * @param element the element
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String element) {
		if(StringUtils.isNumeric(element)) return false;
		
		if(element.startsWith("'") && element.endsWith("'")) return true;
		
		return element.startsWith("\"") && element.endsWith("\"");
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
		return Arrays.asList(element.trim().substring(1, element.trim().length() - 1));
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