package com.github.villanianalytics.unsql.utils;

import java.util.Arrays;
import java.util.List;

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
import com.github.villanianalytics.unsql.extract.ElementsExtract;

public class TestUtils {

	public static ElementsExtract createElementsExtract() {
		return new ElementsExtract(TestUtils.createListElements());
	}

	public static List<Element> createListElements() {
		return Arrays.asList(new Array(), new JsonElement(), new Numeric(), new Other());
	}

	public static List<Condition> createListCondition() {
		ElementsExtract elementsExtract = createElementsExtract();
		return Arrays.asList(new Different(elementsExtract), new Equal(elementsExtract), new Greater(elementsExtract),
				new GreaterOrEqual(elementsExtract), new In(elementsExtract), new Index(elementsExtract),
				new Less(elementsExtract), new LessOrEqual(elementsExtract), new Like(elementsExtract),
				new NotIn(elementsExtract));
	}

	public static List<AggregateFunction> createListAggregateFunctions() {
		return Arrays.asList(new Sum(), new Min(), new Max(), new Everything(), new Distinct(), new Count(), new Avg());
	}
}
