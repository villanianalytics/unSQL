package com.github.villanianalytics.unsql.validate;

import java.util.Arrays;
import java.util.List;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.exception.UnSqlException;
import com.github.villanianalytics.unsql.model.SelectStatement;

/**
 * The Class ValidateQuery.
 */
public class ValidateQuery {
	
	/** The aggregate functions. */
	private List<AggregateFunction> aggreateFunctions;
	
	/** The conditions. */
	private List<Condition> conditions;
	
	/**
	 * Instantiates a new validate query.
	 *
	 * @param aggregateFunctions the aggregate functions
	 * @param conditions the conditions
	 */
	public ValidateQuery(List<AggregateFunction> aggregateFunctions, List<Condition> conditions) {
		this.aggreateFunctions = aggregateFunctions;
		this.conditions = conditions;
	}

	/**
	 * Validate select.
	 *
	 * @param select the select
	 * @throws UnSqlException the un sql exception
	 */
	public void validateSelect(SelectStatement select) throws UnSqlException {
	
		if (!(select.getFrom() != null && select.getFrom().trim().length() > 0)){
			throw new UnSqlException("From table not defined");
		}
		
		if (select.getSelectElements() == null || select.getSelectElements().isEmpty()) {
			throw new UnSqlException("No Elements defined to select");
		}
		
		long count = aggreateFunctions.stream().mapToLong(func -> select.getSelectElements().stream().filter(func::isValid).count()).sum();
		if (count > 1) {
			throw new UnSqlException("More than one aggreate functions defined");
		}
		
		if (count > 0 && select.getSelectElements().size() - count > 0) {
			throw new UnSqlException("Combination of elements to select and aggreate functions");
		}
		
		String whereRaw = select.getWhere();
		List<String> conditionsList = Arrays.asList(whereRaw.toLowerCase().split("and|or"));
		
		for (String where : conditionsList) {
			if (!(where != null && where.trim().length() > 0)) continue;
			
			long countConditions = conditions.stream().filter(cond -> cond.isValid(where)).count();
			
			if (countConditions > 1 ) {
				throw new UnSqlException("Contact System admin: The where statement " + where + " has more than one valid condition, found " + countConditions);
			}
			
			if (countConditions == 0) {
				throw new UnSqlException("The where statement " + where + " has no valid condition");
			}
		}
	}
}
