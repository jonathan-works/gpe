package br.com.infox.epp.access.query;

public interface UsuarioPerfilQuery {

    String PARAM_USUARIO_LOGIN = "usuarioLogin";
    String PARAM_PERFIL_TEMPLATE = "perfilTemplate";
    String PARAM_LOCALIZACAO = "localizacao";

    String GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO = "UsuarioPerfil.getByUsuarioLoginPerfilTemplateLocalizacao";
    String GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO_QUERY = "select o "
            + "from UsuarioPerfil o " + "where o.usuarioLogin = :"
            + PARAM_USUARIO_LOGIN + " and o.perfilTemplate = :"
            + PARAM_PERFIL_TEMPLATE + " and o.localizacao = :"
            + PARAM_LOCALIZACAO;

    String LIST_BY_USUARIO_LOGIN = "UsuarioPerfil.listByUsuarioLogin";
    String LIST_BY_USUARIO_LOGIN_QUERY = "select o from UsuarioPerfil o where o.usuarioLogin = :"
            + PARAM_USUARIO_LOGIN;

}
