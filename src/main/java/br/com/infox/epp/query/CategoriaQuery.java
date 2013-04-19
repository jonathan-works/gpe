package br.com.infox.epp.query;

/**
 * Interface com as queries da entidade de 
 * Categoria
 * @author Daniel
 *
 */
public interface CategoriaQuery {

	String QUERY_PARAM_CATEGORIA = "categoria";

	String LIST_PROCESSO_EPA_BY_CATEGORIA = "listProcessoEpaByCategoria";
	
	String LIST_PROCESSO_EPA_BY_CATEGORIA_QUERY = 
		"select distinct(c), " +
		"(select count(p) from ProcessoEpa p " +
		"inner join p.naturezaCategoriaFluxo ncf " +
		"inner join ncf.categoria cat where cat = c), " +
		"(select count(pTarefa) from ProcessoEpaTarefa pTarefa " +
		"inner join pTarefa.processoEpa pEpa " +
		"inner join pEpa.naturezaCategoriaFluxo ncf " +
		"inner join ncf.categoria cat " +
		"where cat = c and pTarefa.porcentagem <= 100 " +
		"and pTarefa.dataFim is null), " +
		"(select count(p) from ProcessoEpa p " +
		"inner join p.naturezaCategoriaFluxo ncf " +
		"where ncf.categoria = c and " +
		"p.porcentagem <= 100), " +
		"(select count(pTarefa) from ProcessoEpaTarefa pTarefa " +
		"inner join pTarefa.processoEpa pEpa " +
		"inner join pEpa.naturezaCategoriaFluxo ncf " +
		"inner join ncf.categoria cat " +
		"where cat = c and pTarefa.porcentagem > 100) " +
		"from Categoria c " +
		"inner join c.naturezaCategoriaFluxoList ncfList " +
		"inner join ncfList.processoEpaList procList ";
	
}