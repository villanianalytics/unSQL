package com.github.villanianalytics.unsql.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.github.villanianalytics.unsql.condition.Condition;

/**
 * The Class QueryFilters.
 */
public class QueryFilters {

	/** The Constant AND. */
	private static final String AND = "and";
	
	/** The Constant OR. */
	private static final String OR = "or";

	/** The conditions. */
	private List<Condition> conditions;

	/**
	 * Instantiates a new query filters.
	 *
	 * @param conditions the conditions
	 */
	public QueryFilters(List<Condition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * Generate filters.
	 *
	 * @param whereStatement the where statement
	 * @return the predicate
	 */
	public Predicate<String> generateFilters(String whereStatement) {
		if (!StringUtils.isNotBlank(whereStatement))
			return null;

		List<Predicate<String>> predicateList = new ArrayList<>();
		List<String> combinators = new ArrayList<>();
		StringBuilder condition = new StringBuilder();

		char[] whereArray = whereStatement.toCharArray();

		int index = 0;
		while (index < whereArray.length) {
			char ar = whereArray[index];
			if (ar == '(') {
				int lastIndex = findClosingParen(whereArray, index);
				predicateList.add(generateFilters(whereStatement.substring(index + 1, lastIndex)));
				index = lastIndex;
			} else if (condition.toString().endsWith(" " + AND)) {
				computePredicate(predicateList, combinators, condition.toString(), AND);
				condition = new StringBuilder();
			} else if (condition.toString().endsWith(" " + OR)) {
				computePredicate(predicateList, combinators, condition.toString(), OR);
				condition = new StringBuilder();
			} else {
				condition.append(ar);
			}

			index++;
		}

		if (condition.toString().trim().length() > 0) {
			predicateList.add(getPredicate(condition.toString()));
		}

		return combine(predicateList, combinators);
	}

	/**
	 * Compute predicate.
	 *
	 * @param predicateList the predicate list
	 * @param combinators the combinators
	 * @param condition the condition
	 * @param separator the separator
	 */
	private void computePredicate(List<Predicate<String>> predicateList, List<String> combinators, String condition,
			String separator) {
		String c = condition.replace(separator, "");
		if (c.trim().length() > 0)
			predicateList.add(getPredicate(c));
		combinators.add(separator);
	}

	/**
	 * Combine.
	 *
	 * @param predicates the predicates
	 * @param combinators the combinators
	 * @return the predicate
	 */
	private Predicate<String> combine(List<Predicate<String>> predicates, List<String> combinators) {
		int predicateIndex = 0;
		Predicate<String> cCombined = predicates.get(predicateIndex);
		for (String combinator : combinators) {
			predicateIndex++;
			cCombined = combinator.equals(AND) ? cCombined.and(predicates.get(predicateIndex))
					: cCombined.or(predicates.get(predicateIndex));
		}

		return cCombined;
	}

	/**
	 * Gets the predicate.
	 *
	 * @param condition the condition
	 * @return the predicate
	 */
	private Predicate<String> getPredicate(String condition) {
		Optional<Condition> optCondition = conditions.stream().filter(c -> c.isValid(condition.trim())).findFirst();

		return optCondition.isPresent() ? optCondition.get().generate(condition) : null;
	}

	/**
	 * Find closing paren.
	 *
	 * @param text the text
	 * @param openPos the open pos
	 * @return the int
	 */
	private int findClosingParen(char[] text, int openPos) {
		int closePos = openPos;
		int counter = 1;
		while (counter > 0) {
			char c = text[++closePos];
			if (c == '(') {
				counter++;
			} else if (c == ')') {
				counter--;
			}
		}
		return closePos;
	}
}