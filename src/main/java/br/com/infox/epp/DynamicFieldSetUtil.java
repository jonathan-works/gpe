package br.com.infox.epp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

class DynamicFieldSetUtil {

	private DynamicFieldSetUtil() {
	}

	static String decompress(byte[] data) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
			try (GZIPInputStream gzis = new GZIPInputStream(bais)) {
				try (InputStreamReader isr = new InputStreamReader(gzis, StandardCharsets.UTF_8)) {
					try (StringWriter sw = new StringWriter()) {
						char[] chars = new char[1024];
						for (int len; (len = isr.read(chars)) > 0;) {
							sw.write(chars, 0, len);
						}
						return sw.toString();
					}
				}
			}
		}
	}

	static byte[] compress(String data) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
				try (OutputStreamWriter osw = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {
					osw.write(data);
				}
			}
			return bos.toByteArray();
		}
	}

	static byte[] parseStringToData(String string) {
		return new BaseX().decode(string);
	}

	static String printDataAsString(byte[] data) {
		return new BaseX().encode(data);
	}

	static String fromJsfId(String string) {
		try {
			return decompress(parseStringToData(string.substring(3)));
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to decompress data", e);
		}
	}

	static String toJsfId(String string) {
		try {
			return "df_" + printDataAsString(compress(string));
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to compress string", e);
		}
	}

	static ValueExpression createValueExpression(String expression, Class<?> expectedType) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ExpressionFactory expressionFactory = application.getExpressionFactory();
		return expressionFactory.createValueExpression(context.getELContext(), expression, expectedType);
	}

     static MethodExpression createMethodExpression(String expression, Class<?> expectedReturnType) {
         FacesContext context = FacesContext.getCurrentInstance();
         Application application = context.getApplication();
         ExpressionFactory expressionFactory = application.getExpressionFactory();
         Class<?>[] expectedParamTypes={};
        return expressionFactory.createMethodExpression(context.getELContext(), expression, expectedReturnType, expectedParamTypes);
    }

}
