package br.com.infox.epp.meiocontato.query;

public interface MeioContatoQuery {
	
	String PARAM_PESSOA = "pessoa";
	String PARAM_TIPO_CONTATO = "tipoContato";
	String PARAM_VALOR = "valorMeioContato";
	
	String MEIO_CONTATO_BY_PESSOA = "meioContatoByPessoa";
	String MEIO_CONTATO_BY_PESSOA_QUERY = "select u from MeioContato u where u.pessoa=:"
			+ PARAM_PESSOA;
	
	String MEIO_CONTATO_BY_PESSOA_AND_TIPO = "meioContatoByPessoaAndTipo";
	String MEIO_CONTATO_BY_PESSOA_AND_TIPO_QUERY = "select o from MeioContato o "
			+ "where o.pessoa = :" + PARAM_PESSOA 
			+ " and o.tipoMeioContato = :" + PARAM_TIPO_CONTATO;
	
	String EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR = "MeioContat.pessoa.tipo.valor";
	String EXISTE_MEIO_CONTATO_BY_PESSOA_TIPO_VALOR_QUERY = "select count(o) from MeioContato o"
			+ " where o.tipoMeioContato = :" + PARAM_TIPO_CONTATO
			+ " and o.pessoa = :" + PARAM_PESSOA
			+ " and o.meioContato = :" + PARAM_VALOR;
			
}
