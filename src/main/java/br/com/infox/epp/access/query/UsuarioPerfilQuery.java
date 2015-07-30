package br.com.infox.epp.access.query;

public interface UsuarioPerfilQuery {

    String PARAM_USUARIO_LOGIN = "usuarioLogin";
    String PARAM_PERFIL_TEMPLATE = "perfilTemplate";
    String PARAM_DS_PERFIL_TEMPLATE = "descricaoPerfilTemplate";
    String PARAM_LOCALIZACAO = "localizacao";
    String PARAM_ATIVO = "ativo";

    String GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO = "UsuarioPerfil.getByUsuarioLoginPerfilTemplateLocalizacao";
    String GET_BY_USUARIO_LOGIN_PERFIL_TEMPLATE_LOCALIZACAO_QUERY = "select o from UsuarioPerfil o where o.usuarioLogin = :"+ PARAM_USUARIO_LOGIN + 
    		" and o.perfilTemplate = :" + PARAM_PERFIL_TEMPLATE + 
    		" and o.localizacao = :" + PARAM_LOCALIZACAO + 
    		" and o.ativo = :" + PARAM_ATIVO;

    String LIST_BY_USUARIO_LOGIN = "UsuarioPerfil.listByUsuarioLogin";
    String LIST_BY_USUARIO_LOGIN_QUERY = "select o "
            + "from UsuarioPerfil o "
                + "inner join o.localizacao l "
                + "inner join o.perfilTemplate pt "
            + "where o.ativo = true "
                + "and pt.ativo = true "
                + "and o.usuarioLogin = :" + PARAM_USUARIO_LOGIN + " "
            + "order by l.localizacao asc, pt.descricao asc";
    
    String EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO = "existeUsuarioComPerfilDescricaoAtivo";
    String EXISTE_USUARIO_COM_DESCRICAO_PERFIL_ATIVO_QUERY = "select count(*) from UsuarioPerfil up " 
    		+ "inner join up.perfilTemplate pt where pt.descricao = :" + PARAM_DS_PERFIL_TEMPLATE
    		+ " and up.usuarioLogin = :" + PARAM_USUARIO_LOGIN
    		+ " and up.ativo = :" + PARAM_ATIVO;
    

}
