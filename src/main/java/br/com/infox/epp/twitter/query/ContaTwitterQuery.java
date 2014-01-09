package br.com.infox.epp.twitter.query;

public interface ContaTwitterQuery {
    
    String LOCALIZACAO_PARAM = "localizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO = "getContaTwitterByLocalizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO_QUERY = "select o from ContaTwitter o where o.localizacao = :" 
            + LOCALIZACAO_PARAM;
    
    String USUARIO_PARAM = "usuario";
    String CONTA_TWITTER_BY_USUARIO = "getContaTwitterByUsuario";
    String CONTA_TWITTER_BY_USUARIO_QUERY = "select o from ContaTwitter o where o.usuario = :" + USUARIO_PARAM;
    
    String ID_USUARIO_PARAM = "idUsuario";
    String CONTA_TWITTER_BY_ID_USUARIO = "getContaTwitterByIdUsuario";
    String CONTA_TWITTER_BY_ID_USUARIO_QUERY = "select o from ContaTwitter o where o.usuario.idUsuarioLogin = :"
            + ID_USUARIO_PARAM;

}
