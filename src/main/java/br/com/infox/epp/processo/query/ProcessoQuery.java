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
    
    String PARAM_ID_PROCESSO = "idProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO = "apagaActorIdDeProcesso";
    String APAGA_ACTOR_ID_DO_PROCESSO_QUERY = "update public.tb_processo set nm_actor_id = null where id_processo = :" 
            + PARAM_ID_PROCESSO;
    

}
