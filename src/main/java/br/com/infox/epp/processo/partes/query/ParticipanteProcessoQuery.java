package br.com.infox.epp.processo.partes.query;

public interface ParticipanteProcessoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_PROCESSO = "processo";
	String PARAM_TIPO_PARTE = "tipoParte";
	String PARAM_PARTICIPANTE_PAI = "participantePai";
	String PARAM_PESSOA_PARTICIPANTE_FILHO = "pessoaParticipanteFilho";
	String PARAM_TYPED_NAME = "input";
	String PARAM_ID_PARTICIPANTE = "idParticipante";
	
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
			+ "where o.processo = :" + PARAM_PROCESSO + " and o.ativo = true order by o.caminhoAbsoluto";
	
	String PARTICIPANTES_PROCESSO_RAIZ = "ParticipanteProcesso.participantesProcessoRaiz";
	String PARTICIPANTES_PROCESSO_RAIZ_QUERY = "select o from ParticipanteProcesso o "
			+ "where o.processo = :" + PARAM_PROCESSO + " and o.participantePai is null and o.ativo = true";
	
	String PARTICIPANTES_BY_PROCESSO_PARTICIPANTE_FILHO = "getParticipantesByProcessoParticipanteFilho";
	String PARTICIPANTES_BY_PROCESSO_PARTICIPANTE_FILHO_QUERY = "select o from ParticipanteProcesso o "
	        + "where o.processo = :" + PARAM_PROCESSO + " and exists ("
	                    + "select 1 from ParticipanteProcesso pp where pp.participantePai = o and pp.pessoa = :" + PARAM_PESSOA_PARTICIPANTE_FILHO
	                + ")";
	
	String EXISTE_PARTICIPANTE_FILHO_BY_PROCESSO = "existeParticipanteFilhoByProcesso";
    String EXISTE_PARTICIPANTE_FILHO_BY_PROCESSO_QUERY = "select count(o) from ParticipanteProcesso o "
            + "where o.processo = :" + PARAM_PROCESSO + " "
                + "and o.participantePai = :" + PARAM_PARTICIPANTE_PAI + " "
                + "and o.pessoa = :" + PARAM_PESSOA_PARTICIPANTE_FILHO;
    
    String PARTICIPANTES_PROCESSOS_BY_PARTIAL_NAME = "select pp from ParticipanteProcesso pp "
    +"inner join pp.tipoParte tp where tp.identificador = 'adm_publica' and lower(pp.nome) like lower(concat (:" + PARAM_TYPED_NAME +", '%')) order by pp.nome";

    String PESSOA_BY_PARTICIPANTE_PROCESSO = "Participante.participanteProcesso";
    String PESSOA_BY_PARTICIPANTE_PROCESSO_QUERY = "select p from Pessoa p "
            + "where exists ("
                + "select 1 from ParticipanteProcesso pp "
                + "where pp.pessoa = p "
                    + "and pp.id = :" + PARAM_ID_PARTICIPANTE + " "
            + ")";
}
