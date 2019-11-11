package br.com.infox.epp.cliente.manager;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import br.com.infox.util.time.DateRange;

public class CalendarioEventosManagerTest {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter();
	
    private static final Iterator<JsonElement> getTestCases(Class<?> clazz, String name){
    	String fileName = format("/{0}TestCases-{1}.json", clazz.getName(), name);
		System.out.println(fileName);
    	JsonStreamParser jsonStreamParser = new JsonStreamParser(new InputStreamReader(
    			clazz.getResourceAsStream(fileName)));
        return jsonStreamParser.next().getAsJsonArray().iterator();
    }
    
    @Test
	public void testDateMinusBusinessDays(){
		Iterator<JsonElement> testCases = getTestCases(CalendarioEventosManager.class, "dateMinusBusinessDays");
		for (int i = 1; testCases.hasNext(); i++) {
    		JsonObject testCase = testCases.next().getAsJsonObject();
    		String message = format("Test case {0} {1}: ", i, testCase.get("description").getAsString());
    		
    		Date date = toDate(testCase.get("dia").getAsString());
    		int totalBusinessDays=testCase.get("totalDias").getAsInt();
    		List<DateRange> eventos = toDateRange(testCase.getAsJsonArray("eventos"));
    		Date resultadoEncontrado = new CalendarioEventosManager().getDateMinusBusinessDays(date, totalBusinessDays, eventos);
    		Date resultadoEsperado = toDate(testCase.get("resultado").getAsString());
    		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    		assertEquals(message+" Datas inconsistentes", formatter.format(resultadoEsperado), formatter.format(resultadoEncontrado));
    	}
	}

    private DateRange toDateRange(JsonObject range){
    	DateTime start = DateTime.parse(range.get("start").getAsString(), FORMATTER);
    	DateTime end = DateTime.parse(range.get("end").getAsString(), FORMATTER);
    	return new DateRange(start, end);
    }
    
	private List<DateRange> toDateRange(JsonArray dates) {
		List<DateRange> resultDates = new ArrayList<>();
		for (Iterator<JsonElement> iterator = dates.iterator(); iterator.hasNext();) {
			resultDates.add(toDateRange(iterator.next().getAsJsonObject()));
		}
		return resultDates;
	}

	private Date toDate(String date) {
		return DateTime.parse(date, FORMATTER).toDate();
	}
	
}
