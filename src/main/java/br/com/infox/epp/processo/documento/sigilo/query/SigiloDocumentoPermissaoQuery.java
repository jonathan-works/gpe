package br.com.infox.epp.processo.documento.sigilo.query;

public interface SigiloDocumentoPermissaoQuery {
    String TABLE_NAME = "tb_sigilo_documento_permissao";
    String SEQUENCE_NAME = "sq_sigilo_documento_permissao";
    String COLUMN_ID = "id_sigilo_documento_permissao";
    String COLUMN_ID_SIGILO_DOCUMENTO = "id_sigilo_documento";
    String COLUMN_ID_USUARIO_LOGIN = "id_usuario_login";
    String COLUMN_ATIVO = "in_ativo";

    String QUERY_PARAM_SIGILO_DOCUMENTO = "sigiloDocumento";
    String QUERY_PARAM_USUARIO = "usuario";
    String QUERY_PARAM_IDS_DOCUMENTO = "idsDocumento";
    String QUERY_PARAM_PROCESSO = "processo";

    String NAMED_QUERY_USUARIO_POSSUI_PERMISSAO = "SigiloDocumentoPermissao.usuarioPossuiPermissao";
    String QUERY_USUARIO_POSSUI_PERMISSAO = "select o from SigiloDocumentoPermissao o where o.ativo = true and "
            + "o.sigiloDocumento = :"
            + QUERY_PARAM_SIGILO_DOCUMENTO
            + " and o.usuario = :" + QUERY_PARAM_USUARIO;

    String NAMED_QUERY_INATIVAR_PERMISSOES = "SigiloDocumentoPermissao.inativarPermissoes";
    String QUERY_INATIVAR_PERMISSOES = "update SigiloDocumentoPermissao o set o.ativo = false where o.sigiloDocumento = :"
            + QUERY_PARAM_SIGILO_DOCUMENTO;

    String NAMED_QUERY_PERMISSOES_DO_SIGILO = "SigiloDocumentoPermissao.permissoesDoSigilo";
    String QUERY_PERMISSOES_DO_SIGILO = "select o from SigiloDocumentoPermissao o where o.ativo = true and o.sigiloDocumento = :"
            + QUERY_PARAM_SIGILO_DOCUMENTO;

    String NAMED_QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS = "SigiloDocumentoPermissao.usuarioPossuiPermissaoDocumentos";
    String QUERY_USUARIO_POSSUI_PERMISSAO_DOCUMENTOS = "select o from SigiloDocumentoPermissao o "
            + "inner join o.sigiloDocumento s "
            + "where o.ativo = true and "
            + "s.ativo = true and "
            + "s.documento.id in (:"
            + QUERY_PARAM_IDS_DOCUMENTO
            + ") and "
            + "o.usuario = :"
            + QUERY_PARAM_USUARIO;

    String NAMED_QUERY_DOCUMENTOS_PERMITIDOS = "SigiloDocumentoPermissao.documentosPermitidos";
    String QUERY_DOCUMENTOS_PERMITIDOS = "select o from Documento o "
            + "where o.processo = :"
            + QUERY_PARAM_PROCESSO
            + " and (not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o) or "
            + "exists (select 1 from SigiloDocumentoPermissao sp where sp.usuario = :"
            + QUERY_PARAM_USUARIO
            + " and sp.ativo = true and "
            + "sp.sigiloDocumento = (select s from SigiloDocumento s where s.ativo = true and s.documento = o)"
            + ")" + ")";
}
