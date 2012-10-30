package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade Fluxo
 * @author tassio
 */
public interface FluxoQuery {

	String LIST_ATIVOS = "listFluxoAtivo";
	String LIST_ATIVOS_QUERY = "select o from Fluxo o " +
								"where o.ativo = true";
	
}