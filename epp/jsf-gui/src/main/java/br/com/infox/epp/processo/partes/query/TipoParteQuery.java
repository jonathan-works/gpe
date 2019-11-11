package br.com.infox.epp.processo.partes.query;

public interface TipoParteQuery {
	
	String PARAM_IDENTIFICADOR = "identificador";
	
	String TIPO_PARTE_BY_IDENTIFICADOR = "TipoParte.identificador";
	String TIPO_PARTE_BY_IDENTIFICADOR_QUERY = "select o from TipoParte o " + 
			"where o.identificador = :" + PARAM_IDENTIFICADOR;

}
