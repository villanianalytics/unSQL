package com.github.villanianalytics.unsql.extract;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.villanianalytics.unsql.model.Result;
import com.github.villanianalytics.unsql.utils.TestUtils;


public class ExtractResultTest {
	
	private ExtractResult extractResult;
	
	@Before
	public void init() {
		extractResult = new ExtractResult(TestUtils.createListAggregateFunctions());
	}
	
	@Test
	public void testObjectExtract() {
		String fromElement = "test.test2.test3";
		List<String> selectElements = Arrays.asList("test4", "test5", "test6");
		List<String> input = Arrays.asList("test.test3[3].testt=1", "test.test2[1].test3[1].test4=10", "test.test2[1].test3[1].test5=20");
		
		List<Result> results = extractResult.extractFromArray(fromElement,selectElements, input);
		
		assertEquals(1, results.size());
	}
	
	@Test
	public void testArrayExtract() {
		String fromElement = "test.test2.test3";
		List<String> selectElements = Arrays.asList("test4", "test5", "test6");
		List<String> input = Arrays.asList("test.test2[1].test3[1].test6=1", "test.test2[1].test3[2].test4=10", "test.test2[1].test3[1].test5=20");
		
		List<Result> results = extractResult.extractFromArray(fromElement, selectElements, input);
		
		assertEquals(2, results.size());
	}
	
	@Test
	public void testArrayInsideArrayExtract() {
		String fromElement = "test.test2.test3";
		List<String> selectElements = Arrays.asList("test4", "test5", "test6");
		List<String> input = Arrays.asList("test.test2[1].test3[1].test4[1]=1", "test.test2[1].test3[2].test4[2]=10", "test.test2[1].test3[1].test5=20");
		
		List<Result> results = extractResult.extractFromArray(fromElement, selectElements, input);
		
		assertEquals(2, results.size());	
	}
}
