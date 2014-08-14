package br.com.infox.epp.tce.prestacaocontas.modelo.query;

public interface ModeloPrestacaoContasQuery {
    String PARAM_MODELO = "modelo";
    
    String TOTAL_RESPONSAVEIS_ASSOCIADOS = "ModeloPrestacaoContas.totalResponsaveisAssociados";
    String TOTAL_RESPONSAVEIS_ASSOCIADOS_QUERY = "select count(o) from ResponsavelModeloPrestacaoContas o "
            + "where o.modeloPrestacaoContas = :" + PARAM_MODELO;
    
    String TOTAL_DOCUMENTOS_ASSOCIADOS = "ModeloPrestacaoContas.totalDocumentosAssociados";
    String TOTAL_DOCUMENTOS_ASSOCIADOS_QUERY = "select count(o) from ModeloPrestacaoContasClassificacaoDocumento o "
            + "where o.modeloPrestacaoContas = :" + PARAM_MODELO;
}
