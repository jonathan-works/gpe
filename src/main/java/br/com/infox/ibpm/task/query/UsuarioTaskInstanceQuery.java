package br.com.infox.ibpm.task.query;

public interface UsuarioTaskInstanceQuery {
    
    String ID_TASKINSTANCE_PARAM = "idTaskInstance";
    String USUARIO_DA_TAREFA = "getUsuarioByTarefa";
    String USUARIO_DA_TAREFA_QUERY = "SELECT DISTINCT ul.ds_login FROM tb_usuario_login ul "
            + "JOIN tb_usuario_taskinstance uti ON (uti.id_usuario_login = ul.id_usuario_login) "
            + "WHERE id_taskinstance = :" + ID_TASKINSTANCE_PARAM;

}
