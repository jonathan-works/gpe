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
    
    String PARAM_ACTOR_ID = "actorId";
    String ANULA_ACTOR_ID = "anulaActorId";
    String ANULA_ACTOR_ID_QUERY = "update tb_processo set nm_actor_id = null where nm_actor_id = :"
            + PARAM_ACTOR_ID;

    String ANULA_TODOS_OS_ACTOR_IDS = "anularTodosOsActorIds";
    String ANULA_TODOS_OS_ACTOR_IDS_QUERY = "update tb_processo set nm_actor_id = null ";

    String PARAM_ID_PROCESSO = "idProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO = "apagaActorIdDeProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO_QUERY = "update tb_processo set nm_actor_id = null where id_processo = :"
            + PARAM_ID_PROCESSO;

    String REMOVE_PROCESSO_DA_CAIXA_ATUAL = "removerProcessoDaCaixaAtual";
    String REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY = "update tb_processo set id_caixa = null where id_processo = :"
            + PARAM_ID_PROCESSO;

    
    @Deprecated 
    String LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID = "listProcessosByIdProcessoAndActorId";
    /**
     * Retirado actorId do processo
     */
    @Deprecated
    String LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY = "select o from Processo o where o.idProcesso = :"
            + PARAM_ID_PROCESSO ;

    String ID_LIST_PROCESSO_PARAM = "idList";
    String CAIXA_PARAM = "caixa";
    String MOVER_PROCESSOS_PARA_CAIXA = "moverProcessosParaCaixa";
    String MOVER_PROCESSOS_PARA_CAIXA_QUERY = "update Processo set caixa = :"
            + CAIXA_PARAM + " where idProcesso in (:" + ID_LIST_PROCESSO_PARAM
            + ")";

    String MOVER_PROCESSO_PARA_CAIXA = "moverProcessoParaCaixa";
    String MOVER_PROCESSO_PARA_CAIXA_QUERY = "update tb_processo set id_caixa = :"
            + CAIXA_PARAM + " where id_processo = :" + PARAM_ID_PROCESSO;

    String ATUALIZAR_PROCESSOS = "atualizarProcessos";
    String ATUALIZAR_PROCESSOS_QUERY = "update jbpm_processinstance set processdefinition_ = "
            + "(select max(id_) from jbpm_processdefinition pd "
            + "where name_ = (select name_ from jbpm_processdefinition "
            + "where id_ = jbpm_processinstance.processdefinition_));\n"
            +

            "update jbpm_token set node_ = "
            + "(select max(n.id_) from jbpm_node n "
            + "inner join jbpm_processdefinition pd on pd.id_ = n.processdefinition_ "
            + "where n.name_ = (select name_ from jbpm_node node where node.id_ = jbpm_token.node_) "
            + "and pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "
            + "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "
            + "where procinst.id_ = jbpm_token.processinstance_) "
            + "and n.class_ = (select class_ from jbpm_node node where node.id_ = jbpm_token.node_));\n"
            +

            "update jbpm_taskinstance set task_ = "
            + "(select max(t.id_) from jbpm_task t "
            + "inner join jbpm_processdefinition pd on pd.id_ = t.processdefinition_ "
            + "where t.name_ = (select name_ from jbpm_task ta where ta.id_ = jbpm_taskinstance.task_) and "
            + "pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "
            + "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "
            + "where procinst.id_ = jbpm_taskinstance.procinst_)) "
            + "where end_ is null;\n"
            +

            "update tb_processo_localizacao_ibpm set id_task_jbpm ="
            + "(select max(id_) from jbpm_task t where exists"
            + "(select * from jbpm_task ta where name_ = t.name_ "
            + "and ta.id_ = tb_processo_localizacao_ibpm.id_task_jbpm))";

    String GET_PROCESSO_BY_NUMERO_PROCESSO = "getProcessoByNumeroProcesso";
    String GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY = "select o from Processo o where o.numeroProcesso=:" + NUMERO_PROCESSO;
    
    String REMOVER_PROCESSO_JBMP = "removerProcessoJbpm";
	String REMOVER_PROCESSO_JBMP_QUERY = 
			"DELETE FROM tb_task_conteudo_index WHERE id_taskinstance in (select id_ from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM + " );\n" +
			"UPDATE jbpm_processinstance SET roottoken_ = null, superprocesstoken_ = null where id_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM jbpm_variableinstance WHERE processinstance_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM tb_usuario_taskinstance WHERE id_taskinstance in (select id_ from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM + " );\n" +
			"DELETE FROM tb_processo_localizacao_ibpm WHERE id_processo = :" + PARAM_ID_PROCESSO + " ;\n" +
			"DELETE FROM tb_processo_tarefa WHERE id_processo = :" + PARAM_ID_PROCESSO + " ;\n" +
			"DELETE FROM jbpm_taskactorpool WHERE taskinstance_ in (select id_ from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM + " );\n" + 
			"DELETE FROM jbpm_pooledactor WHERE swimlaneinstance_ in (select swimlaninstance_ from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM + " );\n" +
			"DELETE FROM jbpm_taskinstance WHERE procinst_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM jbpm_swimlaneinstance WHERE taskmgmtinstance_ = :" + PARAM_ID_TASKMGMINSTANCE + " ;\n" +
			"DELETE FROM jbpm_tokenvariablemap WHERE token_ = :" + PARAM_ID_TOKEN + " ;\n" +
			"DELETE FROM jbpm_moduleinstance WHERE processinstance_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM jbpm_job WHERE processinstance_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM jbpm_token WHERE processinstance_ = :" + PARAM_ID_JBPM + " ;\n" +
			"DELETE FROM jbpm_processinstance WHERE id_ = :" + PARAM_ID_JBPM + " ;";
	
	String GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST = "getIdTaskMgmInstanceAndIdTokenByProcInst";
	String GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY = "select taskmgmtinstance_ , token_ " +
			"from jbpm_taskinstance where procinst_ = :" + PARAM_ID_JBPM;
	
	String NUMERO_PROCESSO_BY_ID_JBPM = "Processo.numeroProcessoByIdJbpm";
	String NUMERO_PROCESSO_BY_ID_JBPM_QUERY = "select o.numeroProcesso from Processo o where o.idJbpm = :" + PARAM_ID_JBPM;
	
	String NUMERO_PROCESSO_PARAM = "numeroProcesso";
	String PROCESSO_BY_NUMERO = "getProcessoByNumero";
	String PROCESSO_BY_NUMERO_QUERY = "select o from Processo o where o.numeroProcesso = :" + NUMERO_PROCESSO_PARAM;

}
