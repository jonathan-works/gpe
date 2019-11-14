package br.com.infox.seam.util;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Expressions;

@Name(EventCache.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class EventCache {
	public static final String NAME = "eventCache";
	
	private Map<String, Object> cache = new HashMap<>();
	
	public Object get(String expression) {
		return get(expression, null);
	}
	
	public Object get(String expression, Object customizer) {
		Object value;
		String key = customizer != null ? expression + customizer.toString() : expression;
		synchronized (cache) {
			value = cache.get(key);
			if (value == null) {
				value = Expressions.instance().createValueExpression("#{" + expression + "}").getValue();
				cache.put(key, value);
			}
		}
		return value;
	}
	
	@Destroy
	public void destroy() {
		cache = null;
	}
}
