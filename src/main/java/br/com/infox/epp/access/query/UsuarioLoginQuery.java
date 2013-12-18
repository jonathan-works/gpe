package br.com.infox.epp.access.query;

public interface UsuarioLoginQuery {
    
    //Entity Mapping
    String TABLE_USUARIO_LOGIN = "tb_usuario_login";
    String SEQUENCE_USUARIO = "public.sq_tb_usuario";
    String ID_USUARIO = "id_usuario_login";
    String LOGIN = "ds_login";
    String EMAIL = "ds_email";
    String SENHA = "ds_senha";
    String NOME_USUARIO = "nm_usuario";
    String TIPO_USUARIO = "tp_usuario";
    String BLOQUEIO = "in_bloqueio";
    String PROVISORIO = "in_provisorio";
    String DATA_EXPIRACAO = "dt_expiracao_usuario";

	String PARAM_LOGIN = "login";
	String PARAM_ID_TASK_INSTANCE = "idTaskInstance";
	String USUARIO_LOGIN_NAME = "usuarioLogadoByLogin";
	String USUARIO_LOGIN_QUERY = "select u from UsuarioLogin u where login = :" + PARAM_LOGIN;
	
	String PARAM_EMAIL = "email";
	String USUARIO_BY_EMAIL = "usuarioLoginByEmail";
	String USUARIO_LOGIN_EMAIL_QUERY = "select u from UsuarioLogin u where u.email = :" + PARAM_EMAIL;
	
	String USUARIO_BY_LOGIN_TASK_INSTANCE = "usuarioByLoginTaskinstance";
	String USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY = "select o from UsuarioLogin o"
            + " where o.login = :" + PARAM_LOGIN
            + " and not exists (from UsuarioTaskInstance"
            + " where idTaskInstance = :" +PARAM_ID_TASK_INSTANCE+")";
	
	String PARAM_ID = "idUsuarioLogin";
	String INATIVAR_USUARIO = "inativarUsuario";
	String INATIVAR_USUARIO_QUERY = "UPDATE UsuarioLogin u SET u.ativo = false WHERE u.idUsuarioLogin = :" + PARAM_ID;
}
