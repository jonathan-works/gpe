package br.com.infox.epp.processo.documento.query;

public interface PastaQuery {
    String PARAM_PROCESSO = "processo";
    String PARAM_PASTA = "pasta";
    String PARAM_IDS_DOCUMENTOS = "idsDocumentos";
    String PARAM_LOCALIZACAO = "localizacao";
    
    String GET_BY_PROCESSO = "getByProcesso";
    String GET_BY_PROCESSO_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO
            + " order by o.nome";
    
    String TOTAL_DOCUMENTOS_PASTA = "Pasta.totalDocumentosPasta";
    String TOTAL_DOCUMENTOS_PASTA_QUERY = "select count(o) from Documento o inner join o.documentoBin bin "
    		+ " where o.pasta = :" + PARAM_PASTA
    		+ " and bin.minuta = false ";
    
    String FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR = " and (DocumentoSuficientementeAssinado(o.id) = true or o.localizacao = :" + PARAM_LOCALIZACAO + ") ";
    String FILTER_SUFICIENTEMENTE_ASSINADO = " and DocumentoSuficientementeAssinado(o.id) = true ";

    String FILTER_SIGILO = " and not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) ";
    String FILTER_EXCLUIDO = " and o.excluido = false ";
    String FILTER_DOCUMENTOS = " and o.id not in (:" + PARAM_IDS_DOCUMENTOS + ") ";
}
