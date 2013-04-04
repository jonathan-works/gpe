package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade de 
 * CategoriaAssunto
 * @author Daniel
 *
 */
public interface CategoriaItemQuery {

    String QUERY_PARAM_CATEGORIA = "categoria";
    String QUERY_PARAM_ITEM = "item";
	
	String LIST_BY_CATEGORIA = "listCategoriaItemByCategoria";
	String LIST_BY_CATEGORIA_QUERY = "select o from CategoriaItem o " +
									"where o.categoria = :"+QUERY_PARAM_CATEGORIA;

	String COUNT_BY_CATEGORIA_ITEM = "countCategoriaItemByCategoriaAndItem";
	String COUNT_BY_CATEGORIA_ITEM_QUERY = "select count(o) from CategoriaItem o where o.categoria=:" +
			QUERY_PARAM_CATEGORIA+" and o.item=:"+QUERY_PARAM_ITEM;
	
}