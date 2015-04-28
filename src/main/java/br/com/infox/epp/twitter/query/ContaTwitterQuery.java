package br.com.infox.epp.twitter.query;

public interface ContaTwitterQuery {

    String LOCALIZACAO_PARAM = "localizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO = "getContaTwitterByLocalizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO_QUERY = "select o from ContaTwitter o where o.localizacao = :"
            + LOCALIZACAO_PARAM;

    String USUARIO_PARAM = "usuario";
    String CONTA_TWITTER_BY_USUARIO = "getContaTwitterByUsuario";
    String CONTA_TWITTER_BY_USUARIO_QUERY = "select o from ContaTwitter o where o.usuario = :"
            + USUARIO_PARAM;

    String ID_USUARIO_PARAM = "idUsuario";
    String CONTA_TWITTER_BY_ID_USUARIO = "getContaTwitterByIdUsuario";
    String CONTA_TWITTER_BY_ID_USUARIO_QUERY = "select o from ContaTwitter o where o.usuario.idUsuarioLogin = :"
            + ID_USUARIO_PARAM;

    String ID_GRUPO_EMAIL_PARAM = "idGrupoEmail";
    String LIST_TWITTER_BY_ID_GRUPO_EMAIL = "listContaTwitterByIdGrupoEmail";
    String LIST_TWITTER_BY_ID_GRUPO_EMAIL_QUERY = "select distinct c from ContaTwitter c join c.usuario u join u.usuarioPerfilList ul "
            + "where ul.ativo = true and exists (select o from ListaEmail o where o.idGrupoEmail = :"
            + ID_GRUPO_EMAIL_PARAM
            + " and "
            + "((ul.perfilTemplate.localizacao = o.localizacao and (ul.perfilTemplate.papel = o.papel or o.papel is null) and "
            + "(ul.localizacao = o.estrutura or o.estrutura is null)) or (ul.perfilTemplate.papel = o.papel and "
            + "(ul.perfilTemplate.localizacao = o.localizacao or o.localizacao is null) and "
            + "(ul.localizacao = o.estrutura or o.estrutura is null)) or (ul.localizacao = o.estrutura "
            + "and (ul.perfilTemplate.localizacao = o.localizacao or o.localizacao is null) "
            + "and (ul.perfilTemplate.papel = o.papel or o.papel is null))))";

}
