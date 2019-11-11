package br.com.infox.ibpm.event;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapBuilder {
	
	private Map<Object, Object> map = new HashMap<>();
	
	public ObjectMapBuilder add(Object chave, Object valor) {
		if (chave != null && valor != null) {
			map.put(chave, valor);
		}
		return this;
	}
	
	public Map<Object, Object> build() {
		return map;
	}
}
