package br.com.infox.epp.processo.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TipoProcesso {
	
	public static final TipoProcesso DOCUMENTO = new TipoProcesso("DOCUMENTO");
	public static final TipoProcesso COMUNICACAO = new TipoProcesso("COMUNICACAO");
	
	protected static Map<String, TipoProcesso> values = new HashMap<>();
	private String value;
	
	static {
		values.put("DOCUMENTO", DOCUMENTO);
		values.put("COMUNICACAO", COMUNICACAO);
	}
	
	protected TipoProcesso(String value) {
		this.value = value;
	}
	
	public static TipoProcesso getByName(String name) {
		name = name.toUpperCase();
		return values.get(name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TipoProcesso)) {
			return false;
		}
		return ((TipoProcesso) obj).value.equals(value);
	}
	
	public static Collection<TipoProcesso> values() {
		return values.values();
	}
	
	public String toString(){
		return value;
	}
}
