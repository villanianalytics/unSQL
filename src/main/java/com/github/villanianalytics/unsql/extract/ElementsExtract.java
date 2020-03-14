package com.github.villanianalytics.unsql.extract;

import java.util.Arrays;
import java.util.List;

import com.github.villanianalytics.unsql.element.Element;

/**
 * The Class ElementsExtract.
 */
public class ElementsExtract {

	/** The elements. */
	private List<Element> elements;
	
	/**
	 * Instantiates a new elements extract.
	 *
	 * @param elements the elements
	 */
	public ElementsExtract(List<Element> elements) {
		this.elements = elements;
	}
	
	/**
	 * Extract elements.
	 *
	 * @param whereClause the where clause
	 * @param delimiter the delimiter
	 * @return the list
	 */
	public List<Element> extractElements(String whereClause, String delimiter) {
		String[] rawEles = whereClause.split(delimiter);
		
		return Arrays.asList(getElement(rawEles[0].trim()), getElement(rawEles[1].trim()));
	}
	
	/**
	 * Gets the element value.
	 *
	 * @param rawValue the raw value
	 * @param whereClause the where clause
	 * @param delimiter the delimiter
	 * @param index the index
	 * @return the element value
	 */
	public List<String> getElementValue(String rawValue, String whereClause, String delimiter, int index) {
		List<Element> values  = extractElements(whereClause, delimiter);
		String[] rawEles = whereClause.split(delimiter);
		
		return values.get(index).getValue(rawValue, rawEles[index]);	
	}
	
	/**
	 * Gets the element raw.
	 *
	 * @param whereClause the where clause
	 * @param delimiter the delimiter
	 * @param index the index
	 * @return the element raw
	 */
	public String getElementRaw(String whereClause, String delimiter, int index) {
		String[] rawEles = whereClause.split(delimiter);
		return rawEles[index].trim();
	}
	
	/**
	 * Gets the element.
	 *
	 * @param rawValue the raw value
	 * @param whereClause the where clause
	 * @param delimiter the delimiter
	 * @param index the index
	 * @return the element
	 */
	public List<String> getElement(String rawValue, String whereClause, String delimiter, int index) {
		List<Element> values  = extractElements(whereClause, delimiter);
		String[] rawEles = whereClause.split(delimiter);
		
		return values.get(index).getElement(rawValue, rawEles[index]);	
	}
	
	/**
	 * Gets the element.
	 *
	 * @param rawEle the raw ele
	 * @return the element
	 */
	private Element getElement(String rawEle) {
		return elements.stream().filter(e -> e.isValid(rawEle)).findFirst().orElse(null);
	}
}
