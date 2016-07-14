package br.com.infox.core.net;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import br.com.infox.core.util.StringUtil;

public class UrlBuilderTest {

    private static String[] URL_VALIDAS_SEM_QUERIES = { "http://www.google.com", "www.google.com",
            "https://www.google.com" };
    private static String[] URL_VALIDAS_COM_QUERIES = { "http://www.google.com?what=123", "www.google.com?what=123",
            "https://www.google.com?what=123" };

    private static String JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJodHRwOi8vZS50Y20ucGEuZ292LmJyL3doYXRldmVyIiwiaHR0cDovL3d3dy5pbmZveC5jb20uYnIvbG9naW4iOiJhZG1pbiIsImp0aSI6IjUwZDg2MGQzLTY5ZTItN2IwYi0xMmJkLWQyN2UzNjlkMDNkNCIsImlhdCI6MTQ2ODQyMTQ5NCwiZXhwIjoxNDcxMDEzNDk0LCJuYmYiOjE0NjgzMzUwOTQsInN1YiI6Imh0dHA6Ly9lLnRjbS5wYS5nb3YuYnIiLCJpc3MiOiJodHRwOi8vd3d3LmluZm94LmNvbS5ici9lcHAifQ.FaTKjQQ13u7cbWYnwWyko-pQPCDxtrcrkN6uWkjdzhq0fS4Rd_YvCEUbUgeHjLIqSDEBeKvlM5YmgvxkWe5xufiTRb6YLmHNynFJESV_9Ow0H77qSskK5PXji5lGEBIdMfsa-LkmoxQ8o5pIm--lfTGcWTIvpMycCL6M5fiQpr3OrTHsS9GB5p7NC_g_lWUzBrdETjmmKc_mPZHaQ_ErDImqNXQG3TUxG1Bh1KyO1bPeMzHl9-JtctrHK0JDfG8etb3Gkg4yzd-oFgEc4vpASDRfFwirwRNjntuTqDyGD1QCjHPUa93C27FMrN-LNHbSf0-5tQalhd_o7Wlhrns0tw";

    @Test
    public void criarUrlValidas() {
        List<Entry<String, String>> queries = new ArrayList<>();
        queries.add(new SimpleImmutableEntry<String, String>("epp.auth.jwt", JWT_TOKEN));
        queries.add(new SimpleImmutableEntry<String, String>("quér2", "é algo com um monte de valor inválido"));
        for (String url : URL_VALIDAS_SEM_QUERIES) {
            String treatedURL = new UrlBuilder(url).queries(queries).build();
            for (int i = 0, l = queries.size(); i < l; i++) {
                Entry<String, String> query = queries.get(i);
                String failMessage = String.format("Failed to append query [ %s=%s ] to [ %s ]", query.getKey(),
                        query.getValue(), url);
                String encodedKey = StringUtil.encodeToUrlSafeString(query.getKey());
                String encodedValue = StringUtil.encodeToUrlSafeString(query.getValue());
                String message = failMessage + " key[ " + encodedKey + " ] value[ " + encodedValue + " ]";
                if (i == 0) {
                    Assert.assertTrue(message, treatedURL.contains(String.format("?%s=%s", encodedKey, encodedValue)));
                } else {
                    Assert.assertTrue(message, treatedURL.contains(String.format("&%s=%s", encodedKey, encodedValue)));
                }
            }
        }
        for (String url : URL_VALIDAS_COM_QUERIES) {
            String treatedURL = new UrlBuilder(url).queries(queries).build();
            for (int i = 0, l = queries.size(); i < l; i++) {
                Entry<String, String> query = queries.get(i);
                String failMessage = String.format("Failed to append query [ %s=%s ] to [ %s ]", query.getKey(),
                        query.getValue(), url);
                String encodedKey = StringUtil.encodeToUrlSafeString(query.getKey());
                String encodedValue = StringUtil.encodeToUrlSafeString(query.getValue());
                String message = failMessage + " key[ " + encodedKey + " ] value[ " + encodedValue + " ]";
                System.out.println(treatedURL);
                Assert.assertTrue(message, treatedURL.contains(String.format("&%s=%s", encodedKey, encodedValue)));
            }
        }
    }
}
