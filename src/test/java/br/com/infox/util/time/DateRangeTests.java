package br.com.infox.util.time;

import static java.text.MessageFormat.format;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class DateRangeTests {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT).toFormatter();

	private DateTime toDateTime(JsonElement element){
		return DateTime.parse(element.getAsString(), FORMATTER);
	}
	
    private DateRange toDateRange(JsonObject periodo) {
    	DateTime start = toDateTime(periodo.get("start"));
    	DateTime end = toDateTime(periodo.get("end"));
        return new DateRange(start, end);
    }

    private DateRange toDateRangeWithPrazo(JsonObject periodo) {
    	DateTime start = toDateTime(periodo.get("start"));
    	DateTime end = start.plusDays(periodo.get("prazo").getAsInt()).withTime(23, 59, 59, 999);
		return new DateRange(start, end);
	}

    private List<DateRange> toDateRange(JsonArray periodos) {
        List<DateRange> ranges = new ArrayList<>();
        for (JsonElement jsonElement : periodos) {
            ranges.add(toDateRange(jsonElement.getAsJsonObject()));
        }
        return ranges;
    }

    private void assertConsistencia(DateRange resultadoEsperado, DateRange resultado, String message){
        Assert.assertEquals(message+"Data de inicio inconsistente", resultadoEsperado.getStart().toString(DATE_FORMAT), resultado.getStart().toString(DATE_FORMAT));
        Assert.assertEquals(message+"Data de fim inconsistente", resultadoEsperado.getEnd().toString(DATE_FORMAT), resultado.getEnd().toString(DATE_FORMAT));
    }
    
    private Collection<DateRange> sort(Collection<DateRange> collection){
        Collection<DateRange> result = new TreeSet<>(new Comparator<DateRange>() {
            @Override
            public int compare(DateRange o1, DateRange o2) {
                int compareStart = o1.getStart().compareTo(o2.getStart());
                return compareStart != 0 ? compareStart : o1.getEnd().compareTo(o2.getEnd());
            }
        });
        result.addAll(collection);
        return result;
    }
    
    private static final Iterator<JsonElement> getTestCases(Class<?> clazz, String name){
    	JsonStreamParser jsonStreamParser = new JsonStreamParser(new InputStreamReader(
    			clazz.getResourceAsStream(format("/{0}TestCases-{1}.json", clazz.getName(), name))));
        return jsonStreamParser.next().getAsJsonArray().iterator();
    }
    
    
    @Test
    public void suspensoesPrazo(){
    	Iterator<JsonElement> testCases = getTestCases(DateRange.class, "suspensoesPrazo");
    	for (int i = 1; testCases.hasNext(); i++) {
    		JsonObject testCase = testCases.next().getAsJsonObject();
    		String message = format("Test case {0} {1}: ", i, testCase.get("description").getAsString());
    		List<DateRange> suspensoes = toDateRange(testCase.get("suspensoes").getAsJsonArray());
			DateRange resultadoEncontrado = toDateRangeWithPrazo(testCase.getAsJsonObject("periodo"));
			resultadoEncontrado = resultadoEncontrado.withSuspensoes(suspensoes);
			DateRange resultadoEsperado = toDateRange(testCase.getAsJsonObject("resultado"));
			assertConsistencia(resultadoEsperado, resultadoEncontrado, message);
    	}
    }
    
	@Test
    public void juncaoDePeriodos() {
        Iterator<JsonElement> testCases = getTestCases(DateRange.class,"juncaoDePeriodos");
        for (int i = 1; testCases.hasNext(); i++) {
            JsonObject testCase = testCases.next().getAsJsonObject();
            List<DateRange> periodos = toDateRange(testCase.get("periodos").getAsJsonArray());
            Collection<DateRange> resultadoEsperado = sort(toDateRange(testCase.get("resultados").getAsJsonArray()));
            String message = format("Test case {0} {1}: ", i, testCase.get("description").getAsString());
            
            Collection<DateRange> resultado = sort(DateRange.reduce(periodos));
            Assert.assertEquals(message+"Quantidade de períodos inconsistente", resultadoEsperado.size(), resultado.size());
            for (Iterator<DateRange> expected = resultadoEsperado.iterator(), found = resultado.iterator(); expected.hasNext();) {
                assertConsistencia(expected.next(), found.next(), message);
            }
        }
    }

}
