package br.com.infox.epa.query;

public interface NatCatFluxoLocalizacaoQuery {

	String QUERY_PARAM_NAT_CAT_FLUXO = "naturezaCategoriaFluxo";
	String QUERY_PARAM_LOCALIZACAO = "localizacao";
	String QUERY_PARAM_PAPEL = "papel";
	
	String DELETE_BY_NAT_CAT_FLUXO_AND_LOCALIZCAO = 
			   "delete from NatCatFluxoLocalizacao ncfl where " +
			   "ncfl.naturezaCategoriaFluxo = :"+QUERY_PARAM_NAT_CAT_FLUXO+
			   " and ncfl.localizacao = :"+QUERY_PARAM_LOCALIZACAO;

	String GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF = "getNatCatFluxoLocalizacaoByLocNCF";
	String GET_NAT_CAT_FLUXO_LOCALIZACAO_BY_LOC_NCF_QUERY = 
			   "select o from NatCatFluxoLocalizacao o where " +
			   "o.naturezaCategoriaFluxo = :"+QUERY_PARAM_NAT_CAT_FLUXO+
			   " and o.localizacao = :"+QUERY_PARAM_LOCALIZACAO;

	String COUNT_NCF_LOCALIZACAO_BY_LOC_NCF = "countNatCatFluxoLocByLocNCF";
	String COUNT_NCF_LOCALIZACAO_BY_LOC_NCF_QUERY = 
		   "select count(o) from NatCatFluxoLocalizacao o where " +
		   "o.naturezaCategoriaFluxo = :"+QUERY_PARAM_NAT_CAT_FLUXO+
		   " and o.localizacao = :"+QUERY_PARAM_LOCALIZACAO;
	
	String LIST_BY_LOCALIZACAO_AND_PAPEL = "listNatCatFluxoLocalizacaoByLocalizacaoAndPapel";
	String LIST_BY_LOCALIZACAO_AND_PAPEL_QUERY = 
		   "select ncf from NatCatFluxoLocalizacao o " +
		   "inner join o.naturezaCategoriaFluxo ncf " +
		   "inner join ncf.fluxo.fluxoPapelList papelList where " +
		   "o.localizacao = :"+QUERY_PARAM_LOCALIZACAO+
		   " and papelList.papel = :"+QUERY_PARAM_PAPEL;
	
	String LIST_NATUREZA_ATIVO_QUERY = "select o from Natureza o";
	String LIST_CATEGORIA_ATIVO_QUERY = "select o from Categoria o";
	String LIST_FLUXO_ATIVO_QUERY = "select o from Fluxo o";
}