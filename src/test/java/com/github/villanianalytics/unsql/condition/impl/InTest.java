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

public class InTest {

	private In in;

	@Before
	public void init() {
		in = new In(TestUtils.createElementsExtract());
	}

	@Test
	public void testInvalidWhere() {
		boolean valid = in.isValid("");
		assertFalse(valid);
	}

	@Test
	public void testValidWhere() {
		boolean valid = in.isValid("test.test in [1,2]");
		assertTrue(valid);
	}

	@Test
	public void testValidPredicate() {
		Predicate<String> predicate = in.generate("test.test in ['1','2']");
		assertNotNull(predicate);

		List<String> objects = Arrays
				.asList(new String[] { "test.test=2", "test[1].test=2" });

		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());

		assertEquals(2, filteredObjects.size());
	}

	@Test
	public void testValidPredicateOneElement() {
		Predicate<String> predicate = in.generate("test.test in [1]");
		assertNotNull(predicate);

		List<String> objects = Arrays
				.asList(new String[] { "test.test=1", "testsss.test=1", "testa=1", "test[1].test=2" });

		List<String> filteredObjects = objects.stream().filter(predicate).collect(Collectors.toList());

		assertEquals(1, filteredObjects.size());
	}
}
