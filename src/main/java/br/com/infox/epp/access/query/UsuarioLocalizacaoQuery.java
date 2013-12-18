package br.com.infox.epp.access.query;

public interface UsuarioLocalizacaoQuery {
	
	String PARAM_PAPEL = "papel";
	String PARAM_USUARIO = "usuario";
	String PARAM_ESTRUTURA = "estrutura";
	String PARAM_LOCALIZACAO = "localizacao";
	
	String EXISTE_USUARIO_LOCALIZACAO_QUERY = "select count(o) from UsuarioLocalizacao o"
			+ " where o.papel = :" + PARAM_PAPEL + " and o.usuario = :" + PARAM_USUARIO
			+ " and o.localizacao = :" + PARAM_LOCALIZACAO;
	
	String ESTRUTURA_NULL_CONDITION = " and o.estrutura is null";
	String ESTRUTURA_CONDITION = " and o.estrutura = :" + PARAM_ESTRUTURA;
}
