package br.com.infox.epp.processo.sigilo.filter;

public interface SigiloProcessoFilter {
	String TYPE_INT = "int";

	String PARAM_ID_USUARIO = "idUsuario";
	
	String FILTER_SIGILO_PROCESSO = "sigiloProcesso";
	String CONDITION_FILTER_SIGILO_PROCESSO = "(not exists (select 1 from tb_sigilo_processo sp where "
			+ "sp.id_processo_epa = id_processo and sp.in_ativo = true) "
			+ "or exists (select 1 from tb_sigilo_processo_permissao spp where "
			+ "spp.in_ativo = true and spp.id_usuario_login = :" + PARAM_ID_USUARIO + " and "
			+ "spp.id_sigilo_processo = (select sp.id_sigilo_processo from tb_sigilo_processo sp where "
			+ "sp.id_processo_epa = id_processo and sp.in_ativo = true))"
			+ ")";
}
