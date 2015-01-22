package br.com.infox.epp.processo.situacao.query;

public interface SituacaoProcessoQuery {
	
	String PARAM_ID_PROCESSO = "idProcesso";
	
	String GET_ID_TASK_INSTANCE_BY_ID_PROCESSO = "getIdTaskInstanceByIdProcesso";
	String GET_ID_TASK_INSTANCE_BY_ID_PROCESSO_QUERY = "select sp.idTaskInstance from SituacaoProcesso sp "
			+ " where sp.idProcesso = :" + PARAM_ID_PROCESSO;

}
