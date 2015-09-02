package br.com.infox.epp.processo.documento.query;

public interface DocumentoQuery {
    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_PROCESSO = "processo";
    String PARAM_TIPO_NUMERACAO = "tipoNumeracao";
    String PARAM_IDS_DOCUMENTO = "idsDocumento";

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
    		+ "inner join o.documentoBin bin "
            + "where bin.minuta = false and o.idJbpmTask = :"
            + ID_JBPM_TASK_PARAM
            + " and (tpd.visibilidade='A' or tpd.visibilidade='E') and o.excluido = false and "
            + "not exists(select 1 from SigiloDocumento s where s.ativo = true and s.documento = o)";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO = "listAnexosPublicosUsuarioLogado";
    String LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO_QUERY = "select o from Documento o inner join o.classificacaoDocumento tpd "
    		+ "inner join o.documentoBin bin "
            + "where bin.minuta = false and o.idJbpmTask = :"
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
    		"where o.processo = :" + PARAM_PROCESSO + " and o.documentoBin.minuta = false";
    
    String LIST_DOCUMENTO_MINUTA_BY_PROCESSO = "listProcessoDocumentoMinutaByProcesso";
    String LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY = "select o from Documento o " +
    		"where o.processo = :" + PARAM_PROCESSO + " and o.documentoBin.minuta = true";
    
    String LIST_DOCUMENTO_BY_TASKINSTANCE = "listDocumentoByTaskInstance";
    String lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY = "select o from Documento o where idJbpmTask = :" + ID_JBPM_TASK_PARAM;
    
    String TOTAL_DOCUMENTOS_PROCESSO = "Documento.totalDocumentosProcesso";
    String TOTAL_DOCUMENTOS_PROCESSO_QUERY = "select count(o) from Documento o where o.processo = :" + PARAM_PROCESSO 
    		+ " and o.documentoBin.minuta = false";
    
    String DOCUMENTOS_SESSAO_ANEXAR = "Documento.documentosSessaoAnexar";
    String DOCUMENTOS_SESSAO_ANEXAR_QUERY = "select o from Documento o where o.processo = :" + PARAM_PROCESSO
    		+ " and (o.documentoBin.minuta = true or o.id in (:" + PARAM_IDS_DOCUMENTO + "))";

    String DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO = "Documento.porClassificacaoDocumentoOrdenadosPorDataInclusao";
    String DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY = "select d from ClassificacaoDocumento cd inner join cd.documentoList d"
            + " where cd = :" + PARAM_CLASSIFICACAO_DOCUMENTO + " order by d.dataInclusao desc";
}
