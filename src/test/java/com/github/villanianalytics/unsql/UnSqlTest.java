package com.github.villanianalytics.unsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.villanianalytics.unsql.exception.UnSqlException;
import com.github.villanianalytics.unsql.model.Result;

public class UnSqlTest {

	private String jsonResponse;
	private UnSql util;

	@Before
	public void setUp() throws IOException {
		this.util = new UnSql();

		File file = getFileFromResources("response.json");
		this.jsonResponse = new String(Files.readAllBytes(file.toPath()));

		assertNotNull(jsonResponse.replace("\n", "").trim());
	}

	private File getFileFromResources(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}
	}

	@Test
	public void testSqlJsonParsing() {
		String rawJson = "{\"foo\":\"foo1\",\"bar\":\"bar1\"}";

		util.processFile(rawJson);

		assertNotNull(util.getFlatJsonList());
	}

	@Test
	public void testSqlJsonParsingList() {
		String rawJson = "{\"boolean\":true,\"color\":\"gold\",\"null\":null,\"number\":123,\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		assertNotNull(util.getFlatJsonList());
	}

	@Test
	public void testSqlJsonNoWhereClause() throws UnSqlException {
		String rawJson = "{\"boolean\":true,\"color\":\"gold\",\"null\":null,\"number\":123,\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		String sqlStatement = "select c from object";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("d"));
	}

	@Test
	public void testSqlJsonQuery() throws UnSqlException {
		String rawJson = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		String sqlStatement = "select c from object where object.a = 'b'";

		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("d"));
	}

	@Test
	public void testSqlJsonQueryDifferent() throws UnSqlException {
		String rawJson = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		String sqlStatement = "select c from object where object.a <> b";

		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(0, results.size());
	}

	@Test
	public void testSqlSimpleJsonArrayQueryDifferent() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name <> 'test1'";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("2"));
	}

	@Test
	public void testSqlSimpleJsonArrayQuery() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name='test1' and id=1";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("1"));
	}

	@Test
	public void testSqlJsonArrayQuery() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select id from items where name='test1'";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("id1"));
	}

	@Test
	public void testSqlJsonArrayQueryTwoElements() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select id, name from items where name='test1'";
		List<Result> results = util.executeQuery(sqlStatement);
		for (Result set : results)
			System.out.println(set.getResults());
		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("id1"));
	}

	@Test
	public void testSqlSimpleJsonWhereOr() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name='test1' or id=2";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(2, results.size());
		assertTrue(results.get(0).getResults().containsValue("1"));
	}

	@Test
	public void testSqlSimpleJsonWhereOrParen() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\", \"desc\": \"1\"},{\"name\": \"test2\", \"id\": \"2\", \"desc\": \"3\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name='test1' or (id=2 and desc=1)";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("1"));
	}

	@Test
	public void testSqlJsonMultipleIndex() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select rel,href from items.arr.links where items index 0 and arr index 0 and links index 0";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("href1"));
	}

	@Test
	public void testSqlJsonSelectEverythingMultipleIndex() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select * from items.arr.links where items index 0 and arr index 0 and links index 0";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("link1"));
	}
}
