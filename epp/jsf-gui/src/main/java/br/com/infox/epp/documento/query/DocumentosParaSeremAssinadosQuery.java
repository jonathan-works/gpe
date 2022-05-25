package br.com.infox.epp.documento.query;

public interface DocumentosParaSeremAssinadosQuery {

    String PARAM_TASK_INSTANCE = "taskInstance";

    StringBuilder DOCUMENTOS_PARA_SEREM_ASSINADOS_QUERY = new StringBuilder("SELECT DISTINCT(REPLACE(doc.id_documento,'.', '')) as idDoc, REPLACE(doc.id_classificacao_documento,'.', '') as idCla, REPLACE(doc.id_documento_bin,'.', '') as idbin, bin.in_minuta, REPLACE(task.id_taskinstance,'.', '') as idti  from tb_documento doc")
            .append("            INNER JOIN tb_documento_bin bin on doc.id_documento_bin = bin.id_documento_bin")
            .append("            INNER JOIN tb_taski_permitida_assinar_doc task on doc.id_documento = task.id_documento")
            .append("            INNER JOIN tb_assinatura_documento assdoc on assdoc.id_documento_bin = doc.id_documento_bin ")
            .append("            WHERE task.id_taskinstance in :")
            .append(PARAM_TASK_INSTANCE);

}
