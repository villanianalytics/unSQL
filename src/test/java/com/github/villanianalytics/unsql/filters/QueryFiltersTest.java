package com.github.villanianalytics.unsql.filters;

import static org.junit.Assert.assertNotNull;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.github.villanianalytics.unsql.model.SelectStatement;
import com.github.villanianalytics.unsql.utils.TestUtils;

public class QueryFiltersTest {

	private QueryFilters queryFilters; 

	@Before
	public void init() {
		queryFilters = new QueryFilters(TestUtils.createListCondition());
	}
	
	@Test
	public void testTwoEqualCondtion() {
		SelectStatement select = new SelectStatement("select * from test where test.test=1");
		Predicate<String> predicate = queryFilters.generateFilters(select.getWhere());
		assertNotNull(predicate);
	}
	
	@Test
	public void testOneEqualCondtion() {
		SelectStatement select = new SelectStatement("select * from test where test.test=1 and test.test2=1");
		Predicate<String> predicate = queryFilters.generateFilters(select.getWhere());
		assertNotNull(predicate);
	}
	
	@Test
	public void testOneEqualAndOrCondtion() {
		SelectStatement select = new SelectStatement("select * from test where test.test=1 and test.test2=1 or test.test3=2");
		Predicate<String> predicate = queryFilters.generateFilters(select.getWhere());
		assertNotNull(predicate);
	}
	
	@Test
	public void testNestedParenthises() {
		Predicate<String> predicate = queryFilters.generateFilters("test.test=1 or (test.test2=1 or (test.test3=2 and test.test3=1))");
		assertNotNull(predicate);
	}
	
	@Test
	public void testNestedParenthisesOnlyOne() {
		Predicate<String> predicate = queryFilters.generateFilters("test.test=1 or (test.test2=1 or test.test3=1)");
		assertNotNull(predicate);
	}
	
	@Test
	public void testNestedParenthisesOnlyTwo() {
		Predicate<String> predicate = queryFilters.generateFilters("test.test=1 or (test.test2=1 or test.test3=1) and (test.test2=1 or test.test3=1)");
		assertNotNull(predicate);
	}
}
