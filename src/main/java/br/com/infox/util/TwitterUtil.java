package br.com.infox.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.EntityUtil;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtil {
	
	private static TwitterUtil instance = init();
	private TwitterFactory factory;
	private Twitter aplicacao;
		
	private TwitterUtil() throws IllegalArgumentException {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(ParametroUtil.getParametro("oauthConsumerKey"));
		builder.setOAuthConsumerSecret(ParametroUtil.getParametro("oauthConsumerSecret"));
		factory = new TwitterFactory(builder.build());
	}
	
//  ============================= Métodos que utilizam com o twitter da aplicação =============================
	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplicação. Todos os usuários que seguem a aplicação recebem essa atualização.
	 * @param news o conteúdo da atualização
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao
	 */
	public Status sendNews(String news) throws TwitterException{
		return aplicacao.updateStatus(news);
	}
	
	/**
	 * Atualiza o status do Twitter cadastrado para a aplicação e também o marca como favorito. Todos os usuários que seguem a aplicação recebem essa atualização
	 * @param news o conteúdo da atualização
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou quando a String news possui mais de 140 caracteres
	 * @return o ultimo Status do twitter da aplicacao.
	 */
	public Status sendNewsFavorite(String news) throws TwitterException{
		Status status = sendNews(news);
		return aplicacao.createFavorite(status.getId());
	}
	
	/**
	 * Faz com que o Twitter da aplicação retweet o Status passado com parametro
	 * @param status
	 * @return o status retweetado
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou quando o twitter da aplicação não tem permissão para enxergar aquele tweet
	 */
	public Status retweet(Status status) throws TwitterException{
		return aplicacao.retweetStatus(status.getId());
	}
	
	/**
	 * Envia uma mensagem direta (DM) do twitter da aplicação para o twitter passado 
	 * @param receiver a conta do twitter que deverá receber a DM
	 * @param message o conteúdo da DM
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou quando a String message possui mais de 140 caracteres
	 * @return o objeto DirectMessage
	 */
	public DirectMessage sendDirectMessage(ContaTwitter receiver, String message) throws TwitterException{
		return aplicacao.sendDirectMessage(receiver.getIdTwitter(), message);
	}
	
	/**
	 * Bloqueia a conta de Twitter passada para que esta não receba mais as atualizações da aplicação
	 * @param contaTwitter a conta do twitter a ser bloqueada
	 * @throws TwitterException Quando não foi possível conectar ao Twitter
	 * @return o usuário do twitter que foi bloqueado
	 */
	public User blockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.createBlock(contaTwitter.getIdTwitter());
	}
	
	/**
	 * Desloqueia a conta de Twitter passada para que esta não receba mais as atualizações da aplicação
	 * @param contaTwitter a conta do twitter a ser desbloqueada
	 * @throws TwitterException Quando não foi possível conectar ao Twitter
	 * @return o usuário do twitter que foi desbloqueado
	 */
	public User unblockUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyBlock(contaTwitter.getIdTwitter());
	}
	
	
	/**
	 * Método que faz com que o twitter da aplicação siga a conta de Twitter passada
	 * @param contaTwitter
	 * @return o usuário do twitter que está agora sendo seguido
	 * @throws TwitterException Quando não foi possível conectar ao Twitter
	 */
	public User followUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.createFriendship(contaTwitter.getIdTwitter());
	}
	
	/**
	 * Método que faz com que a aplicação para de seguir a conta de Twitter passada
	 * @param contaTwitter
	 * @return o usuário do twitter que recebeu o unfollow
	 * @throws TwitterException Quando não foi possível conectar ao Twitter
	 */
	public User unfollowUser(ContaTwitter contaTwitter) throws TwitterException{
		return aplicacao.destroyFriendship(contaTwitter.getIdTwitter());
	}
	
	
