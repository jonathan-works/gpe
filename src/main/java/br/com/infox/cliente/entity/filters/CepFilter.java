package br.com.infox.cliente.entity.filters;

public interface CepFilter {

	public static final String CONDITION_CEP_ESTADO = "nr_cep like :numeroCep || '%'";
	public static final String FILTER_PARAM_NUMERO_CEP = "numeroCep";
	public static final String FILTER_CEP_ESTADO = "cepEstado";

}