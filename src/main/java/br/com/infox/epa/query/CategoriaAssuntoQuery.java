package br.com.infox.epa.query;

/**
 * Interface com as queries da entidade de 
 * CategoriaAssunto
 * @author Daniel
 *
 */
public interface CategoriaAssuntoQuery {

	String QUERY_PARAM_CATEGORIA = "categoria";
	
	String LIST_BY_CATEGORIA = "listCategoriaAssuntoByCategoria";
	String LIST_BY_CATEGORIA_QUERY = "select o from CategoriaAssunto o " +
									"where o.categoria = :"+QUERY_PARAM_CATEGORIA;
	
}