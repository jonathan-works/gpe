package br.com.infox.epp.pessoa.documento.query;

public interface PessoaDocumentoQuery {
	
	String PARAM_MATRICULA = "matricula";
	String PARAM_PESSOA = "pessoa";
	String PARAM_TPDOCUMENTO = "tpDocumento";
	
	String PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO = "pessoaDocumentoByPessoaTpDocumento";
	String PESSOA_DOCUMENTO_BY_PESSOA_TPDOCUMENTO_QUERY = "select o from PessoaDocumento o "
			+ "where o.pessoa = :" + PARAM_PESSOA + " and o.tipoDocumento = :" + PARAM_TPDOCUMENTO;
	String USUARIO_POR_DOCUMENTO_E_TIPO = "usuarioPorDocumentoETipo";
	String USUARIO_POR_DOCUMENTO_E_TIPO_QUERY = "select o from UsuarioLogin o "
			+ "inner join o.pessoaFisica pf "
			+ "inner join pf.pessoaDocumentoList pdl where "
			+ "pdl.tipoDocumento =:" + PARAM_TPDOCUMENTO + " and pdl.documento =:" + PARAM_MATRICULA;
	

}
