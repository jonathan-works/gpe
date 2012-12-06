package br.com.infox.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import br.com.infox.ibpm.entity.ContaTwitter;

import twitter4j.Twitter;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterUtil {
	
	private final Logger logger = Logger.getLogger(TwitterUtil.class.getName());
	private final static Twitter factory = TwitterFactory.getSingleton();
	
	public static void main(String[] args) throws TwitterException, IOException {
		new TwitterUtil().republish();
	}
	
	/**
	 * Método para envio de DirectMessages via Twitter
	 * @param twitter ContaTwitter - Conta para a qual se deseja enviar a DM
	 * @return Boolean - Mensagem enviada
	 * */
	public static boolean sendDirectMessage(ContaTwitter twitter, String message){
		try {
			factory.sendDirectMessage(twitter.getIdTwitter(), message);
			return true;
		} catch (TwitterException te){
			te.printStackTrace();
			return false;
		}
		
	}
	
	
	private void republish() throws TwitterException, IOException{
		Twitter twitter = TwitterFactory.getSingleton();
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (accessToken == null){
			System.out.println("Open the following URL and grant access to your account:");
		    System.out.println(requestToken.getAuthorizationURL());
		    System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
		    String pin = br.readLine();
		    try{
		         if(pin.length() > 0){
		           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		           System.out.println(accessToken);
		         }else{
		           accessToken = twitter.getOAuthAccessToken();
		         }
		    } catch (TwitterException te) {
		        if(401 == te.getStatusCode()){
		          System.out.println("Unable to get the access token.");
		        }else{
		          te.printStackTrace();
		        }
		    }
		}
	}
	
	private void publish(){
		String message = "O e-PP está agora no Twitter";
		try{
			Twitter twitter = new TwitterFactory().getInstance();
			try {
				RequestToken requestToken = twitter.getOAuthRequestToken();
				AccessToken accessToken = null;
				while (accessToken == null){
					logger.fine("Clique no link para permitir que aplicação acesse a sua conta:");
					logger.fine(requestToken.getAuthorizationURL());
					try{
						accessToken = twitter.getOAuthAccessToken(requestToken);
					} catch (TwitterException te){
						if (te.getStatusCode() == 401){
							logger.severe("Não foi possível obter o Token de Acesso");
						} else {
							te.printStackTrace();
						}
					}
				}
				logger.info("Token de acesso obtido com sucesso.");
				logger.info("Token de Acesso: " + accessToken.getToken());
				logger.info("Código do Token de Acesso" + accessToken.getTokenSecret());
			} catch (IllegalStateException ise) {
				if (!twitter.getAuthorization().isEnabled()){
					logger.severe("OAuth consumer key/secret is not set.");
					return;
				}
			}
			Status status = twitter.updateStatus(message);
			logger.info("Status atualizado com suscesso para [" + status.getText() + "]");
		} catch (TwitterException te){
			te.printStackTrace();
			logger.severe("Não foi possivel obter a timeline: " + te.getMessage());
		}	
	}
}
