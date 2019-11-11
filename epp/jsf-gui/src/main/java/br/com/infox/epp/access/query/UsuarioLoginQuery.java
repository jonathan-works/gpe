package br.com.infox.epp.access.query;

public interface UsuarioLoginQuery {

    // Entity Mapping
    String TABLE_USUARIO_LOGIN = "tb_usuario_login";
    String SEQUENCE_USUARIO = "sq_tb_usuario_login";
    String ID_USUARIO = "id_usuario_login";
    String LOGIN = "ds_login";
    String EMAIL = "ds_email";
    String SENHA = "ds_senha";
    String NOME_USUARIO = "nm_usuario";
    String TIPO_USUARIO = "tp_usuario";
    String BLOQUEIO = "in_bloqueio";
    String PROVISORIO = "in_provisorio";
    String DATA_EXPIRACAO = "dt_expiracao_usuario";

    String PARAM_LOCALIZACAO = "localizacao";
	String PARAM_PAPEIS = "papeis";
    String PARAM_LOGIN = "login";
    String PARAM_ID_TASK_INSTANCE = "idTaskInstance";
    String PARAM_ID_TAREFA = "idTarefa";
    String PARAM_NR_CPF = "nrCpf";
    String USUARIO_LOGIN_NAME = "usuarioLogadoByLogin";
    String USUARIO_LOGIN_QUERY = "select u from UsuarioLogin u where login = :"
            + PARAM_LOGIN;

    String PARAM_EMAIL = "email";
    String USUARIO_BY_EMAIL = "usuarioLoginByEmail";
    String USUARIO_LOGIN_EMAIL_QUERY = "select u from UsuarioLogin u where u.email = :"
            + PARAM_EMAIL;

    String PARAM_PESSOA_FISICA = "pessoaFisica";

    String USUARIO_BY_PESSOA = "usuarioLoginByPessoaFisica";
    String USUARIO_BY_PESSOA_QUERY = "select u from UsuarioLogin u where u.pessoaFisica=:"
            + PARAM_PESSOA_FISICA;

    String USUARIO_BY_LOGIN_TASK_INSTANCE = "usuarioByLoginTaskinstance";
    String USUARIO_BY_LOGIN_TASK_INSTANCE_QUERY = "select o from UsuarioLogin o"
            + " where o.login = :"
            + PARAM_LOGIN
            + " and not exists (from UsuarioTaskInstance"
            + " where idTaskInstance = :" + PARAM_ID_TASK_INSTANCE + ")";

    String PARAM_ID = "idUsuarioLogin";
    String INATIVAR_USUARIO = "inativarUsuario";
    String INATIVAR_USUARIO_QUERY = "UPDATE UsuarioLogin u SET u.ativo = false WHERE u.idUsuarioLogin = :"
            + PARAM_ID;

    String ID_PROCESSO_PARAM = "idProcesso";

    String USUARIO_BY_ID_TASK_INSTANCE = "getUsuarioByIdTaskInstance";
    String USUARIO_BY_ID_TASK_INSTANCE_QUERY = "SELECT DISTINCT ul.ds_login FROM tb_usuario_login ul "
            + "JOIN tb_usuario_taskinstance uti ON (uti.id_usuario_login = ul.id_usuario_login) "
            + "WHERE id_taskinstance = :" + PARAM_ID_TASK_INSTANCE;
    
    String NOME_USUARIO_BY_ID_TASK_INSTANCE = "getNomeUsuarioByIdTaskInstance";
    String NOME_USUARIO_BY_ID_TASK_INSTANCE_QUERY = "SELECT DISTINCT ul.nm_usuario FROM tb_usuario_login ul "
            + "JOIN tb_usuario_taskinstance uti ON (uti.id_usuario_login = ul.id_usuario_login) "
            + "WHERE id_taskinstance = :" + PARAM_ID_TASK_INSTANCE;
    
    String USUARIO_FETCH_PF_BY_NUMERO_CPF = "usuarioLoginByNumeroCpf";
    String USUARIO_FETCH_PF_BY_NUMERO_CPF_QUERY = "SELECT o FROM UsuarioLogin o " +
    		"INNER JOIN FETCH o.pessoaFisica pf " +
    		"WHERE pf.cpf = :" + PARAM_NR_CPF;
    
    
    String USUARIO_LOGIN_LOCALIZACAO_PAPEL = "usuarioLoginByLocalizacaoPapel";
    String USUARIO_LOGIN_LOCALIZACAO_PAPEL_QUERY = "select distinct ul from UsuarioPerfil up " 
    											+"inner join up.perfilTemplate pt "
    											+"inner join up.usuarioLogin ul "
    											+"inner join pt.papel pap " 
    											+"where up.localizacao = :"+ PARAM_LOCALIZACAO+" "
    											+ "and pap.identificador in ( :" + PARAM_PAPEIS + " ) "
    													+ "and up.ativo = true ";

    String USUARIO_LOGIN_LOCALIZACAO = "usuarioLoginByLocalizacao";
    String USUARIO_LOGIN_LOCALIZACAO_QUERY = "select distinct ul from UsuarioPerfil up "
            +"inner join up.perfilTemplate pt "
            +"inner join up.usuarioLogin ul "
            +"where up.localizacao = :"+ PARAM_LOCALIZACAO+" "
            + "and up.ativo = true ";
}
