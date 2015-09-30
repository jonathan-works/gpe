package br.com.infox.epp.processo.documento.sigilo.query;


public interface SigiloDocumentoQuery {
    String TABLE_NAME = "tb_sigilo_documento";
    String SEQUENCE_NAME = "sq_tb_sigilo_documento";
    String COLUMN_ID = "id_sigilo_documento";
    String COLUMN_ID_DOCUMENTO = "id_documento";
    String COLUMN_ID_USUARIO_LOGIN = "id_usuario_login";
    String COLUMN_MOTIVO = "ds_motivo";
    String COLUMN_DATA_INCLUSAO = "dt_inclusao";
    String COLUMN_ATIVO = "in_ativo";

    String QUERY_PARAM_USUARIO_LOGIN = "usuarioLogin";
    String QUERY_PARAM_PROCESSO = "processo";
    String QUERY_PARAM_LOCALIZACAO = "localizacao";
    String QUERY_PARAM_DOCUMENTO = "documento";
    String QUERY_PARAM_ID_DOCUMENTO = "idDocumento";

    String NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO = "SigiloDocumento.sigiloDocumentoAtivo";
    String QUERY_SIGILO_DOCUMENTO_ATIVO = "select o from SigiloDocumento o where o.ativo = true and o.documento = :"
            + QUERY_PARAM_DOCUMENTO;
    
    String NAMED_QUERY_SIGILO_DOCUMENTO_USUARIO_LOGIN_ATIVO = "SigiloDocumento.sigiloDocumentoUsuarioLoginAtivo";
    String QUERY_SIGILO_DOCUMENTO_USUARIO_LOGIN_ATIVO = "select o from SigiloDocumento o where  o.documento = :"
    		+ QUERY_PARAM_DOCUMENTO + " and o.usuario = :" + QUERY_PARAM_USUARIO_LOGIN;
    
    String NAMED_QUERY_DOCUMENTOS_ATIVO_PESSOA = "SigiloDocumento.documentosSigilososPessoa";
    String QUERY_DOCUMENTOS_ATIVO_PESSOA = "select o from SigiloDocumento o "
    										+ "inner join o.usuario ul "
    										+ "inner join o.documento d "
    										+ "where o.ativo = true "
    										+ "and d.processo = :" + QUERY_PARAM_PROCESSO +" "
    										+ "and d.documentoBin.minuta = false "
    										+ "and exists (select up from UsuarioPerfil up "
    										+ "where up.usuarioLogin = ul "
    										+ "and up.localizacao = :"+ QUERY_PARAM_LOCALIZACAO +" )";
     

    String NAMED_QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO = "SigiloDocumento.sigiloDocumentoAtivoPorIdDocumento";
    String QUERY_SIGILO_DOCUMENTO_ATIVO_POR_ID_DOCUMENTO = "select o from SigiloDocumento o where o.ativo = true and o.documento.id = :"
            + QUERY_PARAM_ID_DOCUMENTO;

    String NAMED_QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO = "SigiloDocumento.documentoSigilosoPorIdDocumento";
    String QUERY_DOCUMENTO_SIGILOSO_POR_ID_DOCUMENTO = "select 1 from SigiloDocumento o where o.ativo = true and o.documento.id = :"
            + QUERY_PARAM_ID_DOCUMENTO;

    String NAMED_QUERY_INATIVAR_SIGILOS = "SigiloDocumento.inativarSigilos";
    String QUERY_INATIVAR_SIGILOS = "update SigiloDocumento o set o.ativo = false where o.documento = :"
            + QUERY_PARAM_DOCUMENTO;
}
