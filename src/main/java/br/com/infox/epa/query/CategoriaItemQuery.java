package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade de 
 * CategoriaAssunto
 * @author Daniel
 *
 */
public interface CategoriaItemQuery {

	String QUERY_PARAM_CATEGORIA = "categoria";
	
	String LIST_BY_CATEGORIA = "listCategoriaItemByCategoria";
	String LIST_BY_CATEGORIA_QUERY = "select o from CategoriaItem o " +
									"where o.categoria = :"+QUERY_PARAM_CATEGORIA;
	
}