package br.com.infox.epp.fluxo.query;

public interface NatCatFluxoLocalizacaoQuery {

    String TABLE_NAT_CAT_FLUXO_LOCALIZACAO = "tb_nat_cat_fluxo_localizacao";
    String SEQUENCE_NAT_CAT_FLUXO_LOCALIZACAO = "sq_nat_cat_fluxo_localizacao";
    String ID_NAT_CAT_FLUXO_LOCALIZACAO = "id_nat_cat_fluxo_localizacao";
    String ID_NAT_CAT_FLUXO = "id_nat_cat_fluxo";
    String ID_LOCALIZACAO = "id_localizacao";
    String HERANCA = "in_heranca";

    String QUERY_PARAM_NAT_CAT_FLUXO = "naturezaCategoriaFluxo";
    String QUERY_PARAM_LOCALIZACAO = "localizacao";
    String QUERY_PARAM_PAPEL = "papel";
    String QUERY_PARAM_COD_FLUXO = "codFluxo";

    String DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZACAO = "delete from NatCatFluxoLocalizacao ncfl where "
            + "ncfl.naturezaCategoriaFluxo = :"
            + QUERY_PARAM_NAT_CAT_FLUXO
            + " and ncfl.localizacao = :" + QUERY_PARAM_LOCALIZACAO;

    String GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF = "getNatCatFluxoLocalizacaoByLocNCF";
    String GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY = "select o from NatCatFluxoLocalizacao o where "
            + "o.naturezaCategoriaFluxo = :"
            + QUERY_PARAM_NAT_CAT_FLUXO
            + " and o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;

    String COUNT_NCF_LOCALIZACAO_BY_LOC_NCF = "countNatCatFluxoLocByLocNCF";
    String COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY = "select count(o) from NatCatFluxoLocalizacao o where "
            + "o.naturezaCategoriaFluxo = :"
            + QUERY_PARAM_NAT_CAT_FLUXO
            + " and o.localizacao = :" + QUERY_PARAM_LOCALIZACAO;

    String LIST_BY_LOCALIZACAO_AND_PAPEL = "listNatCatFluxoLocalizacaoByLocalizacaoAndPapel";
    String LIST_BY_LOCALIZACAO_AND_PAPEL_QUERY = "select ncf from NatCatFluxoLocalizacao o "
            + "inner join o.naturezaCategoriaFluxo ncf "
            + "inner join ncf.fluxo.fluxoPapelList papelList where "
            + "o.localizacao = :"
            + QUERY_PARAM_LOCALIZACAO
            + " and papelList.papel = :" + QUERY_PARAM_PAPEL;

    String LIST_BY_NAT_CAT_FLUXO = "listByNatCatFluxo";
    String LIST_BY_NAT_CAT_FLUXO_QUERY = "select ncfl from NatCatFluxoLocalizacao ncfl "
            + "where ncfl.naturezaCategoriaFluxo = :"
            + QUERY_PARAM_NAT_CAT_FLUXO;

    String LIST_NATUREZA_ATIVO_QUERY = "select o from Natureza o";
    String LIST_CATEGORIA_ATIVO_QUERY = "select o from Categoria o";
    String LIST_FLUXO_ATIVO_QUERY = "select o from Fluxo o";
}
