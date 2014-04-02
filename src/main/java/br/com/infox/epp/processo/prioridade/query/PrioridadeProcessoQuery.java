package br.com.infox.epp.processo.prioridade.query;

public interface PrioridadeProcessoQuery {

    String NAMED_QUERY_PRIORIDADES_ATIVAS = "PrioridadeProcesso.prioridadesAtivas";
    String QUERY_PRIORIDADES_ATIVAS = "select o from PrioridadeProcesso o where o.ativo = true order by o.peso";
}
