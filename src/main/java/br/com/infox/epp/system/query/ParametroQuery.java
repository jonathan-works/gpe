package br.com.infox.epp.system.query;

public interface ParametroQuery {
    
    String LIST_PARAMETROS_ATIVOS = "listParametrosAtivos";
    String LIST_PARAMETROS_ATIVOS_QUERY = "select o from Parametro o where o.ativo = true";

}
