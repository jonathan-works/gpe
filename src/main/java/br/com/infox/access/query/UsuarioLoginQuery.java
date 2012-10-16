package br.com.infox.access.query;

public interface UsuarioLoginQuery {

	String PARAM_LOGIN = "login";
	String USUARIO_LOGIN_NAME = "usuarioLogadoByLogin";
	String USUARIO_LOGIN_QUERY = "select u from Usuario u where login = :" + PARAM_LOGIN;
}
