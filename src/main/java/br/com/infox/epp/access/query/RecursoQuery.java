package br.com.infox.epp.access.query;

public interface RecursoQuery {
    
    String IDENTIFICADOR_PARAM = "identificador";
    String COUNT_RECURSO_BY_IDENTIFICADOR = "countRecursoByIdentificador";
    String COUNT_RECURSO_BY_IDENTIFICADOR_QUERY = "select count(o) from Recurso o where o.identificador = :" 
            + IDENTIFICADOR_PARAM ;

}
