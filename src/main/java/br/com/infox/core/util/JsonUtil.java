package br.com.infox.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {

    public static String toJson(Object object) {
        Gson gson = createDefault();
        return toJson(gson, object);
    }

    public static String toJson(Object object, String datePattern) {
        Gson gson = createDefault(datePattern);
        return toJson(gson, object);
    }

    public static <T> T fromJson(String jsonString, Class<T> type) {
        Gson gson = createDefault();
        return fromJson(gson, jsonString, type);
    }

    public static <T> T fromJson(String jsonString, Class<T> type, String datePattern) {
        Gson gson = createDefault(datePattern);
        return fromJson(gson, jsonString, type);
    }
    
    private static Gson createDefault() {
        return createDefault(null);
    }
    
    private static Gson createDefault(String datePattern) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (!StringUtil.isEmpty(datePattern)) {
            gsonBuilder.setDateFormat(datePattern);
        }
        return gsonBuilder.setPrettyPrinting().serializeNulls().create();
    }

    private static String toJson(Gson gson, Object object) {
        return gson.toJson(object);
    }

    private static <T> T fromJson(Gson gson, String jsonString, Class<T> type) {
        return gson.fromJson(jsonString, type);
    }

}
