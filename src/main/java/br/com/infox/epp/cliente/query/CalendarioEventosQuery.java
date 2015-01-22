package br.com.infox.epp.cliente.query;

public interface CalendarioEventosQuery {
    String PARAM_DIA = "dia";
    String PARAM_MES = "mes";
    String PARAM_ANO = "ano";
    
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
}
