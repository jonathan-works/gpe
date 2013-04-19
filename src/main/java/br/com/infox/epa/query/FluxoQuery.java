package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade Fluxo
 * @author tassio
 */
public interface FluxoQuery {

	String FLUXO_PARAM = "fluxo";

	String LIST_ATIVOS = "listFluxoAtivo";
	String LIST_ATIVOS_QUERY = "select o from Fluxo o " +
								"where o.ativo = true";

	String COUNT_PROCESSOS_ATRASADOS = "countProcessosAtrasadosByFluxo";
	String COUNT_PROCESSOS_ATRASADOS_QUERY = "select count(o) from ProcessoEpa o " +
											 "where o.dataFim is null " +
											 "  and o.situacaoPrazo != 'SAT'" +
											 "  and o.naturezaCategoriaFluxo.fluxo = :" + FLUXO_PARAM;
	
}