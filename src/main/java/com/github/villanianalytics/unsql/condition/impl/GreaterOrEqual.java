package com.github.villanianalytics.unsql.condition.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.extract.ElementsExtract;

/**
 * The Class GreaterOrEqual.
 */
public class GreaterOrEqual implements Condition {

	/** The Constant GREATEROREQUAL_DELIMITER. */
	private static final String GREATEROREQUAL_DELIMITER = ">=";

	/** The Constant GREATER_PATTERN. */
	private static final Pattern GREATER_PATTERN = Pattern.compile(".* " + GREATEROREQUAL_DELIMITER + " .*");

	/** The elements extract. */
	private ElementsExtract elementsExtract;
	
	/**
	 * Instantiates a new greater or equal.
	 *
	 * @param elementsExtract the elements extract
	 */
	public GreaterOrEqual(ElementsExtract elementsExtract) {
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
		Matcher m = GREATER_PATTERN.matcher(whereClause);
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
			List<String> valOnes = elementsExtract.getElementValue(input, whereClause, GREATEROREQUAL_DELIMITER, 0);
			List<String> valTwos = elementsExtract.getElementValue(input, whereClause, GREATEROREQUAL_DELIMITER, 1);

			boolean valid = false;
			for (String valOne : valOnes) {
				for (String valTwo : valTwos) {
					if (!StringUtils.isNumeric(valOne) || !StringUtils.isNumeric(valTwo))
						continue;

					Float floatValueOne = Float.valueOf(valOne);
					Float floatValueTwo = Float.valueOf(valTwo);

					boolean va = (floatValueOne != null && floatValueTwo != null && floatValueOne >= floatValueTwo);

					if (va)
						valid = true;
				}
			}

			return valid;

		};
	}
}
