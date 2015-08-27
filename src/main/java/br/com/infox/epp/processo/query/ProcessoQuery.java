package br.com.infox.epp.processo.query;

public interface ProcessoQuery {

	String TABLE_PROCESSO = "tb_processo";
	String SEQUENCE_PROCESSO = "sq_tb_processo";
	String ID_PROCESSO = "id_processo";
	String ID_JBPM = "id_jbpm";
	String ID_USUARIO_CADASTRO_PROCESSO = "id_usuario_cadastro_processo";
	String NUMERO_PROCESSO = "nr_processo";
	String DATA_INICIO = "dt_inicio";
	String DATA_FIM = "dt_fim";
	String DURACAO = "nr_duracao";
	String NOME_ACTOR_ID = "nm_actor_id";
	String ID_CAIXA = "id_caixa";
	String PROCESSO_ATTRIBUTE = "processo";
	String PARAM_ID_JBPM = "idJbpm";
	String PARAM_ID_TASKMGMINSTANCE = "idTaskMgmInstance";
	String PARAM_ID_TOKEN = "idToken";
	String PARAM_ID_PROCESSO = "idProcesso";
	String PARAM_ID_USUARIO = "id_usuario";
	String PARAM_ID_TASK = "idTask";
	String PARAM_FLUXO = "fluxo";
	String PARAM_ID_CAIXA = "idCaixa";
	String QUERY_PARAM_PROCESSO = "processo";
	String QUERY_PARAM_FLUXO_COMUNICACAO = "comunicar";

	String LIST_ALL_NOT_ENDED = "listAllProcessoEpaNotEnded";

	String LIST_ALL_NOT_ENDED_QUERY = "select o from Processo o where " + "o.dataFim is null";

	String LIST_NOT_ENDED_BY_FLUXO = "listNotEndedByFluxo";
	String LIST_NOT_ENDED_BY_FLUXO_QUERY = "select o from Processo o where " + " o.naturezaCategoriaFluxo.fluxo = :" + PARAM_FLUXO
			+ " and o.dataFim is null";

	String TEMPO_GASTO_PROCESSO_EPP = "tempoGastoPeloProcesso";
	String TEMPO_GASTO_PROCESSO_EPP_QUERY = "select new map( sum(pet.tempoGasto) / 60 as horas, ( select sum(pet2.tempoGasto) "
			+ "from ProcessoTarefa pet2 inner join pet2.tarefa t2 " + "where t2.tipoPrazo != 'H' and "
			+ "pet2.processo.idProcesso = pet.processo.idProcesso " + "group by pet2.processo.idProcesso ) as dias ) "
			+ "from ProcessoTarefa pet inner join pet.tarefa t "
			+ "where t.tipoPrazo = 'H' and pet.processo.idProcesso = :idProcesso " + "group by pet.processo.idProcesso";

	String PROCESSO_EPA_BY_ID_JBPM = "getProcessoEpaByIdJbpm";
	String PROCESSO_EPA_BY_ID_JBPM_QUERY = "select pe from Processo pe where pe.idJbpm = :" + PARAM_ID_JBPM;

	String COUNT_PARTES_ATIVAS_DO_PROCESSO = "countPartesAtivasDoProcesso";
	String COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY = "select count(*) from ParticipanteProcesso partes where partes.processo = :"
			+ QUERY_PARAM_PROCESSO + " and partes.ativo = true";

	String ITEM_DO_PROCESSO = "getItemDoProcessoByIdProcesso";
	String ITEM_DO_PROCESSO_QUERY = "select o.itemDoProcesso from Processo o where o.idProcesso =:" + PARAM_ID_PROCESSO;

	String PARAM_SITUACAO = "situacao";
	String TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO = "mediaTempoGasto";
	String TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY = "select avg(pEpa.tempoGasto) from Processo pEpa "
			+ "inner join pEpa.naturezaCategoriaFluxo ncf where ncf.fluxo=:" + PARAM_FLUXO
			+ " and pEpa.dataFim is null and pEpa.situacaoPrazo=:" + PARAM_SITUACAO + " group by ncf.fluxo";

	String PARAM_ACTOR_ID = "actorId";

