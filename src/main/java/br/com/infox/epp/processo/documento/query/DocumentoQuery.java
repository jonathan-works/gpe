package br.com.infox.epp.processo.documento.query;

public interface DocumentoQuery {

    String PARAM_PROCESSO = "processo";
    String PARAM_TIPO_NUMERACAO = "tipoNumeracao";

    String NEXT_SEQUENCIAL = "getNextSequencial";
    String NEXT_SEQUENCIAL_QUERY = "select max(pd.numeroDocumento) from Documento pd "
            + "inner join pd.classificacaoDocumento tpd where pd.processo = :"
            + PARAM_PROCESSO
            + " and tpd.tipoNumeracao=:"
            + PARAM_TIPO_NUMERACAO + " group by pd.processo";

    String ID_JBPM_TASK_PARAM = "idJbpmTask";
    String USUARIO_PARAM = "usuario";
    String LIST_ANEXOS_PUBLICOS = "listAnexosPublicos";
    String LIST_ANEXOS_PUBLICOS_QUERY = "select o from Documento o inner join o.classificacaoDocumento tpd "
            + "where o.idJbpmTask = :"
            + ID_JBPM_TASK_PARAM
            + " and (tpd.visibilidade='A' or tpd.visibilidade='E') and o.excluido = false and "
            + "not exists(select 1 from SigiloDocumento s where s.ativo = true and s.documento = o)";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO = "listAnexosPublicosUsuarioLogado";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY = "select o from Documento o inner join o.classificacaoDocumento tpd "
            + "where o.idJbpmTask = :"
            + ID_JBPM_TASK_PARAM
            + " and (tpd.visibilidade='A' or tpd.visibilidade='E') and o.excluido = false and "
            + "(not exists(select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
            + "exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = :"
            + USUARIO_PARAM
            + " and sp.ativo = true and "
            + "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o)"
            + ")" + ")";
    String DATA_INCLUSAO = "dataInclusao";
    
    String LIST_DOCUMENTO_BY_PROCESSO = "listProcessoDocumentoByProcesso";
    String LIST_DOCUMENTO_BY_PROCESSO_QUERY = "select o from Documento o " +
    		"where o.processo = :" + PARAM_PROCESSO;
    
    String LIST_DOCUMENTO_BY_TASKINSTANCE = "listDocumentoByTaskInstance";
    String lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY = "select o from Documento o where idJbpmTask = :" + ID_JBPM_TASK_PARAM;
    
    String TOTAL_DOCUMENTOS_PROCESSO = "Documento.totalDocumentosProcesso";
    String TOTAL_DOCUMENTOS_PROCESSO_QUERY = "select count(o) from Documento o where o.processo = :" + PARAM_PROCESSO;
    
}
