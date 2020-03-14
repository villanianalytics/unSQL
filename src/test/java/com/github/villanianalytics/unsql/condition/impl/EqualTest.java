package com.github.villanianalytics.unsql.condition.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.github.villanianalytics.unsql.utils.TestUtils;


public class EqualTest {
	
	private Equal equal; 
	
	@Before
	public void init() {
		equal = new Equal(TestUtils.createElementsExtract());
	}
	
	@Test
	public void testInvalidWhere() {
		boolean valid = equal.isValid("");
		assertFalse(valid);
	}
	
	@Test
	public void testValidWhere() {
		boolean valid = equal.isValid("test.test=1");
		assertTrue(valid);
	}
	
	@Test
	public void testValidPredicateNumeric() {
		Predicate<String> predicate = equal.generate("test.test=1");
		assertNotNull(predicate);
		
		List<String> objects = Arrays.asList(new String[]{"test.test=1","testsss.test=1","testa=1", "test[1].test=1"});
		
		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());
	
		assertEquals(2, filteredObjects.size());
	}
	
	@Test
	public void testValidPredicateString() {
		Predicate<String> predicate = equal.generate("test.test='1'");
		assertNotNull(predicate);
		
		List<String> objects = Arrays.asList(new String[]{"test.test=1","testsss.test=1","testa=1", "test[1].test=1"});
		
		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());
	
		assertEquals(2, filteredObjects.size());
	}
	
	@Test
	public void testValidPredicateField() {
		Predicate<String> predicate = equal.generate("test.test=test.test");
		assertNotNull(predicate);
		
		List<String> objects = Arrays.asList(new String[]{"test.test=1","testsss.test=1","testa=1", "test[1].test=1"});
		
		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());
	
		assertEquals(2, filteredObjects.size());
	}
}
