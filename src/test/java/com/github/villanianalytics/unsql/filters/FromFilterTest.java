package com.github.villanianalytics.unsql.filters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FromFilterTest {
	
	private FromFilter fromFilter;
	
	@Before
	public void init() {
		fromFilter = new FromFilter();
	}
	
	@Test
	public void testFromArrayExtract() {
		String fromElement = "test.test2.test3";
		List<String> input = Arrays.asList("test.test3[3].testt=1", "test.test2[1].test3[1].test4=10", "test.test2[1].test3[1].test5=20");
		
		Map<String, List<String>> results = fromFilter.filterJsonByFromElement(input, fromElement);
		
		assertEquals(1, results.size());
	}

	@Test
	public void testFromObjectExtract() {
		String fromElement = "test.test2.test3";
		List<String> input = Arrays.asList("test.test3[3].testt=1", "test.test2[1].test3.test4=10", "test.test2[2].test3.test4=20");
		
		Map<String, List<String>> results = fromFilter.filterJsonByFromElement(input, fromElement);
		
		assertEquals(2, results.size());
	}
}
