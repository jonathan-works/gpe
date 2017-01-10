package br.com.infox.jsf;

public interface AjaxRequestBuilder {

	AjaxRequestBuilder execute(String execute);

	AjaxRequestBuilder render(String render);

	AjaxRequestBuilder param(String name, Object value);

	AjaxRequestBuilder onbegin(String onbegin);

	AjaxRequestBuilder oncomplete(String oncomplete);

	AjaxRequestBuilder onerror(String onerror);

	AjaxRequestBuilder preventDefault();

	AjaxRequestBuilder event(Object event);
	
	AjaxRequestBuilder behavior(String behavior);
	
	String build();

}