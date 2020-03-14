package com.github.villanianalytics.unsql.condition.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.extract.ElementsExtract;

/**
 * The Class Equal.
 */
public class Equal implements Condition {

	/** The Constant EQUAL_DELIMITER. */
	private static final String EQUAL_DELIMITER = "=";

	/** The Constant EQUAL_PATTERN. */
	private static final Pattern EQUAL_PATTERN = Pattern.compile(".*" + EQUAL_DELIMITER + ".*");

	/** The elements extract. */
	private ElementsExtract elementsExtract;

	/**
	 * Instantiates a new equal.
	 *
	 * @param elementsExtract the elements extract
	 */
	public Equal(ElementsExtract elementsExtract) {
		this.elementsExtract = elementsExtract;
	}

	/**
	 * Checks if is valid.
	 *
	 * @param whereClause the where clause
	 * @return true, if is valid
	 */
	@Override
	public boolean isValid(String whereClause) {
		if (whereClause == null)
			return false;
		Matcher m = EQUAL_PATTERN.matcher(whereClause);
		return m.matches();
	}

	/**
	 * Generate.
	 *
	 * @param whereClause the where clause
	 * @return the predicate
	 */
	@Override
	public Predicate<String> generate(String whereClause) {
		return input -> {
			List<String> valOnes = elementsExtract.getElementValue(input, whereClause, EQUAL_DELIMITER, 0);
			List<String> valTwos = elementsExtract.getElementValue(input, whereClause, EQUAL_DELIMITER, 1);

			boolean valid = false;
			for (String valOne : valOnes) {
				for (String valTwo : valTwos) {
					boolean va = (valOne != null && valTwo != null && valOne.equalsIgnoreCase(valTwo));

					if (va)
						valid = true;
				}
			}

			return valid;
		};
	}
}
