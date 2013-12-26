package br.com.infox.epp.processo.query;

public interface ProcessoQuery {
    
    String TABLE_PROCESSO = "tb_processo";
    String TABLE_PROCESSO_CONEXAO = "tb_processo_conexao";
    String SEQUENCE_PROCESSO = "public.sq_tb_processo";
    String ID_PROCESSO = "id_processo";
    String ID_PROCESSO_CONEXO = "id_processo_conexo";
    String ID_JBPM = "id_jbpm";
    String ID_USUARIO_CADASTRO_PROCESSO = "id_usuario_cadastro_processo";
    String NUMERO_PROCESSO = "nr_processo";
    String NUMERO_PROCESSO_ORIGEM = "nr_processo_origem";
    String COMPLEMENTO = "ds_complemento";
    String DATA_INICIO = "dt_inicio";
    String DATA_FIM = "dt_fim";
    String DURACAO = "nr_duracao";
    String NOME_ACTOR_ID = "nm_actor_id";
    String ID_CAIXA = "id_caixa";
    String PROCESSO_ATTRIBUTE = "processo";
    
    String PARAM_ACTOR_ID = "actorId";
    String ANULA_ACTOR_ID = "anulaActorId";
    String ANULA_ACTOR_ID_QUERY = "update public.tb_processo set nm_actor_id = null where nm_actor_id = :" 
            + PARAM_ACTOR_ID;
    
    String ANULA_TODOS_OS_ACTOR_IDS = "anularTodosOsActorIds";
    String ANULA_TODOS_OS_ACTOR_IDS_QUERY = "update public.tb_processo set nm_actor_id = null ";
    
    String PARAM_ID_PROCESSO = "idProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO = "apagaActorIdDeProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO_QUERY = "update public.tb_processo set nm_actor_id = null where id_processo = :" 
            + PARAM_ID_PROCESSO;
    
    String REMOVE_PROCESSO_DA_CAIXA_ATUAL = "removerProcessoDaCaixaAtual";
    String REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY = "updte public.tb_processo set id_caixa = null where id_processo = :" 
            + PARAM_ID_PROCESSO;
    
    String LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID = "listProcessosByIdProcessoAndActorId";
    String LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY = "select o from Processo o where o.idProcesso = :" 
            + PARAM_ID_PROCESSO + " and o.actorId like :" + PARAM_ACTOR_ID;
    
    String ATUALIZAR_PROCESSOS = "atualizarProcessos";
    String ATUALIZAR_PROCESSOS_QUERY = "update jbpm_processinstance pi set processdefinition_ = " +
            "(select max(id_) from jbpm_processdefinition pd " +
            "where name_ = (select name_ from jbpm_processdefinition " +
            "where id_ = pi.processdefinition_));\n" +

            "update jbpm_token t set node_ = "+
            "(select max(n.id_) from jbpm_node n "+
            "inner join jbpm_processdefinition pd on pd.id_ = n.processdefinition_ "+
            "where n.name_ = (select name_ from jbpm_node where id_ = t.node_) "+
            "and pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
            "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
            "where procinst.id_ = t.processinstance_) "+
            "and n.class_ = (select class_ from jbpm_node where id_ = t.node_));\n" +
            
            "update jbpm_taskinstance ti set task_ = "+ 
            "(select max(t.id_) from jbpm_task t "+
            "inner join jbpm_processdefinition pd on pd.id_ = t.processdefinition_ "+
            "where t.name_ = (select name_ from jbpm_task where id_ = ti.task_) and "+
            "pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
            "inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
            "where procinst.id_ = ti.procinst_)) "+
            "where end_ is null;\n" +
            
            "update public.tb_processo_localizacao_ibpm pl set id_task_jbpm =" +
            "(select max(id_) from jbpm_task t where exists" +
            "(select * from jbpm_task where name_ = t.name_ " +
            "and id_ = pl.id_task_jbpm))";

}
