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

public class DistinctTest {

	private Distinct distinct;

	@Before
	public void init() {
		distinct = new Distinct();
	}

	@Test
	public void testInvalidFunction() {
		boolean valid = distinct.isValid("");
		assertFalse(valid);
	}

	@Test
	public void testValidFunction() {
		boolean valid = distinct.isValid("distinct value");
		assertTrue(valid);
	}

	@Test
	public void testFunctionExtract() {
		String valid = distinct.extractElementToSelect("distinct value");
		assertEquals("value", valid);
	}

	@Test
	public void testApply() {
		List<Result> validResults = distinct.apply(getTestingList(10), "value");

		assertEquals(10, validResults.size());
	}

	@Test
	public void testApplyAllEquals() {
		List<Result> validResults = distinct.apply(getTestingListEqual(10), "value");

		assertEquals(1, validResults.size());
	}

	private List<Result> getTestingListEqual(int numberToGenerate) {
		List<Result> testingList = new ArrayList<Result>();
		for (int i = 0; i < numberToGenerate; i++) {
			testingList.add(generateResult(0));
		}

		return testingList;
	}

	private List<Result> getTestingList(int numberToGenerate) {
		List<Result> testingList = new ArrayList<Result>();
		for (int i = 0; i < numberToGenerate; i++) {
			testingList.add(generateResult(i));
		}

		return testingList;
	}

	private Result generateResult(int index) {
		Map<String, String> testingResult = new HashMap<String, String>();
		testingResult.put("value", index > 0 ? "val" + index : "val");

		return new Result(testingResult);
	}
}
