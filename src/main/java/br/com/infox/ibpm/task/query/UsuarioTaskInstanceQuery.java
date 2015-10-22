package br.com.infox.ibpm.task.query;

public interface UsuarioTaskInstanceQuery {

    String ID_TASKINSTANCE_PARAM = "idTaskInstance";
    String PARAM_PROCESSO = "processo";
    String PARAM_LOCALIZACAO = "localizacao";
    
    String USUARIO_DA_TAREFA = "getUsuarioByTarefa";
    String USUARIO_DA_TAREFA_QUERY = "SELECT DISTINCT ul.ds_login FROM tb_usuario_login ul "
            + "JOIN tb_usuario_taskinstance uti ON (uti.id_usuario_login = ul.id_usuario_login) "
            + "WHERE id_taskinstance = :" + ID_TASKINSTANCE_PARAM;

    String LOCALIZACAO_DA_TAREFA = "UsuarioTaskInstance.localizacaoTarefa";
    String LOCALIZACAO_DA_TAREFA_QUERY = "select o.localizacao from UsuarioTaskInstance o where o.idTaskInstance = :" + ID_TASKINSTANCE_PARAM;
    
    String LOCALIZACOES_DO_PROCESSO= "UsuarioTaskInstance.localizacoesProcesso";
    String LOCALIZACOES_DO_PROCESSO_QUERY = "select ut.localizacaoExterna from UsuarioTaskInstance ut, ProcessoTarefa pt "
            + "where pt.taskInstance = ut.idTaskInstance and pt.dataInicio is not null and pt.dataFim is null "
            + "and pt.processo.idProcesso = :" + PARAM_PROCESSO ;
    
    String USUARIO_DA_LOCALIZACAO_DO_PROCESSO = "UsuarioTaskInstance.processosLocalizacao";
    String USUARIO_DA_LOCALIZACAO_DO_PROCESSO_QUERY = "select ut from UsuarioTaskInstance ut, ProcessoTarefa pt "
            + "where pt.taskInstance = ut.idTaskInstance and pt.dataInicio is not null and pt.dataFim is null "
            + "and ut.localizacaoExterna.idLocalizacao = :" + PARAM_LOCALIZACAO + " and pt.processo.idProcesso = :" + PARAM_PROCESSO;
}
