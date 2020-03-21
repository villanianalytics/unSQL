package com.github.villanianalytics.unsql.condition.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.extract.ElementsExtract;

/**
 * The Class NotIn.
 */
public class NotIn implements Condition {

	/** The Constant NOTIN_DELIMITER. */
	private static final String NOTIN_DELIMITER = "notin";
	
	/** The Constant NOTIN_PATTERN. */
	private static final Pattern NOTIN_PATTERN = Pattern.compile(".*\\s" + NOTIN_DELIMITER + "\\s\\[.*\\]");

	/** The elements extract. */
	private ElementsExtract elementsExtract;

	/**
	 * Instantiates a new not in.
	 *
	 * @param elementsExtract the elements extract
	 */
	public NotIn(ElementsExtract elementsExtract) {
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
		Matcher m = NOTIN_PATTERN.matcher(whereClause);
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
			List<String> valOnes = elementsExtract.getElementValue(input, whereClause, NOTIN_DELIMITER, 0);
			List<String> valTwos = elementsExtract.getElementValue(input, whereClause, NOTIN_DELIMITER, 1);

			List<String> arr = Arrays.asList(valTwos.get(0).split(","));
			
			List<String> elementValues = new ArrayList<>();
			arr.forEach(a -> elementValues.addAll(elementsExtract.extractValue(input, a)));
			
			boolean valid = false;
			for (String valOne : valOnes) {
				boolean va = elementValues.stream().noneMatch(str -> str.trim().equals(valOne));
				if (va)
					valid = true;
			}

			return valid;
		};
	}
}
