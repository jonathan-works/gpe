package br.com.infox.epp.cliente.query;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA_FIM;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA_INICIO;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DESCRICAO_EVENTO;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.LOCALIZACAO;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_END_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.PARAM_START_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.SERIE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.TIPO_EVENTO;

public interface CalendarioEventosQuery {
    public interface Param {
        String PARAM_START_DATE = "start_date";
        String PARAM_END_DATE = "end_date";

        String TIPO_EVENTO = "tipoEvento";
        String DATA_FIM = "dataFim";
        String DATA_INICIO = "dataInicio";
        String DESCRICAO_EVENTO = "descricaoEvento";
        String LOCALIZACAO = "localizacao";
        String DATA = "data";
        String SERIE = "serie";
    }
    
    String GET_BY_DATA = "getCalendarioEventoByData";
    String GET_BY_DATA_QUERY = "select o from CalendarioEventos o where" + " :" + DATA
            + " between o.dataInicio and o.dataFim";
    String GET_BY_DATA_RANGE = "getCalendarioEventoByDataRange";
    String GET_BY_DATA_RANGE_QUERY = "select c from CalendarioEventos c " + "where c.dataInicio between :"
            + PARAM_START_DATE + " and :" + PARAM_END_DATE + " or c.dataFim between :"
            + PARAM_START_DATE + " and :" + PARAM_END_DATE;
    

    String GET_CALENDARIO_BY = "CalendarioEventos.getBy";
    String GET_CALENDARIO_BY_QUERY = "select cd from CalendarioEventos cd"
            + " where cd.localizacao=:" + LOCALIZACAO
            + " and cd.descricaoEvento=:" + DESCRICAO_EVENTO
            + " and cd.dataInicio=:" + DATA_INICIO
            + " and cd.dataFim = :" + DATA_FIM
            + " and cd.tipoEvento = :" + TIPO_EVENTO;
    
    String GET_PERIODICOS_NAO_ATUALIZADOS="CalendarioEventos.getPeriodicosNaoAtualizados";
    String GET_PERIODICOS_NAO_ATUALIZADOS_QUERY = "select cd from CalendarioEventos cd"
            + " where cd.dataFim < :" + DATA
            + " and cd.serie is not null"
            + " and not exists(select 1 from CalendarioEventos future where future.serie=cd.serie and future.dataFim > cd.dataFim)";
    String GET_BY_SERIE="CalendarioEventos.getBySerie";
    String GET_BY_SERIE_QUERY = "select cd from CalendarioEventos cd where cd.serie = :" + SERIE;
    String GET_ULTIMO_EVENTO_SERIE="CalendarioEventos.getUltimoEventoSerie";
    String GET_ULTIMO_EVENTO_SERIE_QUERY = GET_BY_SERIE_QUERY
            + " and cd.serie is not null order by cd.dataInicio desc, cd.dataFim desc";
    String GET_BY_SERIE_AFTER_DATE="CalendarioEventos.getBySerieAfterDate";
    String GET_BY_SERIE_AFTER_DATE_QUERY = GET_BY_SERIE_QUERY + " and cd.dataInicio >= :" + DATA_FIM;
    String GET_ORPHAN_SERIES="SerieEventos.getOrphanSeries";
    String GET_ORPHAN_SERIES_QUERY = "select s from CalendarioEventos cd right join cd.serie s where cd is null";

}
