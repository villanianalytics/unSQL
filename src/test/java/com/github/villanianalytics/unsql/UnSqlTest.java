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

import com.github.villanianalytics.unsql.UnSql.EXPORT_FORMAT;
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
	public void testSqlFromRoot() throws UnSqlException {
		String rawJson = "{\"foo\":\"foo1\",\"bar\":\"bar1\"}";

		util.processFile(rawJson);

		String sqlStatement = "select foo from *";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("foo1"));
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.JSON);

		assertEquals("{\"foo\":\"foo1\"}", jsonResult);
	}
	
	@Test
	public void testSqlFromRootBar() throws UnSqlException {
		String rawJson = "{\"foo\":\"foo1\",\"bar\":\"bar1\"}";

		util.processFile(rawJson);

		String sqlStatement = "select bar from *";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("bar1"));
	}

	@Test
	public void testSqlJsonQuery() throws UnSqlException {
		String rawJson = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		String sqlStatement = "select c from object where object.a = 'b'";

		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("d"));
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.JSON);

		assertEquals("{\"object\":{\"c\":\"d\"}}", jsonResult);
	}

	@Test
	public void testSqlJsonQueryDifferent() throws UnSqlException {
		String rawJson = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";

		util.processFile(rawJson);

		String sqlStatement = "select c from object where object.a <> 'b'";

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
	public void testSqlGreater() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where id > 1";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("2"));
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"items[1].id\":\"2\",\"items[1].name\":\"test2\"", jsonResult);
	}
	
	@Test
	public void testSqlLess() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where id < 2";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		assertTrue(results.get(0).getResults().containsValue("1"));
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"items[0].id\":\"1\",\"items[0].name\":\"test1\"", jsonResult);
	}
	
	@Test
	public void testSqlIn() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where name in ['test1', 'test2']";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(2, results.size());
		assertTrue(results.get(0).getResults().containsValue("1"));
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"items[0].id\":\"1\",\"items[0].name\":\"test1\",\"items[1].id\":\"2\",\"items[1].name\":\"test2\"", jsonResult);
	}
	
	@Test
	public void testSqlNotIn() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where name notin ['test1', 'test2']";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(0, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("", jsonResult);
	}
	
	@Test
	public void testSqlLike() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where name like 'test'";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(2, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"items[0].id\":\"1\",\"items[0].name\":\"test1\",\"items[1].id\":\"2\",\"items[1].name\":\"test2\"", jsonResult);
	}
	
	@Test
	public void testSqlIndex() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id, name from items where items index 0";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"items[0].id\":\"1\",\"items[0].name\":\"test1\"", jsonResult);
	}
	
	@Test
	public void testSqlMax() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select max(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"max(id)\":\"2.0\"", jsonResult);
	}
	
	@Test
	public void testSqlMaxValue() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select max(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.VALUES);

		assertEquals("2.0", jsonResult);
	}

	@Test
	public void testSqlMin() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select min(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"min(id)\":\"1.0\"", jsonResult);
	}
	
	@Test
	public void testSqlSum() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select sum(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"sum(id)\":\"3.0\"", jsonResult);
	}
	
	@Test
	public void testSqlCount() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select count(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"count(id)\":\"2\"", jsonResult);
	}
	
	@Test
	public void testSqlAvg() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select avg(id) from items";
		List<Result> results = util.executeQuery(sqlStatement);

		assertEquals(1, results.size());
		
		String jsonResult = util.executeQuery(sqlStatement, EXPORT_FORMAT.TEXT);

		assertEquals("\"avg(id)\":\"1.5\"", jsonResult);
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
	
	@Test
	public void testSqlJsonToJson() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select * from items.arr.links where items index 0 and arr index 0 and links index 0";
		String result = util.executeQuery(sqlStatement, UnSql.EXPORT_FORMAT.JSON);

		assertNotNull(result);
		assertTrue(result.contains("link1"));
	}
	
	@Test
	public void testSqlJsonToXml() throws UnSqlException {
		util.processFile(this.jsonResponse);

		String sqlStatement = "select * from items.arr.links where items index 0 and arr index 0 and links index 0";
		String result = util.executeQuery(sqlStatement, UnSql.EXPORT_FORMAT.XML);

		assertNotNull(result);
		assertTrue(result.contains("link1"));
	}
	
	@Test
	public void testEmptyToXML() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\", \"desc\": \"1\"},{\"name\": \"test2\", \"id\": \"2\", \"desc\": \"3\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name='tett'";
		String result = util.executeQuery(sqlStatement, UnSql.EXPORT_FORMAT.XML);

		assertEquals("", result);
	}
	
	@Test
	public void testEmptyToJson() throws UnSqlException {
		String rawJson = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\", \"desc\": \"1\"},{\"name\": \"test2\", \"id\": \"2\", \"desc\": \"3\"}]}";

		util.processFile(rawJson);

		String sqlStatement = "select id from items where name='tett'";
		String result = util.executeQuery(sqlStatement, UnSql.EXPORT_FORMAT.JSON);

		assertEquals("{}", result);
	}
}
