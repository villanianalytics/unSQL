# unSQL Utility

A command line tool to run SQL on unstructured data. (Json and XML files)

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


## Maven Repo
```xml
<dependency>
	<groupId>com.github.villanianalytics.unsql</groupId>
	<artifactId>unsql</artifactId>
	<version>0.0.7</version>
</dependency>
```

## Quick Start
```java
String json = "{\"foo\":\"foo1\",\"bar\":\"bar1\"}";
String query = "select foo from *";
UnSql util = new UnSql(json);
String jsonResult = util.executeQuery(query, EXPORT_FORMAT.JSON);

System.out.println(jsonResult);
// Output: {"foo":"foo1"}
```

### Conditions

Most of the common sql conditions are available, below is a list and how to use them. 


* Equal

```java
String json = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";
String query = "select c from object where object.a = 'b'";

UnSql util = new UnSql(json);
String jsonResult = util.executeQuery(query, EXPORT_FORMAT.JSON);

System.out.println(jsonResult);
// Output: {"object":{"c":"d"}}
```
* Different

```java
String json = "{\"object\":{\"a\":\"b\",\"c\":\"d\",\"array\":[1,2,3]},\"string\":\"Hello World\"}";
String query = "select c from object where object.a <> 'b'";

UnSql util = new UnSql(json);
String jsonResult = util.executeQuery(query, EXPORT_FORMAT.JSON);

System.out.println(jsonResult);
// Output: {}
```
* Greater

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where id > 1";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(jsonResult);
// Output: "\"items[1].id\":\"2\",\"items[1].name\":\"test2\""
```

* Greater or Equal

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where id >= 2";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(jsonResult);
// Output: "\"items[1].id\":\"2\",\"items[1].name\":\"test2\""
```

* Less

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where id < 2";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(jsonResult);
// Output: "\"items[0].id\":\"1\",\"items[0].name\":\"test1\""
```

* Less or Equal

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where id <= 1";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(jsonResult);
// Output: "\"items[0].id\":\"1\",\"items[0].name\":\"test1\""
```

* In

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where name in ['test1', 'test2']";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: "\"items[0].id\":\"1\",\"items[0].name\":\"test1\",\"items[1].id\":\"2\",\"items[1].name\":\"test2\""
```

* NotIn

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where name notin ['test1', 'test2']";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""
```

* Like

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where name like 'test'";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: "\"items[0].id\":\"1\",\"items[0].name\":\"test1\",\"items[1].id\":\"2\",\"items[1].name\":\"test2\""
```

* Index

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select id, name from items where name items index 0";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: "\"items[0].id\":\"1\",\"items[0].name\":\"test1\""
```


### Aggregate Functions

* Max

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select max(id) from items";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""max(id)":"2.0""
```

* Min

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select min(id) from items";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""min(id)":"1.0""
```

* Sum

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select sum(id) from items";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""sum(id)":"3.0""
```

* Count

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select count(id) from items";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""count(id)":"2""
```

* Avg

```java
String json = "{\"items\":[{\"name\": \"test1\", \"id\": \"1\"},{\"name\": \"test2\", \"id\": \"2\"}]}";
String query = "select avg(id) from items";

UnSql util = new UnSql(json);
String textResult = util.executeQuery(query, EXPORT_FORMAT.TEXT);

System.out.println(textResult);
// Output: ""avg(id)":"1.5""
```

## License

This project is licensed under the MIT License

