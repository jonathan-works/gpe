package br.com.infox.epp.access.query;

public interface UsuarioLoginQuery {

	String PARAM_LOGIN = "login";
	String PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	String USUARIO_LOGIN_NAME = "usuarioLogadoByLogin";
	String USUARIO_LOGIN_QUERY = "select u from UsuarioLogin u where login = :" + PARAM_LOGIN;
	
	String USUARIO_BY_LOGIN_TASK_INSTANCE = "usuarioByLoginTaskinstance";
	String USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY = "select o from UsuarioLogin o"
            + " where o.login = :" + PARAM_LOGIN
            + " and not exists (from UsuarioTaskInstance"
            + " where idTaskInstance = :" +PARAM_ID_TASK_INSTANCE+")";
}
