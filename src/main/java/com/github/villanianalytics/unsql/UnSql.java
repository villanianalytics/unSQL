package com.github.villanianalytics.unsql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;

import com.github.villanianalytics.unsql.aggregate.AggregateFunction;
import com.github.villanianalytics.unsql.aggregate.impl.Avg;
import com.github.villanianalytics.unsql.aggregate.impl.Count;
import com.github.villanianalytics.unsql.aggregate.impl.Distinct;
import com.github.villanianalytics.unsql.aggregate.impl.Everything;
import com.github.villanianalytics.unsql.aggregate.impl.Max;
import com.github.villanianalytics.unsql.aggregate.impl.Min;
import com.github.villanianalytics.unsql.aggregate.impl.Sum;
import com.github.villanianalytics.unsql.condition.Condition;
import com.github.villanianalytics.unsql.condition.impl.Different;
import com.github.villanianalytics.unsql.condition.impl.Equal;
import com.github.villanianalytics.unsql.condition.impl.Greater;
import com.github.villanianalytics.unsql.condition.impl.GreaterOrEqual;
import com.github.villanianalytics.unsql.condition.impl.In;
import com.github.villanianalytics.unsql.condition.impl.Index;
import com.github.villanianalytics.unsql.condition.impl.Less;
import com.github.villanianalytics.unsql.condition.impl.LessOrEqual;
import com.github.villanianalytics.unsql.condition.impl.Like;
import com.github.villanianalytics.unsql.condition.impl.NotIn;
import com.github.villanianalytics.unsql.element.Element;
import com.github.villanianalytics.unsql.element.impl.Array;
import com.github.villanianalytics.unsql.element.impl.JsonElement;
import com.github.villanianalytics.unsql.element.impl.Numeric;
import com.github.villanianalytics.unsql.element.impl.Other;
import com.github.villanianalytics.unsql.exception.UnSqlException;
import com.github.villanianalytics.unsql.extract.ElementsExtract;
import com.github.villanianalytics.unsql.extract.ExtractResult;
import com.github.villanianalytics.unsql.filters.FromFilter;
import com.github.villanianalytics.unsql.filters.QueryFilters;
import com.github.villanianalytics.unsql.model.Result;
import com.github.villanianalytics.unsql.model.SelectStatement;
import com.github.villanianalytics.unsql.validate.ValidateQuery;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;

/**
 * The Class UnSql.
 */
public class UnSql {

	/**
	 * The Enum EXPORT_FORMAT.
	 */
	public enum EXPORT_FORMAT {XML, JSON, VALUES, TEXT }
	
	/** The flat str. */
	private Map<String, Object> flatStr;

	/** The flat str list. */
	private List<String> flatStrList;

	/** The from filter. */
	private FromFilter fromFilter;

	/** The query filters. */
	private QueryFilters queryFilters;

	/** The extract result. */
	private ExtractResult extractResult;

	/** The validate query. */
	private ValidateQuery validateQuery;
	
	/**  The row delimiter. */
	private String rowDelimiter = "|";
	
	/**  The result delimiter. */
	private String resultDelimiter = "\n";
	
	/**  The result with headears. */
	private boolean headers = false;
	
	/** The export format. */
	private EXPORT_FORMAT exportFormat = EXPORT_FORMAT.VALUES;

	/**
	 * Instantiates a new un sql util.
	 *
	 * @param input the input
	 */
	public UnSql(String input) {
		List<Element> elements = createListElements();
		List<AggregateFunction> aggregateFunctions = createListAggregateFunctions();
		
		ElementsExtract elementsExtract = new ElementsExtract(elements);
		List<Condition> conditions = createListCondition(elementsExtract);
		
		this.fromFilter = createFromFilter();
		this.queryFilters = createQueryFilters(conditions);
		this.extractResult = createExtractResult(aggregateFunctions);
		this.validateQuery = createValidateQuery(aggregateFunctions, conditions);
		
		
		processFile(input);
	}
	
	/**
	 * With export format.
	 *
	 * @param format the format
	 * @return the un sql
	 */
	public UnSql withExportFormat(EXPORT_FORMAT format) {
		this.setExportFormat(format);
	    return this;
	}
	
	/**
	 * With row delimiter.
	 *
	 * @param rowDelimiter the row delimiter
	 * @return the un sql
	 */
	public UnSql withRowDelimiter(String rowDelimiter) {
		this.setRowDelimiter(rowDelimiter);
	    return this;
	}
	
	/**
	 * With result delimiter.
	 *
	 * @param resultDelimiter the result delimiter
	 * @return the un sql
	 */
	public UnSql withResultDelimiter(String resultDelimiter) {
		this.setResultDelimiter(resultDelimiter);
	    return this;
	}
	
