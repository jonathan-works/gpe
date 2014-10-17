package br.com.infox.epp.processo.partes.query;

public interface ParticipanteProcessoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_PROCESSO = "processo";
	
	String PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO = "Participante.pessoa.processo";
	String PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY = "select o from ParticipanteProcesso o " +
			"where o.pessoa = :" + PARAM_PESSOA + " and o.processo = :" + PARAM_PROCESSO;
	
}
