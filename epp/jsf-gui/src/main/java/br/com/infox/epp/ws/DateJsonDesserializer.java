package br.com.infox.epp.ws;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateJsonDesserializer extends JsonDeserializer<Date>{
	
	public static String datePattern = "yyyy-MM-ddThh:mm:ssZ";
	public static DateFormat dateFormat = new SimpleDateFormat(datePattern);

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String dataString = jp.getText();
		try {
			return dataString != null ? dateFormat.parse(dataString) : null;
		} catch (ParseException e) {
			throw new JsonParseException("Não foi possível deserializar a data no formato: " + datePattern, jp.getCurrentLocation());
		}
	}

}
