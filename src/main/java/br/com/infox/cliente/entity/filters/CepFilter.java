package br.com.infox.cliente.entity.filters;

public interface CepFilter {

	String CONDITION_CEP_ESTADO = "nr_cep like :numeroCep || '%'";
	String FILTER_PARAM_NUMERO_CEP = "numeroCep";
	String FILTER_CEP_ESTADO = "cepEstado";

}