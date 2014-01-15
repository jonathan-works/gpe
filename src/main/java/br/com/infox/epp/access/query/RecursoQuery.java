package br.com.infox.epp.access.query;

public interface RecursoQuery {
    
    String IDENTIFICADOR_PARAM = "identificador";
    String COUNT_RECURSO_BY_IDENTIFICADOR = "countRecursoByIdentificador";
    String COUNT_RECURSO_BY_IDENTIFICADOR_QUERY = "select count(o) from Recurso o where o.identificador = :" 
            + IDENTIFICADOR_PARAM ;
    
    String LISTA_IDENTIFICADORES_PARAM = "identificadores";
    String RECURSOS_FROM_IDENTIFICADORES = "getRecursosFromPermissoes";
    String RECURSOS_FROM_IDENTIFICADORES_QUERY = "select distinct o from Recurso o where o.identificador in (:" 
            + LISTA_IDENTIFICADORES_PARAM + ")";
    
    String RECURSOS_NOT_IN_IDENTIFICADORES = "getRecursosWithoutPermissoes";
    String RECURSOS_NOT_IN_IDENTIFICADORES_QUERY = "select distinct o from Recurso o where o.identificador not in (:" 
            + LISTA_IDENTIFICADORES_PARAM + ")";

}
