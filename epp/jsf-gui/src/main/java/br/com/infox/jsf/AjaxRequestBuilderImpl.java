package br.com.infox.jsf;

import static br.com.infox.core.util.ObjectUtil.is;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.google.gson.Gson;

class AjaxRequestBuilderImpl implements AjaxRequestPreBuilder, AjaxRequestBuilder {

	private final FacesContext context;

	private String source;
	private String event;

	// OPTIONS
	private String execute;
	private String render;
	private Map<String, Object> params;
	private String onbegin;
	private String oncomplete;
	private String onerror;
	private String behavior;
	private boolean preventDefault;

	AjaxRequestBuilderImpl(FacesContext facesContext) {
		this.context = facesContext;
		this.params = new HashMap<>();
		this.preventDefault = false;
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		
                if (is(onbegin).notEmpty()) {
                        sb.append(onbegin);
                        if (!onbegin.endsWith(";"))
                                sb.append(";");
                }
                
		sb.append("jsf.ajax.request(");
		sb.append(String.format("\"%s\"", source));

		final boolean hasOptions = hasOptions();
		final boolean hasEvent = is(event).notEmpty();

		if (hasOptions || hasEvent)
			sb.append(",").append(hasEvent ? event : "null");

		if (hasOptions)
			sb.append(",").append(getOptions());

		sb.append(");");

		if (preventDefault)
			sb.append("event.preventDefault();").append("return false;");

		return sb.toString();
	}

	private boolean hasOptions() {
		return is(execute).notEmpty() || is(render).notEmpty() || is(oncomplete).notEmpty() 
				|| is(onerror).notEmpty()
				|| is(params).notEmpty() || is(behavior).notEmpty();
	}

	private String getOptions() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		boolean hasText = false;
		
		if (is(execute).notEmpty()) {
			sb.append("\"execute\":\"").append(execute).append("\"");
			hasText = true;
		}

		if (is(behavior).notEmpty()) {
		    if (hasText)
		        sb.append(",");
		    sb.append("\"javax.faces.behavior.event\":\"").append(behavior).append("\"");
		    hasText = true;
		}
		
		if (is(render).notEmpty()) {
			if (hasText)
				sb.append(",");
			sb.append("\"render\":\"").append(render).append("\"");
			hasText = true;
		}

		if (is(oncomplete).notEmpty()) {
			if (hasText)
				sb.append(",");
			JSFunctionBuilder jsFunctionBuilder = AjaxRequestBuilderFactory.jsFunction();
			jsFunctionBuilder.arg("event");
			StringBuilder sbStatement = new StringBuilder();
			sbStatement.append("if (event.type === 'event') {");
			sbStatement.append("switch(event.status) {");
			sbStatement.append("case 'success': ");
			sbStatement.append(oncomplete);
			sbStatement.append("break;");
			sbStatement.append("}}");
			jsFunctionBuilder.statement(sbStatement.toString());
			sb.append("\"onevent\":").append(jsFunctionBuilder.build());
		}

		if (is(onerror).notEmpty()) {
			if (hasText)
				sb.append(",");
			JSFunctionBuilder jsFunctionBuilder = AjaxRequestBuilderFactory.jsFunction();
			jsFunctionBuilder.arg("event");
			jsFunctionBuilder.statement(oncomplete);
			sb.append("\"onerror\":").append(jsFunctionBuilder.build());
		}

		if (is(params).notEmpty()) {
			if (hasText)
				sb.append(",");
			sb.append("\"params\":").append(getRequestParams());
		}

		sb.append("}");
		return sb.toString();
	}

	private String toJson(Object value) {
		return new Gson().toJson(value);
	}

	private String getRequestParams() {
		StringBuilder sb = new StringBuilder();
		sb.append(" { ");
		boolean written = false;
		for (Entry<String, Object> entry : params.entrySet()) {
			if (written)
				sb.append(",");

			sb.append(String.format("\"%s\":%s", entry.getKey(), toJson(entry.getValue())));
			written = true;
		}
		sb.append(" } ");
		return sb.toString();
	}

	public AjaxRequestBuilder event(Object event) {
		this.event = toJson(event);
		return this;
	}

	public AjaxRequestBuilder execute(String execute) {
		this.execute = execute;
		return this;
	}

	public AjaxRequestBuilder render(String render) {
		this.render = render;
		return this;
	}

	public AjaxRequestBuilder param(String name, Object value) {
		params.put(name, value);
		return this;
	}

	public AjaxRequestBuilder onbegin(String onbegin) {
		this.onbegin = onbegin;
		return this;
	}

	public AjaxRequestBuilder oncomplete(String oncomplete) {
		this.oncomplete = oncomplete;
		return this;
	}
    
        public AjaxRequestBuilder behavior(String behavior) {
            this.behavior = behavior;
            return this;
        }
        
	public AjaxRequestBuilder onerror(String onerror) {
		this.onerror = onerror;
		return this;
	}

	public AjaxRequestBuilder preventDefault() {
		this.preventDefault = true;
		return this;
	}

	public AjaxRequestBuilder from(UIComponent component) {
		this.source = component.getClientId(context);
		return this;
	}

	public AjaxRequestBuilder from(String clientId) {
		this.source = clientId;
		return this;
	}

}