	String GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO = "getProcessoByIdProcessoAndIdUsuario";
	String GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO_QUERY = "SELECT p.* " + "FROM tb_processo p "
			+ "INNER JOIN tb_processo_tarefa pt on (p.id_processo = pt.id_processo) "
			+ "INNER JOIN tb_usuario_taskinstance uti ON (pt.id_task_instance = uti.id_taskinstance) " + "WHERE p.id_processo = :"
			+ PARAM_ID_PROCESSO + " AND uti.id_usuario_login = :" + PARAM_ID_USUARIO + " AND pt.id_task_instance = :" + PARAM_ID_TASK;

	String ID_LIST_PROCESSO_PARAM = "idList";
	String CAIXA_PARAM = "caixa";

	String ATUALIZAR_PROCESSOS = "atualizarProcessos";
	String ATUALIZAR_PROCESSOS_QUERY = "update jbpm_processinstance set processdefinition_ = "
			+ "(select max(id_) from jbpm_processdefinition pd " + "where name_ = (select name_ from jbpm_processdefinition "
			+ "where id_ = jbpm_processinstance.processdefinition_));\n" +

			"update jbpm_token set node_ = " + "(select max(n.id_) from jbpm_node n "
			+ "inner join jbpm_processdefinition pd on pd.id_ = n.processdefinition_ "
			+ "where n.name_ = (select name_ from jbpm_node node where node.id_ = jbpm_token.node_) "
			+ "and pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "
			+ "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "
			+ "where procinst.id_ = jbpm_token.processinstance_) "
			+ "and n.class_ = (select class_ from jbpm_node node where node.id_ = jbpm_token.node_));\n" +

			"update jbpm_taskinstance set task_ = " + "(select max(t.id_) from jbpm_task t "
			+ "inner join jbpm_processdefinition pd on pd.id_ = t.processdefinition_ "
			+ "where t.name_ = (select name_ from jbpm_task ta where ta.id_ = jbpm_taskinstance.task_) and "
			+ "pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "
			+ "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "
			+ "where procinst.id_ = jbpm_taskinstance.procinst_)) " + "where end_ is null;\n" +

			"update tb_processo_localizacao_ibpm set id_task_jbpm =" + "(select max(id_) from jbpm_task t where exists"
			+ "(select * from jbpm_task ta where name_ = t.name_ " + "and ta.id_ = tb_processo_localizacao_ibpm.id_task_jbpm))";

	String GET_PROCESSO_BY_NUMERO_PROCESSO = "getProcessoByNumeroProcesso";
	String GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY = "select o from Processo o where o.numeroProcesso=:" + NUMERO_PROCESSO;

	String REMOVER_PROCESSO_JBMP = "removerProcessoJbpm";
	String REMOVER_PROCESSO_JBMP_QUERY = "DELETE FROM tb_task_conteudo_index WHERE id_taskinstance in (select id_ from jbpm_taskinstance where procinst_ = :"
			+ PARAM_ID_JBPM
			+ " );\n"
			+ "UPDATE jbpm_processinstance SET roottoken_ = null, superprocesstoken_ = null where id_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM jbpm_variableinstance WHERE processinstance_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM tb_usuario_taskinstance WHERE id_taskinstance in (select id_ from jbpm_taskinstance where procinst_ = :"
			+ PARAM_ID_JBPM
			+ " );\n"
			+ "DELETE FROM tb_processo_localizacao_ibpm WHERE id_processo = :"
			+ PARAM_ID_PROCESSO
			+ " ;\n"
			+ "DELETE FROM tb_processo_tarefa WHERE id_processo = :"
			+ PARAM_ID_PROCESSO
			+ " ;\n"
			+ "DELETE FROM jbpm_taskactorpool WHERE taskinstance_ in (select id_ from jbpm_taskinstance where procinst_ = :"
			+ PARAM_ID_JBPM
			+ " );\n"
			+ "DELETE FROM jbpm_pooledactor WHERE swimlaneinstance_ in (select swimlaninstance_ from jbpm_taskinstance where procinst_ = :"
			+ PARAM_ID_JBPM
			+ " );\n"
			+ "DELETE FROM jbpm_taskinstance WHERE procinst_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM jbpm_swimlaneinstance WHERE taskmgmtinstance_ = :"
			+ PARAM_ID_TASKMGMINSTANCE
			+ " ;\n"
			+ "DELETE FROM jbpm_tokenvariablemap WHERE token_ = :"
			+ PARAM_ID_TOKEN
			+ " ;\n"
			+ "DELETE FROM jbpm_moduleinstance WHERE processinstance_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM jbpm_job WHERE processinstance_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM jbpm_token WHERE processinstance_ = :"
			+ PARAM_ID_JBPM
			+ " ;\n"
			+ "DELETE FROM jbpm_processinstance WHERE id_ = :" + PARAM_ID_JBPM + " ;";

