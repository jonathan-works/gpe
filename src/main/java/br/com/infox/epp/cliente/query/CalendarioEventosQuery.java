package br.com.infox.epp.cliente.query;

public interface CalendarioEventosQuery {
    String PARAM_DIA = "dia";
    String PARAM_MES = "mes";
    String PARAM_ANO = "ano";
    String PARAM_START_DATE = "start_date";
    String PARAM_END_DATE = "end_date";
    String PARAM_START_YEAR = "start_year";
    String PARAM_END_YEAR = "end_year";
    
    
    String GET_BY_DATA = "getCalendarioEventoByData";
    String GET_BY_DATA_QUERY = "select o from CalendarioEventos o where"
    		+ " ("
    		+ "o.dia = :" + PARAM_DIA
    		+ " and o.mes = :" + PARAM_MES
    		+ " and o.ano = :" + PARAM_ANO
    		+ ") or ("
    		+ "o.dia = :" + PARAM_DIA
    		+ " and o.mes = :" + PARAM_MES
    		+ " and o.ano is null"
    		+ ")";
    String GET_BY_DATA_RANGE = "getCalendarioEventoByDataRange";
    String GET_BY_DATA_RANGE_QUERY = "select c from CalendarioEventos c "
    		+ "where to_date( concat( cast(c.dia as string) ,'/',cast(c.mes as string ) ,'/',cast(c.ano as string ) )  ) between :"+PARAM_START_DATE + " and :"+PARAM_END_DATE 
    		+ " or "
                + "to_date( concat( cast(c.dia as string) ,'/',cast(c.mes as string ) ,'/', cast(:" + PARAM_START_YEAR + " as string) )  ) between :"+PARAM_START_DATE + " and :"+PARAM_END_DATE
                + " or "
                + "to_date( concat( cast(c.dia as string) ,'/',cast(c.mes as string ) ,'/', cast(:" + PARAM_END_YEAR + " as string) )  ) between :"+PARAM_START_DATE + " and :"+PARAM_END_DATE;
}
