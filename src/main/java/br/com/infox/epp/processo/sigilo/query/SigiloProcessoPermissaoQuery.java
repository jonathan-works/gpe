package br.com.infox.epp.processo.sigilo.query;

public interface SigiloProcessoPermissaoQuery {
    String TABLE_NAME = "tb_sigilo_processo_permissao";
    String SEQUENCE_NAME = "sq_sigilo_processo_permissao";
    String COLUMN_ID = "id_sigilo_processo_permissao";
    String COLUMN_ID_SIGILO_PROCESSO = "id_sigilo_processo";
    String COLUMN_ID_USUARIO_LOGIN = "id_usuario_login";
    String COLUMN_ATIVO = "in_ativo";

    String QUERY_PARAM_SIGILO_PROCESSO = "sigiloProcesso";
    String QUERY_PARAM_USUARIO = "usuario";

    String NAMED_QUERY_USUARIO_POSSUI_PERMISSAO = "SigiloProcessoPermissao.usuarioPossuiPermissao";
    String QUERY_USUARIO_POSSUI_PERMISSAO = "select o from SigiloProcessoPermissao o where o.ativo = true and "
            + "o.sigiloProcesso = :"
            + QUERY_PARAM_SIGILO_PROCESSO
            + " and "
            + "o.usuario = :" + QUERY_PARAM_USUARIO;

    String NAMED_QUERY_INATIVAR_PERMISSOES = "SigiloProcessoPermissao.inativarPermissoes";
    String QUERY_INATIVAR_PERMISSOES = "update SigiloProcessoPermissao o set o.ativo = false where o.sigiloProcesso = :"
            + QUERY_PARAM_SIGILO_PROCESSO;

    String NAMED_QUERY_PERMISSOES_DO_SIGILO = "SigiloProcessoPermissao.permissoesDoSigilo";
    String QUERY_PERMISSOES_DO_SIGILO = "select o from SigiloProcessoPermissao o where o.ativo = true and o.sigiloProcesso = :"
            + QUERY_PARAM_SIGILO_PROCESSO;
}
