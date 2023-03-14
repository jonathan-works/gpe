package br.com.infox.epp.documento.query;

public interface TarefasMovimentarLoteQuery {

    String PARAM_TASK_INSTANCE = "taskInstance";

    StringBuilder PODE_MOVIMENTAR_EM_LOTE_QUERY = new StringBuilder("select taskins.ID_ taskins from JBPM_TASKINSTANCE taskins")
            .append("            INNER JOIN JBPM_VARIABLEINSTANCE vi on vi.TASKINSTANCE_ = taskins.ID_")
            .append("            WHERE taskins.ID_ in :")
            .append(PARAM_TASK_INSTANCE)
            .append(" AND vi.NAME_ = 'tarefaMovimentarLote' AND vi.STRINGVALUE_ = 'true' ");

}
