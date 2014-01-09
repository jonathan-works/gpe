package br.com.infox.epp.system.query;

public interface ParametroQuery {
    
    String LIST_PARAMETROS_ATIVOS = "listParametrosAtivos";
    String LIST_PARAMETROS_ATIVOS_QUERY = "select o from Parametro o where o.ativo = true";
    
    String TRIGGER_NAMES_PARAM = "triggersNames";
    String MAP_PARAMETRO_TRIGGERS = "mapParametroTriggers";
    String MAP_PARAMETRO_TRIGGERS_QUERY = "select new map(o.nomeVariavel as nomeVariavel, "
            + "o.descricaoVariavel as descricaoVariavel, "
            + "o.valorVariavel as valorVariavel, "
            + "o.idParametro as idParametro,"
            + "case when o.valorVariavel in (:" + TRIGGER_NAMES_PARAM +") then true else false end as valido) "
            + "from Parametro o where o.valorVariavel like '________:___________:_____'";

}
