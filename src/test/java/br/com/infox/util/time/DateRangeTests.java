package br.com.infox.util.time;

import static java.text.MessageFormat.format;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class DateRangeTests {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateRange toDateRange(JsonObject periodo) {
        LocalDate start = new LocalDate(periodo.get("start").getAsString());
        LocalDate end = new LocalDate(periodo.get("end").getAsString());
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
    public void juncaoDePeriodos() {
        JsonStreamParser jsonStreamParser = new JsonStreamParser(new InputStreamReader(
                DateRange.class.getResourceAsStream(format("/{0}TestCases.json", DateRange.class.getName()))));
        Iterator<JsonElement> testCases = jsonStreamParser.next().getAsJsonObject().get("juncoesDePeriodos")
                .getAsJsonArray().iterator();
        for (int i = 1; testCases.hasNext(); i++) {
            JsonObject testCase = testCases.next().getAsJsonObject();
            List<DateRange> periodos = toDateRange(testCase.get("periodos").getAsJsonArray());
            DateRange resultadoEsperado = toDateRange(testCase.get("resultado").getAsJsonObject());
            String message = format("Test case {0} {1}: ", (i + 1), testCase.get("description").getAsString());
            
            DateRange resultado = DateRange.merge(periodos);
            Assert.assertEquals(message+"Data de inicio inconsistente", resultadoEsperado.getStart().toString(DATE_FORMAT), resultado.getStart().toString(DATE_FORMAT));
            Assert.assertEquals(message+"Data de fim inconsistente", resultadoEsperado.getEnd().toString(DATE_FORMAT), resultado.getEnd().toString(DATE_FORMAT));
        }
    }

}
