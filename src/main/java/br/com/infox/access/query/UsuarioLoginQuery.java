package br.com.infox.access.query;

public interface UsuarioLoginQuery {

	public static final String PARAM_LOGIN = "login";
	public static final String USUARIO_LOGIN_NAME = "usuarioLogadoByLogin";
	public static final String USUARIO_LOGIN_QUERY = "select u from Usuario u where login = :" + PARAM_LOGIN;
}