//  ========== Métodos que utilizam com o twitter dos usuários ==========
	
	/**
	 * Atualiza o status do usuário cuja conta é passada como parâmetro
	 * @param contaTwitter
	 * @param status
	 * @return o Status recém postado
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status updateStatus(ContaTwitter contaTwitter, String status) throws TwitterException{
		return createTwitter(contaTwitter).updateStatus(status);
	}
	
	/**
	 * Atualiza o status do usuário cuja conta é passada como parâmetro e o marca como favorito
	 * @param contaTwitter
	 * @param status
	 * @return o Status recém postado
	 * @throws TwitterException Quando não foi possível conectar ao Twitter ou a String status possui mais de 140 caracteres 
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
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status toFavorite(ContaTwitter contaTwitter, Status status) throws TwitterException{
		return createTwitter(contaTwitter).createFavorite(status.getId());
	}
		
	/**
	 * @param contaTwitter
	 * @param status
	 * @return
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String status possui mais de 140 caracteres 
	 */
	public Status retweet(ContaTwitter contaTwitter, Status status) throws TwitterException{
		return createTwitter(contaTwitter).retweetStatus(status.getId());
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem que faz uma "mention" direta ao receiver
	 * @param sender a conta que postará o tweet
	 * @param receiverScreenName o screenName da conta que será citada na mensagem
	 * @param message o conteúdo da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public Status sendMessage(ContaTwitter sender, String receiverScreenName, String message) throws TwitterException{
		String newMessage = "@" + receiverScreenName + " " + message;
		return createTwitter(sender).updateStatus(newMessage);
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem que faz uma "mention" direta ao receiver
	 * @param sender a conta que postará o tweet
	 * @param receiver a conta que será citada na mensagem
	 * @param message o conteúdo da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public Status sendMessage(ContaTwitter sender, ContaTwitter receiver, String message) throws TwitterException{
		String newMessage = "@" + receiver.getScreenName() + " " + message;
		return createTwitter(sender).updateStatus(newMessage);
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem para cada receiver da lista que for passada e fazendo também uma "mention" a cada um deles
	 * @param sender a conta que postará o tweet
	 * @param receivers a lista com os screenNames das contas que serão citadas nas mensgens
	 * @param message o conteúdo da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
	 */
	public List<Status> sendMessages(ContaTwitter sender, List<String> receiversScreenNames, String message) throws TwitterException{
		List<Status> statusList = new ArrayList<Status>();
		Twitter twitter = createTwitter(sender);
		for (String screenName : receiversScreenNames){
			String newMessage = "@" + screenName + " " + message;
			statusList.add(twitter.updateStatus(newMessage));
		}
		return statusList;
	}
	
	/**
	 * Atualiza o status do sender com uma mensagem para cada receiver da lista que for passada e fazendo também uma "mention" a cada um deles
	 * @param sender a conta que postará o tweet
	 * @param receivers a lista de contas que serão citadas nas mensgens
	 * @param message o conteúdo da mensagem
	 * @return o status resultante da postagem
	 * @throws TwitterException Quando não for possível conectar ao Twitter ou a String resultante da postagem possuir mais de 140 caracteres
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
	
	public DirectMessage sendDirectMessage(ContaTwitter sender, ContaTwitter receiver, String message) throws TwitterException{
		return createTwitter(sender).sendDirectMessage(receiver.getIdTwitter(), message);
	}
	
	public void followApplication(ContaTwitter contaTwitter) throws IllegalStateException, TwitterException{
		createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
	}
	
//  =================== Métodos que afetam o Twitter da aplicação e dos usuários ao mesmo tempo	
	
	public void mutualFollow(ContaTwitter contaTwitter) throws IllegalStateException, TwitterException{
		Twitter twitter = createTwitter(contaTwitter);
		twitter.createFriendship(aplicacao.getId(), true);
		aplicacao.createFriendship(twitter.getId(), true);
	}
	
//  ======================= Métodos Inicializadores e Auxiliares Públicos ===================
	
	public final static void restart(){
		instance = init();
	}
	
//  ======================= Métodos Inicializadores e Auxiliares Privados ===================
	
	//TODO fazer com que esse método inicialize o twitter da aplicação (esperando definir melhor como o twitter da aplicação será salvo) 
	private final void loadApplicationTwitter() {
		Integer idUsuarioSistema = Integer.valueOf(ParametroUtil.getParametro("idUsuarioSistema"));
		String hql = "select o from ContaTwitter o right join o.usuario u where u.idUsuario = :usuario";
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
	
	//TODO
	/**
	 * Método estático para setar as configurações necessárias para a Factory que instancia os objetos Twitter
	 * Utiliza os parâmetros do sistema "oauthConsumerKey" e "oauthConsumerSecret" que são gerados quando a aplicação
	 * é registrada em https://dev.twitter.com/apps
	 * @return 
	 * */
	private final static TwitterUtil init(){
		TwitterUtil result = null;
		try {
			result = new TwitterUtil();
			result.loadApplicationTwitter();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NullPointerException e)	{
			e.printStackTrace();
		}
		return result;
	}
	
	
	
//  ============== Métodos para testes locais - Remover antes de colocar em Produção ==========================	
	
	public TwitterFactory getFactory() {
		return factory;
	}

	public static TwitterUtil getInstance() {
		return instance;
	}

	public Twitter getAplicacao() {
		if (aplicacao == null){
			loadApplicationTwitter();
		}
		return aplicacao;
	}

	//TODO remover esse método - utilizado somente para testes locais
	private AccessToken loadAccessToken(){
	    String token = "980311075-QPU70Kmu7DJE44eBV0CzsyJje2rQel7y4q35EBAF";
	    String tokenSecret = "qehADZbdRPYrvdpHjuh5xDtNdhKDNPo3v5lGZEC5ng";
	    return new AccessToken(token, tokenSecret);
	}
	
	//TODO remover esse método - utilizado somente para testes locais
	private AccessToken loadAccessToken2(){
	    String token = "811368666-5eQBz3BeMlo6J7dpaqorJu6egiqyPb4VeecLrNdV";
	    String tokenSecret = "G50BaEgzWHIX9fsLzfEY2zhc75F0hVicoiJB7YbyYPA";
	    return new AccessToken(token, tokenSecret);
	}
	
	//TODO remover esse método - utilizado somente para testes locais
	public static void main(String[] args) throws TwitterException, IOException {
//		ContaTwitter ct = new ContaTwitter();
//		ct.setAccessToken(loadAccessToken2());
//		sendMessage(ct, "InfoxPP", "seu processo foi finalizado");
//		ct.setScreenName("infoxPP");
//		updateStatus(ct, "Testando o e-PP");
//		followApplication(ct);
//		sendNewsFavorite("new teste3");
	}
}
