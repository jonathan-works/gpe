package br.com.infox.core.net;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.http.client.utils.URIBuilder;

import br.com.infox.core.exception.SystemExceptionFactory;

public class UrlBuilder {
    
    private final String baseUrl;
    private final Map<String,String> queries;
    private final Queue<String> path;
    
    public UrlBuilder(String baseUrl){
        this.baseUrl = baseUrl;
        this.queries = new HashMap<>();
        this.path = new LinkedList<>();
    }
    
    public UrlBuilder path(String path){
        this.path.add(path);
        return this;
    }
    
    public UrlBuilder query(String key, String value){
        this.queries.put(key, value);
        return this;
    }

    public UrlBuilder queries(Map<String,String> map){
        this.queries.putAll(map);
        return this;
    }
    
    public UrlBuilder queries(Iterable<Entry<String, String>> iterable){
        for (Entry<String, String> entry : iterable) {
            query(entry);
        }
        return this;
    }
    
    public UrlBuilder query(Entry<String, String> entry) {
        this.queries.put(entry.getKey(), entry.getValue());
        return this;
    }
    
    public String build() {
        StringBuilder sb = new StringBuilder();
        if (!baseUrl.matches("^\\w+://.+")) {
            sb.append("http://");
        }
        sb.append(baseUrl);
        for (String string : path) {
            for (String split : string.split("/")) {
                if (!split.trim().isEmpty())
                sb.append("/").append(split);
            }
        }
        
        try {
            String baseUrl = sb.toString();
            URIBuilder uriBuilder = new URIBuilder(baseUrl);
            for (Entry<String, String> entry : queries.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            return uriBuilder.build().toString();
        } catch (URISyntaxException e){
            throw SystemExceptionFactory.create(URLErrorCode.URL_SYNTAX).set("baseURL", baseUrl).set("urlQueries", queries);
        }
    }
    
}
