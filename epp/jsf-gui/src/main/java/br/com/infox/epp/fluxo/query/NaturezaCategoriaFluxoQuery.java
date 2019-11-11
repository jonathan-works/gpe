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
	String PARAM_COD_FLUXO = "codFluxo";
	String PARAM_LOCALIZACAO = "localizacao";
	String PARAM_DS_NATUREZA = "dsNatureza";
	String PARAM_DS_CATEGORIA = "dsCategoria";

	String LIST_BY_NATUREZA = "listNaturezaCategoriaFluxoByNatureza";
	String LIST_BY_NATUREZA_QUERY = "select o from NaturezaCategoriaFluxo o " + "where o.natureza = :" + PARAM_NATUREZA;

	String LIST_BY_RELATIONSHIP = "listByNaturezaCategoriaAndFluxo";
	String BY_RELATIONSHIP_QUERY = "select o from NaturezaCategoriaFluxo o where o.natureza = :" + PARAM_NATUREZA
			+ " and o.categoria = :" + PARAM_CATEGORIA + " and o.fluxo = :" + PARAM_FLUXO;

	String ATIVOS_BY_FLUXO = "listAtivosByFluxo";
	String ATIVOS_BY_FLUXO_QUERY = "select ncf from NaturezaCategoriaFluxo ncf " + "inner join ncf.natureza n "
			+ "inner join ncf.categoria c " + "where n.ativo=true " + "and c.ativo=true " + "and ncf.fluxo=:fluxo";

	String NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA = "listNaturezaCatFluxoByDsNaturezaDsCategoria";
	String NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_QUERY = "select o from NaturezaCategoriaFluxo o " + "inner join o.natureza n "
			+ "inner join o.categoria c " + "where UPPER(n.natureza) = :" + PARAM_DS_NATUREZA + " and UPPER(c.categoria) = :"
			+ PARAM_DS_CATEGORIA;

	String NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS = "listNaturezaCatFluxoByDsNaturezaDsCategoriaDisponiveis";
	String NATCATFLUXO_BY_DS_NATUREZA_DS_CATEGORIA_DISPONIVEIS_QUERY = "select o from NaturezaCategoriaFluxo o "
			+ "inner join o.natureza n inner join o.categoria c inner join o.fluxo f where UPPER(n.natureza) = :" + PARAM_DS_NATUREZA
			+ " and UPPER(c.categoria) = :" + PARAM_DS_CATEGORIA + " and f.publicado = true and f.ativo = true and n.ativo = true "
					+ "and c.ativo = true";
}
