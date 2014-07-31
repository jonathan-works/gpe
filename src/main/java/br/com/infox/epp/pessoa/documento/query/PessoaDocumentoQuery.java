package br.com.infox.epp.pessoa.documento.query;

public interface PessoaDocumentoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_TPDOCUMENTO = "tpDocumento";
	
	String PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO = "pessoaDocumentoByPessoaTpDocumento";
	String PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO_QUERY = "select o from PessoaDocumento o "
			+ "where o.pessoa = :" + PARAM_PESSOA + " and o.tipoDocumento = :" + PARAM_TPDOCUMENTO;

}
