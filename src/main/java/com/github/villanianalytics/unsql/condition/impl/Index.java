package com.github.villanianalytics.unsql.condition.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.extract.ElementsExtract;

/**
 * The Class Index.
 */
public class Index implements Condition {

	/** The Constant INDEX_DELIMITER. */
	private static final String INDEX_DELIMITER = "index";

	/** The Constant INDEX_PATTERN. */
	private static final Pattern INDEX_PATTERN = Pattern.compile(".* " + INDEX_DELIMITER + " .*");

	/** The elements extract. */
	private ElementsExtract elementsExtract;

	/**
	 * Instantiates a new index.
	 *
	 * @param elementsExtract the elements extract
	 */
	public Index(ElementsExtract elementsExtract) {
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
		Matcher m = INDEX_PATTERN.matcher(whereClause.trim());
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
			String valOneRaw = elementsExtract.getElementRaw(whereClause, INDEX_DELIMITER, 0);

			List<String> valTwoList = elementsExtract.getElementValue(input, whereClause, INDEX_DELIMITER, 1);

			String valTwo = valTwoList.get(0);

			Pattern pattern = generatePattern(valOneRaw.trim(), valTwo.trim());
			Matcher m = pattern.matcher(input);
			
			return m.lookingAt();
		};
	}

	/**
	 * Generate pattern.
	 *
	 * @param elementOne the element one
	 * @param elementTwo the element two
	 * @return the pattern
	 */
	private Pattern generatePattern(String elementOne, String elementTwo) {
		String pattern = ".*" + elementOne.replace(".", "(\\[.\\].|.)") + "\\[" + elementTwo + "\\]";
		return Pattern.compile(pattern);
	}
}
