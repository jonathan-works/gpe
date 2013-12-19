package br.com.infox.epp.access.query;

import java.io.Serializable;

public interface BloqueioUsuarioQuery extends Serializable {
    
    String TABLE_BLOQUEIO_USUARIO = "tb_bloqueio_usuario";
    String SEQUENCE_BLOQUEIO_USUARIO = "public.sq_tb_bloqueio_usuario";
    String ID_BLOQUEIO_USUARIO = "id_bloqueio_usuario";
    String ID_USUARIO = "id_usuario";
    String DATA_BLOQUEIO = "dt_bloqueio";
    String DATA_PREVISAO_DESBLOQUEIO = "dt_previsao_desbloqueio";
    String MOTIVO_BLOQUEIO = "ds_motivo_bloqueio";
    String DATA_DESBLOQUEIO = "dt_desbloqueio";
    
    String PARAM_USUARIO = "usuario";
    String BLOQUEIO_MAIS_RECENTE = "bloqueioMaisRecente";
    String BLOQUEIO_MAIS_RECENTE_QUERY = "select o from BloqueioUsuario o where o.idBloqueioUsuario = " +
            "(select max(b.idBloqueioUsuario) from BloqueioUsuario b where b.usuario = :" + PARAM_USUARIO + ")";
    
    String PARAM_ID_USUARIO = "id_usuario";
    String UNDO_BLOQUEIO = "undoBloqueio";
    String UNDO_BLOQUEIO_NATIVE_QUERY = "update public.tb_usuario set in_bloqueio=false where id_usuario = :" 
                                            + PARAM_ID_USUARIO;
    
    String PARAM_BLOQUEIO = "bloqueio";
    String PARAM_DATA_DESBLOQUEIO = "dataDesbloqueio";
    String SAVE_DATA_DESBLOQUEIO = "saveDataDesbloqueio";
    String SAVE_DATA_DESBLOQUEIO_QUERY = "UPDATE BloqueioUsuario b SET b.dataDesbloqueio = :" + PARAM_DATA_DESBLOQUEIO 
                                            + " WHERE b.idBloqueioUsuario = :" + PARAM_BLOQUEIO;
    
    

}
