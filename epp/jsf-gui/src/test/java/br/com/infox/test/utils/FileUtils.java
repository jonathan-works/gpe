package br.com.infox.test.utils;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class FileUtils {

	private FileUtils(){
	}
	
	public static JsonStreamParser getTestCase(Class<?> testClass, String suffix) {
		String filename = format("/{0}{1}.json", testClass.getName(), suffix);
		URL resource = testClass.getResource(filename);
		try {
			File file = new File(resource.toURI());
			if (!file.exists() && file.createNewFile()) {
				new Gson().toJson(new JsonObject(), new FileWriter(file));
			}
			InputStream inputStream = testClass.getResourceAsStream(filename);
			return new JsonStreamParser(new InputStreamReader(inputStream));
		} catch (URISyntaxException | JsonIOException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
