package com.github.villanianalytics.unsql.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class SelectStatement.
 */
public class SelectStatement {

	/** The select. */
	private String select;

	/**
	 * Instantiates a new select statement.
	 *
	 * @param select the select
	 */
	public SelectStatement(String select) {
		super();
		this.select = select;
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public String getFrom() {
		String from = StringUtils.substringBetween(select + " ", "from ", " ");
		return from != null ? from.trim() : null;
	}

	/**
	 * Gets the where.
	 *
	 * @return the where
	 */
	public String getWhere() {
		return select.contains("where") ? select.split("where")[1].trim().toLowerCase(): "";
	}

	/**
	 * Gets the select elements.
	 *
	 * @return the select elements
	 */
	public List<String> getSelectElements() {
		String selectEle = StringUtils.substringBetween(select + " ", "select", "from");

		return selectEle != null
				? Arrays.asList(
						StringUtils.substringBetween(select + " ", "select", "from").trim().replace(" ", "").split(","))
				: null;
	}
}