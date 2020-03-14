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

public class GreaterOrEqualTest {

	private GreaterOrEqual greaterOrEqual;

	@Before
	public void init() {
		greaterOrEqual = new GreaterOrEqual(TestUtils.createElementsExtract());
	}

	@Test
	public void testInvalidWhere() {
		boolean valid = greaterOrEqual.isValid("");
		assertFalse(valid);
	}

	@Test
	public void testValidWhere() {
		boolean valid = greaterOrEqual.isValid("test.test >= 1");
		assertTrue(valid);
	}

	@Test
	public void testValidPredicate() {
		Predicate<String> predicate = greaterOrEqual.generate("test.test >= 1");
		assertNotNull(predicate);

		List<String> objects = Arrays.asList(new String[] { "test.test=1,testsss.test=1", "testa=1,test[1].test=2" });

		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());

		assertEquals(2, filteredObjects.size());
	}
}
