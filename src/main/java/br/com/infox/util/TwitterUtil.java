package br.com.infox.util;

import java.io.IOException;

import br.com.infox.ibpm.entity.ContaTwitter;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Jonas
 *
 */
public class TwitterUtil {
	
	//TODO inicializar a factory usando com new TwitterFactory(init()) assim que o init() for conclu�do
	private final static TwitterFactory factory = new TwitterFactory();
	private final static Twitter aplicacao = loadApplicationTwitter(); 	
	
//  ============================= M�todos que utilizam com o twitter da aplica��o =============================
	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplica��o. Todos os usu�rios que seguem a aplica��o recebem essa atualiza��o.
	 * @param news o conte�do da atualiza��o
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao
	 */
	public static Status sendNews(String news) throws TwitterException{
		return aplicacao.updateStatus(news);		
	}
	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplica��o e tamb�m o marca como favorito. Todos os usu�rios que seguem a aplica��o recebem essa atualiza��o
	 * @param news o conte�do da atualiza��o
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao.
	 */
	public static Status sendNewsFavorite(String news) throws TwitterException{
		Status status = sendNews(news);
		return aplicacao.createFavorite(status.getId());
	}
	
	/**
	 * Envia uma mensagem direta (DM) do twitter da aplica��o para o twitter passado 
	 * @param receiver a conta do twitter que dever� receber a DM
	 * @param message o conte�do da DM
	 * @throws TwitterException
	 * @return o objeto DirectMessage
	 */
	public static DirectMessage sendDirectMessage(ContaTwitter receiver, String message) throws TwitterException{
		return aplicacao.sendDirectMessage(receiver.getIdTwitter(), message);
	}
	
	/**
	 * Bloqueia a conta de Twitter passada para que esta n�o receba mais as atualiza��es da aplica��o
	 * @param contaTwitter a conta do twitter a ser bloqueada
	 * @throws TwitterException
	 * @return o usu�rio do twitter que foi bloqueado
	 */
	public static User blockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.createBlock(contaTwitter.getIdTwitter());
	}
	
	/**
	 * Desloqueia a conta de Twitter passada para que esta n�o receba mais as atualiza��es da aplica��o
	 * @param contaTwitter a conta do twitter a ser desbloqueada
	 * @throws TwitterException
	 * @return o usu�rio do twitter que foi desbloqueado
	 */
	public static User unblockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyBlock(contaTwitter.getIdTwitter());
	}
	
	public static User followUser(ContaTwitter contaTwitter) throws IllegalStateException, TwitterException{
		return aplicacao.createFriendship(contaTwitter.getIdTwitter());
	}
	
	public static User unfollowUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyFriendship(contaTwitter.getIdTwitter());
	}
	
	public static void asdf() throws TwitterException{
		aplicacao.getAccountSettings();
	}
	
	
//  ========== M�todos que utilizam com o twitter dos usu�rios ==========
	
	/**
	 * Atualiza o status do usu�rio cuja conta � passada como par�metro
	 * @param contaTwitter
	 * @param status
	 * @throws TwitterException
	 */
	public static void updateStatus(ContaTwitter contaTwitter, String status) throws TwitterException{
		createTwitter(contaTwitter).updateStatus(status);
	}
	
	public static void followApplication(ContaTwitter contaTwitter) throws IllegalStateException, TwitterException{
		createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
	}
	
	public static void mutualFollow(ContaTwitter contaTwitter) throws IllegalStateException, TwitterException{
		Twitter twitter = createTwitter(contaTwitter);
		twitter.createFriendship(aplicacao.getId(), true);
		aplicacao.createFriendship(twitter.getId(), true);
	}
	
	
//  ======================= M�todos Inicializadoes e Auxiliares Privados ===================
	
	//TODO fazer com que esse m�todo inicialize o twitter da aplica��o (esperando definir melhor como o twitter da aplica��o ser� salvo) 
	private final static Twitter loadApplicationTwitter(){
		return factory.getInstance(loadAccessToken());
	}
	
	private static Twitter createTwitter(ContaTwitter contaTwitter){
		Twitter twitter = factory.getInstance();
		twitter.setOAuthAccessToken(contaTwitter.getAccessToken());
		return twitter;
	}
	
	//TODO
	/**
	 * M�todo est�tico para setar as configura��es necess�rias para a Factory que instancia os objetos Twitter
	 * Utiliza os par�metros do sistema "oauthConsumerKey" e "oauthConsumerSecret" que s�o gerados quando a aplica��o
	 * � registrada em https://dev.twitter.com/apps
	 * */
	private final static Configuration init()	{
		ConfigurationBuilder builder = new ConfigurationBuilder();
		try {
			builder.setOAuthConsumerKey(br.com.infox.util.ParametroUtil.getParametro("oauthConsumerKey"));
		} catch (IllegalStateException ise){
			
		}
		try{
			builder.setOAuthConsumerSecret(br.com.infox.util.ParametroUtil.getParametro("oauthConsumerSecret"));
		} catch (IllegalStateException ise){
			
		}
		return builder.build();	
	}
	
//  ============== M�todos para testes locais - Remover antes de colocar em Produ��o ==========================	
	
	//TODO remover esse m�todo - utilizado somente para testes locais
	private static AccessToken loadAccessToken(){
	    String token = "980311075-QPU70Kmu7DJE44eBV0CzsyJje2rQel7y4q35EBAF";
	    String tokenSecret = "qehADZbdRPYrvdpHjuh5xDtNdhKDNPo3v5lGZEC5ng";
	    return new AccessToken(token, tokenSecret);
	}
	
	//TODO remover esse m�todo - utilizado somente para testes locais
	private static AccessToken loadAccessToken2(){
	    String token = "811368666-5eQBz3BeMlo6J7dpaqorJu6egiqyPb4VeecLrNdV";
	    String tokenSecret = "G50BaEgzWHIX9fsLzfEY2zhc75F0hVicoiJB7YbyYPA";
	    return new AccessToken(token, tokenSecret);
	}
	
	//TODO remover esse m�todo - utilizado somente para testes locais
	public static void main(String[] args) throws TwitterException, IOException {
//		ContaTwitter ct = new ContaTwitter();
//		ct.setAccessToken(loadAccessToken2());
//		ct.setScreenName("infoxPP");
//		updateStatus(ct, "Testando o e-PP");
//		followApplication(ct);
		sendNewsFavorite("new teste3");
	}
}
