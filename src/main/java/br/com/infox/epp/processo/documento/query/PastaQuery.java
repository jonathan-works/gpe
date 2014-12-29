package br.com.infox.epp.processo.documento.query;

public interface PastaQuery {
    String PARAM_PROCESSO = "processo";
    String PARAM_PASTA = "pasta";
    String PARAM_IDS_DOCUMENTOS_BIN = "idsDocumentos";
    
    String GET_BY_PROCESSO = "getByProcesso";
    String GET_BY_PROCESSO_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO
            + " order by o.nome";
    String GET_DEFAULT_BY_PROCESSO = "getDefaultByProcesso";
    String GET_DEFAULT_BY_PROCESSO_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO
            + " order by o.id"; 
    
    String TOTAL_DOCUMENTOS_PASTA = "Pasta.totalDocumentosPasta";
    String TOTAL_DOCUMENTOS_PASTA_QUERY = "select count(o) from Documento o inner join o.documentoBin bin "
    		+ " where o.pasta = :" + PARAM_PASTA
    		+ " and bin.minuta = false";
    
    String FILTER_SIGILO = " and not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) ";
    String FILTER_EXCLUIDO = " and o.excluido = false ";
    String FILTER_DOCUMENTOS_BIN = " and bin.id not in (:" + PARAM_IDS_DOCUMENTOS_BIN + ") ";
}
