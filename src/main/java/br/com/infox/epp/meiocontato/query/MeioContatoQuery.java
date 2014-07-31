package br.com.infox.epp.meiocontato.query;

public interface MeioContatoQuery {
	
	String PARAM_PESSOA = "pessoa";
	
	String MEIO_CONTATO_BY_PESSOA = "meioContatoByPessoa";
	String MEIO_CONTATO_BY_PESSOA_QUERY = "select u from MeioContato u where u.pessoa=:"
			+ PARAM_PESSOA;
}
