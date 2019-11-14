package br.com.infox.epp.documento.query;

public interface ClassificacaoDocumentoQuery {
	
	String CLASSIFICACAO_DOCUMENTO_PARAM = "classificacaoDocumento";
    String PAPEL_PARAM = "papel";
    String TIPO_DOCUMENTO_PARAM = "tipoDocumento";
    String PARAM_DESCRICAO = "descricao";
    String PARAM_PROCESSO = "processo";

    String ASSINATURA_OBRIGATORIA = "assinaturaObrigatoria";
    String ASSINATURA_OBRIGATORIA_QUERY = "select distinct tpdp "
            + "from ClassificacaoDocumentoPapel tpdp "
            + "where tpdp.classificacaoDocumento = :" + CLASSIFICACAO_DOCUMENTO_PARAM
            + " and tpdp.papel = :" + PAPEL_PARAM;


    String LIST_CLASSIFICACAO_DOCUMENTO = "listClassificacaoDocumento";
    String LIST_CLASSIFICACAO_DOCUMENTO_QUERY = "select o from ClassificacaoDocumento o ";
    
    String CODIGO_DOCUMENTO_PARAM = "codigoDocumento";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO = "findClassificacaoDocumentoByCodigo";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_CODIGO_QUERY = "select o from ClassificacaoDocumento o where o.codigoDocumento = :" + CODIGO_DOCUMENTO_PARAM;
    
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO = "findClassificacaoDocumentoByDescricao";
    String FIND_CLASSIFICACAO_DOCUMENTO_BY_DESCRICAO_QUERY = "select o from ClassificacaoDocumento o where o.descricao like :" + PARAM_DESCRICAO;
    
    String LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO = "listClassificacaoDocumentoByProcesso";
    String LIST_CLASSIFICACAO_DOCUMENTO_BY_PROCESSO_QUERY = "select distinct(cd) from Documento d "
    			+ "inner join d.pasta p "
    			+ "inner join d.classificacaoDocumento cd "
    		+ "where p.processo = :" + PARAM_PROCESSO + " "
    		    + "or d in ("
    		        + "select docComp.documento from DocumentoCompartilhamento docComp "
    		        + "where docComp.processoAlvo = :" + PARAM_PROCESSO + " and docComp.ativo = true"
    		    + ") or p in ("
    		        + "select pastaComp.pasta from PastaCompartilhamento pastaComp "
    		        + "where pastaComp.processoAlvo = :" + PARAM_PROCESSO + " and pastaComp.ativo = true"
    		    + ")"
    		+ "order by cd.descricao";
}
