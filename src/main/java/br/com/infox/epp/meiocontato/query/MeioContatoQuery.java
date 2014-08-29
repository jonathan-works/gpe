package br.com.infox.epp.meiocontato.query;

public interface MeioContatoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_TIPO_MEIO_CONTATO = "tipoMeioContato";
	
	String MEIO_CONTATO_BY_PESSOA = "meioContatoByPessoa";
	String MEIO_CONTATO_BY_PESSOA_QUERY = "select u from MeioContato u where u.pessoa=:"
			+ PARAM_PESSOA;
	
	String MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO = "MeioContato.meioContatoByPessoaAndTipoMeioContato";
	String MEIO_CONTATO_BY_PESSOA_AND_TIPO_MEIO_CONTATO_QUERY = "select o from MeioContato o where o.pessoa = :" + PARAM_PESSOA
	        + " and o.tipoMeioContato = :" + PARAM_TIPO_MEIO_CONTATO;
}
