package com.github.villanianalytics.unsql.aggregate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.villanianalytics.unsql.model.Result;

public class MinTest {

	private Min min;

	@Before
	public void init() {
		min = new Min();
	}

	@Test
	public void testInvalidFunction() {
		boolean valid = min.isValid("");
		assertFalse(valid);
	}

	@Test
	public void testValidFunction() {
		boolean valid = min.isValid("min(value)");
		assertTrue(valid);
	}

	@Test
	public void testFunctionExtract() {
		String valid = min.extractElementToSelect("min(value)");
		assertEquals("value", valid);
	}

	@Test
	public void testApply() {
		List<Result> validResults = min.apply(getTestingList(2), "value");

		assertEquals(1, validResults.size());
	}

	private List<Result> getTestingList(int numberToGenerate) {
		List<Result> testingList = new ArrayList<Result>();
		for (int i = 0; i < numberToGenerate; i++) {
			testingList.add(generateResult());
		}

		return testingList;
	}

	private Result generateResult() {
		Map<String, String> testingResult = new HashMap<String, String>();
		testingResult.put("value", "2");

		return new Result(testingResult);
	}
}