	String GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST = "getIdTaskMgmInstanceAndIdTokenByProcInst";
	String GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY = "select taskmgmtinstance_ , token_ "
			+ "from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM;

	String NUMERO_PROCESSO_BY_ID_JBPM = "Processo.numeroProcessoByIdJbpm";
	String NUMERO_PROCESSO_BY_ID_JBPM_QUERY = "select o.numeroProcesso from Processo o where o.idJbpm = :" + PARAM_ID_JBPM;

	String NUMERO_PROCESSO_PARAM = "numeroProcesso";
	String PROCESSO_BY_NUMERO = "getProcessoByNumero";
	String PROCESSO_BY_NUMERO_QUERY = "select o from Processo o where o.numeroProcesso = :" + NUMERO_PROCESSO_PARAM;

	String PROCESSO_PAI_PARAM = "processoPai";
	String NUMERO_PROCESSO_ROOT_PARAM = "numeroProcessoRoot";
	String TIPO_PROCESSO_PARAM = "tipoProcesso";
		
	String PROCESSOS_FILHO_BY_TIPO = "listProcessosFilhoByTipo";
	String PROCESSOS_FILHO_BY_TIPO_QUERY = "select o from Processo o inner join o.metadadoProcessoList m where"
			+ " NumeroProcessoRoot(o.idProcesso) = :" + NUMERO_PROCESSO_ROOT_PARAM + " and m.metadadoType = 'tipoProcesso' and m.valor = :" + TIPO_PROCESSO_PARAM;
	
	String PROCESSOS_FILHO_NOT_ENDED_BY_TIPO = "listProcessosFilhoNotEndedByTipo";
	String PROCESSOS_FILHO_NOT_ENDED_BY_TIPO_QUERY = PROCESSOS_FILHO_BY_TIPO_QUERY + " and o.dataFim is null ";
	
	String PROCESSOS_BY_ID_CAIXA = "processosByIdCaixa";
	String PROCESSOS_BY_ID_CAIXA_QUERY = "select o from Processo o where o.caixa.idCaixa = :" + PARAM_ID_CAIXA;
	
	String MEIO_EXPEDICAO_PARAM = "tipoExpedicao";
	String LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA = "listProcessosComunicacaoSemCiencia";
	String LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA_QUERY = "select p from Processo p " 
			+ " where p.idJbpm is not null and p.dataFim is null " 
			+ " and exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and mp.metadadoType = 'tipoProcesso' "
					+ " and mp.valor = :" + TIPO_PROCESSO_PARAM + " ) " 
			+ " and not exists (select 1 from MetadadoProcesso mp " 
					+ " where p = mp.processo and mp.metadadoType = 'dataCiencia' ) "
			+ " and exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and mp.metadadoType = 'meioExpedicaoComunicacao' "
					+ " and mp.valor = :" + MEIO_EXPEDICAO_PARAM + " ) "
			+ " and exists (select 1 from org.jbpm.graph.exe.ProcessInstance pi where pi.id = p.idJbpm and pi.processDefinition.name = :comunicar) ";
	
	String LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO = "listProcessosComunicacaoSemCumprimento";
	String LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO_QUERY = "select p from Processo p " 
			+ " where p.idJbpm is not null and p.dataFim is null " 
			+ " and exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and mp.metadadoType = 'tipoProcesso' "
					+ " and mp.valor = :" + TIPO_PROCESSO_PARAM + " ) " 
			+ " and exists (select 1 from MetadadoProcesso mp " 
					+ " where p = mp.processo and mp.metadadoType = 'dataCiencia' ) "
			+ " and exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and mp.metadadoType = 'meioExpedicaoComunicacao' "
					+ " and mp.valor = :" + MEIO_EXPEDICAO_PARAM + " ) "
			+ " and exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and  mp.metadadoType = 'limiteDataCumprimento' ) "
			+ " and not exists (select 1 from MetadadoProcesso mp "
					+ " where p = mp.processo and mp.metadadoType = 'dataCumprimento' ) "
			+ " and exists (select 1 from org.jbpm.graph.exe.ProcessInstance pi where pi.id = p.idJbpm and pi.processDefinition.name = :comunicar) ";
}
