package br.com.infox.epp.twitter.query;

public interface ContaTwitterQuery {
    
    String LOCALIZACAO_PARAM = "localizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO = "getContaTwitterByLocalizacao";
    String CONTA_TWITTER_BY_LOCALIZACAO_QUERY = "select o from ContaTwitter o where o.localizacao = :" 
            + LOCALIZACAO_PARAM;

}
