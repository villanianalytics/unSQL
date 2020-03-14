package com.github.villanianalytics.unsql.element;

import java.util.List;

/**
 * The Interface Element.
 */
public interface Element {
	
	/**
	 * Checks if is valid.
	 *
	 * @param whereClause the where clause
	 * @return true, if is valid
	 */
	public boolean isValid(String whereClause);

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @param whereClause the where clause
	 * @return the value
	 */
	public List<String> getValue(String value, String whereClause);
	
	/**
	 * Gets the element.
	 *
	 * @param value the value
	 * @param whereClause the where clause
	 * @return the element
	 */
	public List<String> getElement(String value, String whereClause);
}

