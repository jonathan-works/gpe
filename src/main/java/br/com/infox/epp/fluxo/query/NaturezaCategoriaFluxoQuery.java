package br.com.infox.epp.fluxo.query;

public interface NaturezaCategoriaFluxoQuery {

    String TABLE_NATUREZA_CATEGORIA_FLUXO = "tb_natureza_categoria_fluxo";
    String SEQUENCE_NATRUEZA_CATEGORIA_FLUXO = "sq_tb_natureza_categoria_fluxo";
    String ID_NATUREZA_CATEGORIA_FLUXO = "id_natureza_categoria_fluxo";
    String ID_NATUREZA = "id_natureza";
    String ID_CATEGORIA = "id_categoria";
    String ID_FLUXO = "id_fluxo";
    String NATUREZA_CATEGORIA_FLUXO_ATTRIBUTE = "naturezaCategoriaFluxo";

    String PARAM_NATUREZA = "natureza";
    String PARAM_CATEGORIA = "categoria";
    String PARAM_FLUXO = "fluxo";

    String LIST_BY_NATUREZA = "listNaturezaCategoriaFluxoByNatureza";
    String LIST_BY_NATUREZA_QUERY = "select o from NaturezaCategoriaFluxo o "
            + "where o.natureza = :" + PARAM_NATUREZA;

    String LIST_BY_RELATIONSHIP = "listByNaturezaCategoriaAndFluxo";
    String BY_RELATIONSHIP_QUERY = "select o from NaturezaCategoriaFluxo o where o.natureza = :"
            + PARAM_NATUREZA
            + " and o.categoria = :"
            + PARAM_CATEGORIA
            + " and o.fluxo = :" + PARAM_FLUXO;

    String ATIVOS_BY_FLUXO = "listAtivosByFluxo";
    String ATIVOS_BY_FLUXO_QUERY = "select ncf from NaturezaCategoriaFluxo ncf "
            + "inner join ncf.natureza n "
            + "inner join ncf.categoria c "
            + "where n.ativo=true "
            + "and c.ativo=true "
            + "and ncf.fluxo=:fluxo";
}