	/**
	 * With headers.
	 *
	 * @param headers the headers
	 * @return the un sql
	 */
	public UnSql withHeaders(boolean headers) {
		this.setHeaders(headers);
	    return this;
	}

	/**
	 * Creates the query filters.
	 *
	 * @param conditions the conditions
	 * @return the query filters
	 */
	private QueryFilters createQueryFilters(List<Condition> conditions) {
		return new QueryFilters(conditions);
	}

	/**
	 * Creates the from filter.
	 *
	 * @return the from filter
	 */
	private FromFilter createFromFilter() {
		return new FromFilter();
	}

	/**
	 * Creates the extract result.
	 *
	 * @param aggregateFunctions the aggregate functions
	 * @return the extract result
	 */
	private ExtractResult createExtractResult(List<AggregateFunction> aggregateFunctions) {
		return new ExtractResult(aggregateFunctions);
	}

	/**
	 * Creates the validate query.
	 *
	 * @param aggregateFunctions the aggregate functions
	 * @param conditions         the conditions
	 * @return the validate query
	 */
	private ValidateQuery createValidateQuery(List<AggregateFunction> aggregateFunctions, List<Condition> conditions) {
		return new ValidateQuery(aggregateFunctions, conditions);
	}

	/**
	 * Creates the list elements.
	 *
	 * @return the list
	 */
	private List<Element> createListElements() {
		return Arrays.asList(new Array(), new JsonElement(), new Numeric(), new Other());
	}

	/**
	 * Creates the list aggregate functions.
	 *
	 * @return the list
	 */
	private List<AggregateFunction> createListAggregateFunctions() {
		return Arrays.asList(new Sum(), new Min(), new Max(), new Everything(), new Distinct(), new Count(), new Avg());
	}

	/**
	 * Creates the list condition.
	 *
	 * @param elementsExtract the elements extract
	 * @return the list
	 */
	private List<Condition> createListCondition(ElementsExtract elementsExtract) {
		return Arrays.asList(new Different(elementsExtract), new Equal(elementsExtract), new Greater(elementsExtract),
				new GreaterOrEqual(elementsExtract), new In(elementsExtract), new Index(elementsExtract),
				new Less(elementsExtract), new LessOrEqual(elementsExtract), new Like(elementsExtract),
				new NotIn(elementsExtract));
	}

	/**
	 * Run query.
	 *
	 * @param raw   the raw
	 * @param query the query
	 * @return the list
	 * @throws UnSqlException the un sql exception
	 */
	public List<Result> runQuery(String raw, String query) throws UnSqlException {
		this.processFile(raw);
		return this.executeQuery(new SelectStatement(query));
	}

	/**
	 * Process file.
	 *
	 * @param raw the raw
	 */
	public void processFile(String raw) {

		if (raw.startsWith("<")) {
			raw = processXml(raw);
		}

		this.flatStr = JsonFlattener.flattenAsMap(raw);
		this.flatStrList = flatStr.entrySet().stream().map(val -> val.getKey().toLowerCase() + "=" + val.getValue())
				.collect(Collectors.toList());
	}

	/**
	 * Process xml.
	 *
	 * @param raw the raw
	 * @return the string
	 */
	private String processXml(String raw) {
		JSONObject soapDatainJsonObject = XML.toJSONObject(raw);
		return soapDatainJsonObject.toString();
	}

	
	/**
	 * Execute query.
	 *
	 * @param query the query
	 * @return the string
	 * @throws UnSqlException the un sql exception
	 */
	public String execute(String query) throws UnSqlException {
		String ret;
		
		SelectStatement selectStatement = new SelectStatement(query);
		List<Result> results = executeQuery(selectStatement);
		
		switch (this.exportFormat) {
			case XML:
				ret = exportToXML(results);
			break;
			case JSON:
				ret = exportToJson(results);
			break;
			case VALUES:
				ret = exportToValue(results);
			break;
			case TEXT:
				ret = convertResultsToString(results);
			break;
			default:
				ret = exportToValue(results);
			break;
		}
		
		return ret;
	}
	
	
	/**
	 * Export to json.
	 *
	 * @param results the results
	 * @return the string
	 */
	private String exportToJson(List<Result> results) {
		String flatValue = convertResultsToString(results);
		String json = JsonUnflattener.unflatten("{" + flatValue + "}");
		JSONObject jsonFormat = new JSONObject(json);
		
		return jsonFormat.toString();
	}
	
	/**
	 * Export to XML.
	 *
	 * @param results the results
	 * @return the string
	 */
	private String exportToXML(List<Result> results) {
		String flatValue = convertResultsToString(results);
		String json = JsonUnflattener.unflatten("{" + flatValue + "}");
		JSONObject jsonFormat = new JSONObject(json);
		
		return XML.toString(jsonFormat);
	}
	
