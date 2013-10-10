package br.com.infox.epp.query;

/**
 * Interface com as queries da entidade de 
 * NaturezaLocalizacao
 * @author Daniel
 *
 */
public interface NaturezaLocalizacaoQuery {

	String QUERY_PARAM_NATUREZA = "natureza";
	
	String LIST_BY_NATUREZA = "listNaturezaLocalizacaoByNatureza";
	String LIST_BY_NATUREZA_QUERY = "select o from NaturezaLocalizacao o " +
									"where o.natureza = :"+QUERY_PARAM_NATUREZA;
	
}