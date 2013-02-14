package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade de 
 * NaturezaCategoriaFluxo
 * @author Daniel
 *
 */
public interface NaturezaCategoriaFluxoQuery {

	String QUERY_PARAM_NATUREZA = "natureza";
	
	String LIST_BY_NATUREZA = "listNaturezaCategoriaFluxoByNatureza";
	String LIST_BY_NATUREZA_QUERY = "select o from NaturezaCategoriaFluxo o " +
									"where o.natureza = :"+QUERY_PARAM_NATUREZA;
	
	String LIST_CATEGORIA_ATIVO_QUERY = "select o from Categoria o" +
			                            " where o.ativo = true";
	
	String LIST_FLUXO_ATIVO_QUERY = "select o from Fluxo o" +
			                        " where o.ativo = true";
	
}