	/**
	 * Export to value.
	 *
	 * @param results the results
	 * @return the string
	 */
	private String exportToValue(List<Result> results) {
		List<String> values = new ArrayList<>();
		List<String> headersList = !results.isEmpty() ? results.get(0).getHeaders(): new ArrayList<>();
		
		results.forEach(r -> values.add(r.getResultByPattern(headersList,this.rowDelimiter)));
		
		return (headers ? String.join(this.rowDelimiter, headersList) + "\n" : "") + String.join(this.resultDelimiter, values);
	}
	 
	/**
	 * Convert results to string.
	 *
	 * @param results the results
	 * @return the string
	 */
	private String convertResultsToString(List<Result> results) {
		List<String> values = new ArrayList<>();
		results.forEach(r -> values.addAll(r.getResults().entrySet()
                .stream()
                .filter(entry -> !StringUtils.isEmpty(entry.getValue()))
                .map(entry -> ("\""+entry.getKey()+"\":\""+ entry.getValue())+"\"")
                .collect(Collectors.toList())));
		
		return values.stream().map(Object::toString).collect(Collectors.joining(","));
	}
	
	/**
	 * Execute query.
	 *
	 * @param query the query
	 * @return the list
	 * @throws UnSqlException the un sql exception
	 */
	public List<Result> executeQuery(String query) throws UnSqlException {
		return this.executeQuery(new SelectStatement(query));
	}
    
	/**
	 * Execute query.
	 *
	 * @param selectStatement the select statement
	 * @return the list
	 * @throws UnSqlException the un sql exception
	 */
	private List<Result> executeQuery(SelectStatement selectStatement) throws UnSqlException {
		if (flatStr == null)
			throw new UnSqlException("File not defined");
		
		validateQuery.validateSelect(selectStatement);

		Map<String, List<String>> filterfromJson = fromFilter.filterJsonByFromElement(this.getFlatJsonList(),
				selectStatement.getFrom());

		String where = selectStatement.getWhere();
		if (StringUtils.isNotBlank(where)) {
			Predicate<String> predicate = queryFilters.generateFilters(selectStatement.getWhere());

			filterfromJson = filterfromJson.entrySet().stream().collect(Collectors.toMap(Entry::getKey, l -> {
				List<String> concatedList = Arrays.asList(l.getValue().toString());
				List<String> filteredList = concatedList.stream().filter(predicate).collect(Collectors.toList());
				return filteredList.isEmpty() ? new ArrayList<>() : l.getValue();
			}));
		}

		filterfromJson.entrySet().removeIf(s -> s.getValue().isEmpty());

		List<String> resultsJson = filterfromJson.values().stream().flatMap(List::stream).collect(Collectors.toList());

		List<Result> results = extractResult.extract(selectStatement.getFrom(), selectStatement.getSelectElements(), resultsJson);

		return results.stream().filter(r -> r.getResults().size() > 0).collect(Collectors.toList());
	}

	/**
	 * Gets the flat json list.
	 *
	 * @return the flat json list
	 */
	public List<String> getFlatJsonList() {
		return flatStrList;
	}

	/**
	 * Gets the row delimiter.
	 *
	 * @return the row delimiter
	 */
	public String getRowDelimiter() {
		return rowDelimiter;
	}

	/**
	 * Sets the row delimiter.
	 *
	 * @param rowDelimiter the new row delimiter
	 */
	public void setRowDelimiter(String rowDelimiter) {
		this.rowDelimiter = rowDelimiter;
	}

	/**
	 * Gets the result delimiter.
	 *
	 * @return the result delimiter
	 */
	public String getResultDelimiter() {
		return resultDelimiter;
	}

	/**
	 * Sets the result delimiter.
	 *
	 * @param resultDelimiter the new result delimiter
	 */
	public void setResultDelimiter(String resultDelimiter) {
		this.resultDelimiter = resultDelimiter;
	}

	/**
	 * Gets the export format.
	 *
	 * @return the export format
	 */
	public EXPORT_FORMAT getExportFormat() {
		return exportFormat;
	}

	/**
	 * Sets the export format.
	 *
	 * @param exportFormat the new export format
	 */
	public void setExportFormat(EXPORT_FORMAT exportFormat) {
		this.exportFormat = exportFormat;
	}

	/**
	 * Checks if is headers.
	 *
	 * @return true, if is headers
	 */
	public boolean isHeaders() {
		return headers;
	}

	/**
	 * Sets the headers.
	 *
	 * @param headers the new headers
	 */
	public void setHeaders(boolean headers) {
		this.headers = headers;
	}
}
