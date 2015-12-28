package br.com.infox.epp.processo.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TipoProcesso {
	
	public static final TipoProcesso DOCUMENTO = new TipoProcesso("DOCUMENTO");
	public static final TipoProcesso COMUNICACAO = new TipoProcesso("COMUNICACAO");
	public static final TipoProcesso COMUNICACAO_INTERNA = new TipoProcesso("COMUNICACAO_INTERNA");
	public static final TipoProcesso COMUNICACAO_NAO_ELETRONICA = new TipoProcesso("COMUNICACAO_NAO_ELETRONICA");
	
	protected static Map<String, TipoProcesso> values = new HashMap<>();
	private String value;
	
	static {
		values.put("DOCUMENTO", DOCUMENTO);
		values.put("COMUNICACAO", COMUNICACAO);
		values.put("COMUNICACAO_INTERNA", COMUNICACAO_INTERNA);
		values.put("COMUNICACAO_NAO_ELETRONICA", COMUNICACAO_NAO_ELETRONICA);
	}
	
	public TipoProcesso(String value) {
		if (!isTipoProcessoValido(value)) {
			throw new IllegalArgumentException("Tipo processo " + value + " inválido");
		}
		this.value = value;
	}
	
	public String value() {
		return value;
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
		return ((TipoProcesso) obj).value.equalsIgnoreCase(value);
	}
	
	public static Collection<TipoProcesso> values() {
		return values.values();
	}
	
	public String toString(){
		return value;
	}
	
	protected boolean isTipoProcessoValido(String tipoProcesso) {
		return "DOCUMENTO".equals(tipoProcesso) || "COMUNICACAO".equals(tipoProcesso) || "COMUNICACAO_NAO_ELETRONICA".equals(tipoProcesso)
		        || "COMUNICACAO_INTERNA".equals(tipoProcesso); 
	}
}
