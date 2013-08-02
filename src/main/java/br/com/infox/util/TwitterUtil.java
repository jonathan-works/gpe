package br.com.infox.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.TwitterTemplate;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author jonas
 *
 */
public final class TwitterUtil {
	
	private static TwitterUtil instance = init();
	private TwitterFactory factory;
	private Twitter aplicacao;
	private static final LogProvider LOG = Logging.getLogProvider(TwitterUtil.class);
		
	private TwitterUtil() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(ParametroUtil.getParametro("oauthConsumerKey"));
		builder.setOAuthConsumerSecret(ParametroUtil.getParametro("oauthConsumerSecret"));
		factory = new TwitterFactory(builder.build());
	}
/*	
       =================================== M�todos que utilizam com o twitter da aplica��o =======================================
*/	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplica��o. Todos os usu�rios que seguem a aplica��o recebem essa atualiza��o.
	 * @param news o conte�do da atualiza��o
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao
	 */
	public Status sendNews(String news) throws TwitterException{
		return aplicacao.updateStatus(news);
	}
	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplica��o e tamb�m o marca como favorito. Todos os usu�rios que seguem a aplica��o recebem essa atualiza��o
	 * @param news o conte�do da atualiza��o
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao.
	 */
	public Status sendNewsFavorite(String news) throws TwitterException{
		Status status = sendNews(news);
		return aplicacao.createFavorite(status.getId());
	}
	
	/**
	 * Faz com que o Twitter da aplica��o retweet o Status passado com parametro
	 * @param status
	 * @return o status retweetado
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando o twitter da aplica��o n�o tem permiss�o para enxergar aquele tweet
	 */
	public Status retweet(Status status) throws TwitterException{
		return aplicacao.retweetStatus(status.getId());
	}
	
	/**
	 * Atualiza o status do twitter da aplicacao com uma mensagem que faz uma "mention" direta ao receiver
	 * @param receiver a conta que ser� citada na mensagem
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public Status sendMessage(ContaTwitter receiver, String message) throws TwitterException {
		String newMessage = "@" + receiver.getScreenName() + " " + message;
		return aplicacao.updateStatus(newMessage);
	}
	
	/**
	 * Atualiza o status do twitter da aplicacao com uma mensagem para cada receiver da lista que for passada e fazendo tamb�m uma "mention" a cada um deles
	 * @param receivers a lista de contas que ser�o citadas nas mensgens
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public List<Status> sendMessage(List<ContaTwitter> receivers, String message) throws TwitterException {
		List<Status> statusList = new ArrayList<Status>();
		for (ContaTwitter receiver : receivers){
			String newMessage = "@" + receiver.getScreenName() + " " + message;
			statusList.add(aplicacao.updateStatus(newMessage));
		}
		return statusList;
	}
	
	/**
	 * Envia uma mensagem direta (DM) do twitter da aplica��o para o twitter passado 
	 * @param receiver a conta do twitter que dever� receber a DM
	 * @param message o conte�do da DM
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou quando a String message possui mais de 140 caracteres
	 * @return o objeto DirectMessage
	 */
	public DirectMessage sendDirectMessage(ContaTwitter receiver, String message) throws TwitterException{
		return aplicacao.sendDirectMessage(receiver.getIdTwitter(), message);
	}
	
	/**
	 * Bloqueia a conta de Twitter passada para que esta n�o receba mais as atualiza��es da aplica��o
	 * @param contaTwitter a conta do twitter a ser bloqueada
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter
	 * @return o usu�rio do twitter que foi bloqueado
	 */
	public User blockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.createBlock(contaTwitter.getIdTwitter());
	}
	
	/**
	 * Desloqueia a conta de Twitter passada para que esta n�o receba mais as atualiza��es da aplica��o
	 * @param contaTwitter a conta do twitter a ser desbloqueada
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter
	 * @return o usu�rio do twitter que foi desbloqueado
	 */
	public User unblockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyBlock(contaTwitter.getIdTwitter());
	}
	
	
	/**
	 * M�todo que faz com que o twitter da aplica��o siga a conta de Twitter passada
	 * @param contaTwitter
	 * @return o usu�rio do twitter que est� agora sendo seguido
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter
	 */
	public User followUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.createFriendship(contaTwitter.getIdTwitter());
	}
	
	/**
	 * M�todo que faz com que a aplica��o para de seguir a conta de Twitter passada
	 * @param contaTwitter
	 * @return o usu�rio do twitter que recebeu o unfollow
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter
	 */
	public User unfollowUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyFriendship(contaTwitter.getIdTwitter());
	}
	
