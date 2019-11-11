package br.com.infox.epp.processo.prioridade.query;

public interface PrioridadeProcessoQuery {

    String TABLE_NAME = "tb_prioridade_processo";
    String COLUMN_ID = "id_prioridade_processo";
    String COLUMN_DESCRICAO = "ds_prioridade_processo";
    String COLUMN_NR_PESO = "nr_peso";

    String NAMED_QUERY_PRIORIDADES_ATIVAS = "PrioridadeProcesso.prioridadesAtivas";
    String QUERY_PRIORIDADES_ATIVAS = "select o from PrioridadeProcesso o where o.ativo = true order by o.peso desc";
}
