package br.com.infox.epp.processo.partes.query;

public interface ParteProcessoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_PROCESSO = "processo";
	
	String PARTE_PROCESSO_BY_PESSOA_PROCESSO = "ParteProcesso.pessoa.processo";
	String PARTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY = "select o from ParteProcesso o " +
			"where o.pessoa = :" + PARAM_PESSOA + " and o.processo = :" + PARAM_PROCESSO;
}