/*	
	========================================= M�todos que utilizam o twitter dos usu�rios ===========================================
 */
	
	/**
	 * Atualiza o status do usu�rio cuja conta � passada como par�metro
	 * @param contaTwitter
	 * @param status
	 * @return o Status rec�m postado
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status updateStatus(ContaTwitter contaTwitter, String status) throws TwitterException{
		return createTwitter(contaTwitter).updateStatus(status);
	}
	
	/**
	 * Atualiza o status do usu�rio cuja conta � passada como par�metro e o marca como favorito
	 * @param contaTwitter
	 * @param status
	 * @return o Status rec�m postado
	 * @throws TwitterException Quando n�o foi poss�vel conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status updateStatusAndFavorite(ContaTwitter contaTwitter, String status) throws TwitterException{
		Twitter twitter = createTwitter(contaTwitter);
		Status st = twitter.updateStatus(status);
		return twitter.createFavorite(st.getId());
	}
	
	/**
	 * A conta de Twitter marca um tweet como favorito
	 * @param contaTwitter
	 * @param status
	 * @return o status que foi marcado como favorito
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status toFavorite(ContaTwitter contaTwitter, Status status) throws TwitterException{
		return createTwitter(contaTwitter).createFavorite(status.getId());
	}
		
	/**
	 * @param contaTwitter
	 * @param status
	 * @return
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status retweet(ContaTwitter contaTwitter, Status status) throws TwitterException{
		return createTwitter(contaTwitter).retweetStatus(status.getId());
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem que faz uma "mention" direta ao receiver
	 * @param sender a conta que postar� o tweet
	 * @param receiverScreenName o screenName da conta que ser� citada na mensagem
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public Status sendMessage(ContaTwitter sender, String receiverScreenName, String message) throws TwitterException{
		String newMessage = "@" + receiverScreenName + " " + message;
		return createTwitter(sender).updateStatus(newMessage);
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem que faz uma "mention" direta ao receiver
	 * @param sender a conta que postar� o tweet
	 * @param receiver a conta que ser� citada na mensagem
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public Status sendMessage(ContaTwitter sender, ContaTwitter receiver, String message) throws TwitterException{
		String newMessage = "@" + receiver.getScreenName() + " " + message;
		return createTwitter(sender).updateStatus(newMessage);
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem para cada receiver da lista que for passada e fazendo tamb�m uma "mention" a cada um deles
	 * @param sender a conta que postar� o tweet
	 * @param receivers a lista de contas que ser�o citadas nas mensgens
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public List<Status> sendMessage(ContaTwitter sender, List<ContaTwitter> receivers, String message) throws TwitterException{
		List<Status> statusList = new ArrayList<Status>();
		Twitter twitter = createTwitter(sender);
		for (ContaTwitter receiver : receivers){
			String newMessage = "@" + receiver.getScreenName() + " " + message;
			statusList.add(twitter.updateStatus(newMessage));
		}
		return statusList;
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem para cada receiver da lista que for passada e fazendo tamb�m uma "mention" a cada um deles
	 * @param sender a conta que postar� o tweet
	 * @param receivers a lista de contas que ser�o citadas nas mensgens
	 * @param message o conte�do da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando n�o for poss�vel conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public List<Status> sendMessage(ContaTwitter sender, List<ContaTwitter> receivers, TwitterTemplate template) throws TwitterException{
		return sendMessage(sender, receivers, template.getMensagem());
	}

	/**
	 * Envia uma mensagem direta e privativa (DM) do sender para o receiver
	 * @param sender a conta que envia a DM
	 * @param receiver a conta que recebe a DM
	 * @param message o conte�do da mensagem
	 * @return a DirectMessage resultante da opera��o
	 * @throws TwitterException uando n�o for poss�vel conectar ao Twitter ou quando o receiver n�o segue o sender (caso
	 * em que o twitter n�o permite o envio de DMs) ou ainda quando a String resultante da postagem possuir mais de 140 caracteres
	 */
	public DirectMessage sendDirectMessage(ContaTwitter sender, ContaTwitter receiver, String message) throws TwitterException{
		return createTwitter(sender).sendDirectMessage(receiver.getIdTwitter(), message);
	}
		
	/**
	 * M�todo que faz com que a conta de Twitter passada passe a seguir o twitter da aplica��o 
	 * @param contaTwitter
	 * @throws IllegalStateException
	 * @throws TwitterException
	 */
	public void followApplication(ContaTwitter contaTwitter) throws TwitterException{
		createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
	}
	
	/**
	 * M�todo que faz com que a conta de Twitter passada pare de seguir o twitter da aplica��o
	 * @param contaTwitter
	 * @throws IllegalStateException
	 * @throws TwitterException
	 */
	public void unfollowApplication(ContaTwitter contaTwitter) throws TwitterException{
		createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
	}
	
