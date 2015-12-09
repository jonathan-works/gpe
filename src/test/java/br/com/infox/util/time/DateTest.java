package br.com.infox.util.time;

import static java.text.MessageFormat.format;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class DateTest {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter();
	
	private Date toDate(JsonElement element){
		return new Date(toDateTime(element));
	}

	private DateTime toDateTime(JsonElement element){
		return DateTime.parse(element.getAsString(),FORMATTER);
	}
	
    private DateRange toDateRange(JsonObject periodo) {
        DateTime start = toDateTime(periodo.get("start"));
        DateTime end = toDateTime(periodo.get("end"));
        return new DateRange(start, end);
    }

    private List<DateRange> toDateRange(JsonArray periodos) {
        List<DateRange> ranges = new ArrayList<>();
        for (JsonElement jsonElement : periodos) {
            ranges.add(toDateRange(jsonElement.getAsJsonObject()));
        }
        return ranges;
    }

	@Test
	public void nextWeekdayTest(){
		JsonStreamParser jsonStreamParser = new JsonStreamParser(new InputStreamReader(
                Date.class.getResourceAsStream(format("/{0}TestCases.json", Date.class.getName()))));
		Iterator<JsonElement> testCases = jsonStreamParser.next().getAsJsonObject().get("nextWeekday")
                .getAsJsonArray().iterator();
		for (int i = 1; testCases.hasNext(); i++) {
			JsonObject testCase = testCases.next().getAsJsonObject();
			String message = format("Test case {0} {1}: ", i, testCase.get("description").getAsString());
			Date found = toDate(testCase.get("data"));
			List<DateRange> feriados = toDateRange(testCase.get("feriados").getAsJsonArray());
			found = found.nextWeekday(feriados.toArray(new DateRange[feriados.size()]));
			Date expected = toDate(testCase.get("resultado"));
			Assert.assertEquals(message+"Datas inconsistentes", expected, found);
			
		}
	}
	
}
