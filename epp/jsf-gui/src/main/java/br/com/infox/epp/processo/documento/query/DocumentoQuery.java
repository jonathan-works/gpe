package br.com.infox.epp.processo.documento.query;

public interface DocumentoQuery {
    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_PROCESSO = "processo";
    String PARAM_TIPO_NUMERACAO = "tipoNumeracao";
    String PARAM_IDS_DOCUMENTO = "idsDocumento";

    String NEXT_SEQUENCIAL = "getNextSequencial";
    String NEXT_SEQUENCIAL_QUERY = "select max(pd.numeroSequencialDocumento) from Documento pd "
            + "inner join pd.classificacaoDocumento tpd inner join pd.pasta p where p.processo = :"
            + PARAM_PROCESSO
            + " and tpd.tipoNumeracao=:"
            + PARAM_TIPO_NUMERACAO + " group by p.processo";

    String ID_JBPM_TASK_PARAM = "idJbpmTask";
    String USUARIO_PARAM = "usuario";
    String DATA_INCLUSAO = "dataInclusao";
    
    String LIST_DOCUMENTO_BY_PROCESSO = "listProcessoDocumentoByProcesso";
    String LIST_DOCUMENTO_BY_PROCESSO_QUERY = "select o from Documento o inner join o.pasta p " +
    		"where p.processo = :" + PARAM_PROCESSO + " and o.documentoBin.minuta = false";
    
    String LIST_DOCUMENTO_MINUTA_BY_PROCESSO = "listProcessoDocumentoMinutaByProcesso";
    String LIST_DOCUMENTO_MINUTA_BY_PROCESSO_QUERY = "select o from Documento o inner join o.pasta p " +
    		"where p.processo = :" + PARAM_PROCESSO + " and o.documentoBin.minuta = true";
    
    String LIST_DOCUMENTO_BY_TASKINSTANCE = "listDocumentoByTaskInstance";
    String lIST_DOCUMENTO_BY_TASKINSTANCE_QUERY = "select o from Documento o where idJbpmTask = :" + ID_JBPM_TASK_PARAM;
    
    String TOTAL_DOCUMENTOS_PROCESSO = "Documento.totalDocumentosProcesso";
    String TOTAL_DOCUMENTOS_PROCESSO_QUERY = "select count(o) from Documento o inner join o.pasta p where p.processo = :" + PARAM_PROCESSO 
    		+ " and o.documentoBin.minuta = false";
    
    String DOCUMENTOS_SESSAO_ANEXAR = "Documento.documentosSessaoAnexar";
    String DOCUMENTOS_SESSAO_ANEXAR_QUERY = "select o from Documento o inner join o.pasta p where p.processo = :" + PARAM_PROCESSO
    		+ " and (o.documentoBin.minuta = true or o.id in (:" + PARAM_IDS_DOCUMENTO + "))";

    String DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO = "Documento.porClassificacaoDocumentoOrdenadosPorDataInclusao";
    String DOCUMENTOS_POR_CLASSIFICACAO_DOCUMENTO_ORDENADOS_POR_DATA_INCLUSAO_QUERY = "select d from ClassificacaoDocumento cd inner join cd.documentoList d"
            + " where cd = :" + PARAM_CLASSIFICACAO_DOCUMENTO + " order by d.dataInclusao desc";
}
