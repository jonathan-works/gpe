package br.com.infox.epp.processo.type;

public enum TipoProcesso {
	
	DOCUMENTO, COMUNICACAO;
	
	public static TipoProcesso getByName(String name) {
		for (TipoProcesso tp : values()) {
			if (tp.name().equalsIgnoreCase(name)){
				return tp;
			}
		}
		return null;
	}

}
