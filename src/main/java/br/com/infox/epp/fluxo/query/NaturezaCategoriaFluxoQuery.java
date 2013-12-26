package br.com.infox.epp.fluxo.query;

/**
 * Interface com as queries da entidade de NaturezaCategoriaFluxo
 * 
 * @author Daniel
 * 
 */
public interface NaturezaCategoriaFluxoQuery {

    String PARAM_NATUREZA = "natureza";
    String PARAM_CATEGORIA = "categoria";
    String PARAM_FLUXO = "fluxo";

    String LIST_BY_NATUREZA = "listNaturezaCategoriaFluxoByNatureza";
    String LIST_BY_NATUREZA_QUERY = "select o from NaturezaCategoriaFluxo o "
            + "where o.natureza = :" + PARAM_NATUREZA;

    String LIST_CATEGORIA_ATIVO_QUERY = "select o from Categoria o"
            + " where o.ativo = true";

    String LIST_FLUXO_ATIVO_QUERY = "select o from Fluxo o"
            + " where o.ativo = true";

    String BY_RELATIONSHIP_QUERY = "select o from NaturezaCategoriaFluxo o where o.natureza = :"
            + PARAM_NATUREZA
            + " and o.categoria = :"
            + PARAM_CATEGORIA + " and o.fluxo = :" + PARAM_FLUXO;
}
