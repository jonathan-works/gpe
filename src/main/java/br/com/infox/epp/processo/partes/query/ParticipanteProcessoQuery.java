package br.com.infox.epp.processo.partes.query;

public interface ParticipanteProcessoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_PROCESSO = "processo";
	String PARAM_TIPO_PARTE = "tipoParte";
	String PARAM_PARTICIPANTE_PAI = "participantePai";
	
	String PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO = "Participante.pessoa.processo";
	String PARTICIPANTE_PROCESSO_BY_PESSOA_PROCESSO_QUERY = "select o from ParticipanteProcesso o " +
			"where o.pessoa = :" + PARAM_PESSOA + " and o.processo = :" + PARAM_PROCESSO;
	
	String EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_PAI_TIPO = "Participante.processo.pessoa.pai.tipo";
	String EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_PAI_TIPO_QUERY = "select count(o) from ParticipanteProcesso o " +
			"where o.pessoa = :" + PARAM_PESSOA + " and o.processo = :" + PARAM_PROCESSO +
			" and o.tipoParte = :" + PARAM_TIPO_PARTE + " and o.participantePai = :" + PARAM_PARTICIPANTE_PAI;
	
	String EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_TIPO = "Participante.processo.pessoa.tipo";
	String EXISTE_PARTICIPANTE_BY_PESSOA_PROCESSO_TIPO_QUERY = "select count(o) from ParticipanteProcesso o " +
			"where o.pessoa = :" + PARAM_PESSOA + " and o.processo = :" + PARAM_PROCESSO +
			" and o.tipoParte = :" + PARAM_TIPO_PARTE + " and o.participantePai is null";
			
	String PARTICIPANTES_PROCESSO = "ParticipanteProcesso.participantesProcesso";
	String PARTICIPANTES_PROCESSO_QUERY = "select o from ParticipanteProcesso o "
			+ "where o.processo = :" + PARAM_PROCESSO + " and o.ativo = true";
	
}
