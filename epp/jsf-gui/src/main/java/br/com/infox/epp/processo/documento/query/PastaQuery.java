package br.com.infox.epp.processo.documento.query;

public interface PastaQuery {
    String PARAM_PROCESSO = "processo";
    String PARAM_PASTA = "pasta";
    String PARAM_IDS_DOCUMENTOS = "idsDocumentos";
    String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_CLASSIFICACAO_DOCUMENTO = "classificacaoDocumento";
    String PARAM_NOME = "nome";
    String PARAM_NUMERO_SEQ_DOCUMENTO = "numeroSequencialDocumento";
    String PARAM_CODIGO_MARCADOR = "codigoMarcador";
    String PARAM_DESCRICAO = "descricao";
    String PARAM_USUARIO_PERMISSAO = "usuarioPermissao";
    
    String TOTAL_DOCUMENTOS_PASTA = "Pasta.totalDocumentosPasta";
    String TOTAL_DOCUMENTOS_PASTA_QUERY = "select count(o) from Documento o inner join o.documentoBin bin "
    		+ " where o.pasta = :" + PARAM_PASTA
    		+ " and bin.minuta = false ";
    
    String FILTER_CLASSIFICACAO_DOCUMENTO = " and o.classificacaoDocumento.id = :" + PARAM_CLASSIFICACAO_DOCUMENTO;
    String FILTER_NUMERO_SEQ_DOCUMENTO = " and o.numeroSequencialDocumento = :" + PARAM_NUMERO_SEQ_DOCUMENTO;
    String FILTER_MARCADOR_DOCUMENTO = " and exists (select 1 from DocumentoBin docBin inner join docBin.marcadores marc where docBin.id = bin.id and marc.codigo = '{" + PARAM_CODIGO_MARCADOR + "}' ) ";
    String TOTAL_DOCUMENTOS_PASTA_CLASSIFICACAO_DOCUMENTO_QUERY = "select count(o) from Documento o inner join o.documentoBin bin "
    		+ " where o.pasta = :" + PARAM_PASTA
    		+ " and o.classificacaoDocumento = :" + PARAM_CLASSIFICACAO_DOCUMENTO
    		+ " and bin.minuta = false ";
    
    
    String FILTER_SUFICIENTEMENTE_ASSINADO_OU_SETOR = " and (bin.suficientementeAssinado = true or o.localizacao = :" + PARAM_LOCALIZACAO + ") ";
    String FILTER_SUFICIENTEMENTE_ASSINADO = " and bin.suficientementeAssinado = true ";

    String FILTER_SIGILO = " and (not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
    		+ "exists(select 1 from SigiloDocumentoPermissao sp where sp.usuario = :" + PARAM_USUARIO_PERMISSAO + " and sp.ativo = true and "
    		+ "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o))) ";
    String FILTER_EXCLUIDO = " and o.excluido = false ";
    String FILTER_DOCUMENTOS = " and o.id not in (:" + PARAM_IDS_DOCUMENTOS + ") ";
    
    String GET_BY_NOME = "Pasta.getByNome";
    String GET_BY_NOME_QUERY = "select o from Pasta o where o.processo = :" + PARAM_PROCESSO + " and o.nome = :" + PARAM_NOME;
    String GET_BY_PROCESSO_AND_DESCRICAO = "getByProcessoAndDescricao";
    String GET_BY_PROCESSO_AND_DESCRICAO_QUERY = "select o from Pasta o"
            + " where o.processo = :" + PARAM_PROCESSO + " and o.descricao = :" + PARAM_DESCRICAO;
}
