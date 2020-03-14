package com.github.villanianalytics.unsql.condition.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.extract.ElementsExtract;

/**
 * The Class In.
 */
public class In implements Condition {

	/** The Constant IN_DELIMITER. */
	private static final String IN_DELIMITER = "in";
	
	/** The Constant IN_PATTERN. */
	private static final Pattern IN_PATTERN = Pattern.compile(".* " + IN_DELIMITER + " (.*)");

	/** The elements extract. */
	private ElementsExtract elementsExtract;

	/**
	 * Instantiates a new in.
	 *
	 * @param elementsExtract the elements extract
	 */
	public In(ElementsExtract elementsExtract) {
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
		Matcher m = IN_PATTERN.matcher(whereClause);
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
			List<String> valOnes = elementsExtract.getElementValue(input, whereClause, IN_DELIMITER, 0);
			List<String> valTwos = elementsExtract.getElementValue(input, whereClause, IN_DELIMITER, 1);

			List<String> arr = Arrays.asList(valTwos.get(0).split(","));

			boolean valid = false;
			for (String valOne : valOnes) {
				boolean va = arr.stream().anyMatch(str -> str.trim().equals(valOne));
				if (va)
					valid = true;
			}

			return valid;

		};
	}
}
