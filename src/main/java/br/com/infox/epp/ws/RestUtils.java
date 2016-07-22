package br.com.infox.epp.ws;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RestUtils {

	private RestUtils(){
	}
	
	public static String produceErrorJson(String errorMessage){
		return produceErrorJson(errorMessage, new HashMap<String,Object>());
	}
	
	public static String produceErrorJson(String errorMessage, Map<String,Object> properties){
		JsonObject error = new JsonObject();
		error.addProperty("errorMessage", errorMessage);
		Gson gson = new Gson();
		error.add("properties", gson.toJsonTree(properties));
		return gson.toJson(error);
	}
	
	public static String generateLocationURL(UriInfo uriInfo, String codigo) {
		StringBuilder sb = new StringBuilder(uriInfo.getAbsolutePath().toString());
		sb.append("/");
		sb.append(codigo);
		return sb.toString();
	}
	
}