//  =================== M�todos que afetam o Twitter da aplica��o e dos usu�rios ao mesmo tempo	
	
	/**
	 * Faz com que o usu�rio passado passe a seguir a aplica��o e aplica��o tamb�m passe a seguir o usu�rio
	 * @param contaTwitter
	 * @return o usu�rio do twitter que passou a ser seguido
	 * @throws IllegalStateException
	 * @throws TwitterException
	 */
	public User mutualApplicationFollow(ContaTwitter contaTwitter) throws TwitterException{
		Twitter twitter = createTwitter(contaTwitter);
		twitter.createFriendship(aplicacao.getId(), true);
		return aplicacao.createFriendship(twitter.getId(), true);
	}
	
	/**
	 * Faz com que o usu�rio passado pare de seguir a aplica��o e aplica��o tambem pare de seguir o usu�rio
	 * @param contaTwitter
	 * @return o usu�rio do twitter que recebeu o unfollow
	 * @throws IllegalStateException
	 * @throws TwitterException
	 */
	public User mutualApplicationUnfollow(ContaTwitter contaTwitter) throws TwitterException{
		Twitter twitter = createTwitter(contaTwitter);
		twitter.createFriendship(aplicacao.getId(), true);
		return aplicacao.createFriendship(twitter.getId(), true);
	}
	
//  ======================================== Getters ==============================================
	
	public static TwitterUtil getInstance() {
		return instance;
	}
	
	/**
	 * Classe f�brica para cria��o de objetos Twitter
	 * @return a inst�ncia da classe j� configurada para a aplica��o
	 */
	public TwitterFactory getFactory() {
		return factory;
	}
	
	/**
	 * @return a conta do twitter do usu�rio logado ou null caso ela n�o exista
	 */
	public ContaTwitter getContaTwitterUsuarioLogado() {
		return getContaTwitter(Authenticator.getUsuarioLogado());
	}
	
	
	/**
	 * @param usuario
	 * @return a conta do twitter do usu�rio passado como parametro ou null caso ela n�o exista 
	 */
	public ContaTwitter getContaTwitter(UsuarioLogin usuario){
		ContaTwitter conta = null;
		if (usuario.getTemContaTwitter()){
			String hql = "select o from ContaTwitter o where o.usuario = :usuario";
			conta = (ContaTwitter) EntityUtil.createQuery(hql)
					.setParameter("usuario", usuario)
					.getSingleResult();
		}
		return conta;
	}
	
	/**
	 * @param usuario
	 * @return a conta do twitter da localiza��o passada como parametro ou null caso a conta n�o exista 
	 */
	public ContaTwitter getContaTwitter(Localizacao localizacao){
		ContaTwitter conta = null;
		if (localizacao.getTemContaTwitter()){
			String hql = "select o from ContaTwitter o where o.localizacao = :localizacao";
			conta = (ContaTwitter) EntityUtil.createQuery(hql)
					.setParameter("localizacao", localizacao)
					.getSingleResult();
		}
		return conta;
	}
	
/*	
    ================================== M�todos Inicializadores e Auxiliares P�blicos ========================================
*/	
	
	public static final void restart(){
		instance = init();
	}

/*	
    =================================== M�todos Inicializadores e Auxiliares Privados ========================================
*/	
	
	private void loadApplicationTwitter() {
		Integer idUsuarioSistema = Integer.valueOf(ParametroUtil.getParametro("idUsuarioSistema"));
		String hql = "select o from ContaTwitter o where o.usuario.idPessoa = :usuario";
		ContaTwitter ct = (ContaTwitter) EntityUtil.createQuery(hql)
				.setParameter("usuario", idUsuarioSistema)
				.getSingleResult();
		aplicacao = factory.getInstance(ct.getAccessToken());
	}
	
	private Twitter createTwitter(ContaTwitter contaTwitter){
		Twitter twitter = factory.getInstance();
		twitter.setOAuthAccessToken(contaTwitter.getAccessToken());
		return twitter;
	}
	
	/**
	 * M�todo est�tico para setar as configura��es necess�rias para a Factory que instancia os objetos Twitter
	 * Utiliza os par�metros do sistema "oauthConsumerKey" e "oauthConsumerSecret" que s�o gerados quando a aplica��o
	 * � registrada em https://dev.twitter.com/apps
	 * @return uma inst�ncia do TwitterUtil
	 * */
	private static TwitterUtil init(){
		TwitterUtil result = null;
		try {
			result = new TwitterUtil();
			result.loadApplicationTwitter();
		} catch (IllegalArgumentException | NullPointerException | NoResultException e) {
		    LOG.error(".init()", e);
		} catch (Exception e) {
		    LOG.error(".init()", e);
		}
		return result;
	}
}
