package br.com.infox.jsf;

public interface JSFunctionBuilder {

	JSFunctionBuilder name(String arg);

	JSFunctionBuilder arg(String arg);

	JSFunctionBuilder statement(String oncomplete);

	String build();
	
}