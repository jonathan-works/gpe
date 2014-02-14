package br.com.infox.epp.processo.sigilo.query;

public interface SigiloProcessoQuery {
	String TABLE_NAME = "tb_sigilo_processo";
	String SEQUENCE_NAME = "sq_tb_sigilo_processo";
	String COLUMN_ID = "id_sigilo_processo";
	String COLUMN_ID_PROCESSO_EPA = "id_processo_epa";
	String COLUMN_ID_USUARIO_LOGIN = "id_usuario_login";
	String COLUMN_SIGILOSO = "in_sigiloso";
	String COLUMN_MOTIVO = "ds_motivo";
	String COLUMN_DATA_INCLUSAO = "dt_inclusao";
	String COLUMN_ATIVO = "in_ativo";
	
	String QUERY_PARAM_PROCESSO = "processo";
	
	String NAMED_QUERY_SIGILO_PROCESSO_ATIVO = "SigiloProcesso.sigiloProcessoAtivo";
	String QUERY_SIGILO_PROCESSO_ATIVO = "select o from SigiloProcesso o where o.ativo = true and o.processo = :" + QUERY_PARAM_PROCESSO;
}
