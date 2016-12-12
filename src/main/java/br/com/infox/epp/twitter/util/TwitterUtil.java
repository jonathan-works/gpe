package br.com.infox.epp.twitter.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.twitter.entity.ContaTwitter;
import br.com.infox.epp.twitter.entity.TwitterTemplate;
import br.com.infox.epp.twitter.manager.ContaTwitterManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;
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

    private static final String DEFAULT_MESSAGE_FORMAT = "@{0} {1}";
    private static final String OAUTH_CONSUMER_SECRET = "oauthConsumerSecret";
    private static final String OAUTH_CONSUMER_KEY = "oauthConsumerKey";
    private static final LogProvider LOG = Logging.getLogProvider(TwitterUtil.class);
    private static TwitterUtil instance;
    private TwitterFactory factory;
    private Twitter aplicacao;

    private TwitterUtil() {
    }

    private void config() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(ParametroUtil.getParametro(OAUTH_CONSUMER_KEY));
        builder.setOAuthConsumerSecret(ParametroUtil.getParametro(OAUTH_CONSUMER_SECRET));
        factory = new TwitterFactory(builder.build());
    }

    /*
     * =================================== Métodos que utilizam com o twitter da
     * aplicação =======================================
     */
    /**
     * Atualiza o status do Twitter cadastrado para a aplicação. Todos os
     * usuários que seguem a aplicação recebem essa atualização.
     * 
     * @param news o conteúdo da atualização
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou
     *         quando a String news possui mais de 140 caracteres
     * @return o ultimo Status do twitter da aplicacao
     */
    public Status sendNews(String news) throws TwitterException {
        return aplicacao.updateStatus(news);
    }

    /**
     * Atualiza o status do Twitter cadastrado para a aplicação e também o marca
     * como favorito. Todos os usuários que seguem a aplicação recebem essa
     * atualização
     * 
     * @param news o conteúdo da atualização
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou
     *         quando a String news possui mais de 140 caracteres
     * @return o ultimo Status do twitter da aplicacao.
     */
    public Status sendNewsFavorite(String news) throws TwitterException {
        Status status = sendNews(news);
        return aplicacao.createFavorite(status.getId());
    }

    /**
     * Faz com que o Twitter da aplicação retweet o Status passado com parametro
     * 
     * @param status
     * @return o status retweetado
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou
     *         quando o twitter da aplicação não tem permissão para enxergar
     *         aquele tweet
     */
    public Status retweet(Status status) throws TwitterException {
        return aplicacao.retweetStatus(status.getId());
    }

    /**
     * Atualiza o status do twitter da aplicacao com uma mensagem que faz uma
     * "mention" direta ao receiver
     * 
     * @param receiver a conta que será citada na mensagem
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public Status sendMessage(ContaTwitter receiver, String message) throws TwitterException {
        String newMessage = MessageFormat.format(DEFAULT_MESSAGE_FORMAT, receiver.getScreenName(), message);
        return aplicacao.updateStatus(newMessage);
    }

    /**
     * Atualiza o status do twitter da aplicacao com uma mensagem para cada
     * receiver da lista que for passada e fazendo também uma "mention" a cada
     * um deles
     * 
     * @param receivers a lista de contas que serão citadas nas mensgens
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public List<Status> sendMessage(List<ContaTwitter> receivers, String message) throws TwitterException {
        List<Status> statusList = new ArrayList<Status>();
        for (ContaTwitter receiver : receivers) {
            statusList.add(sendMessage(receiver, message));
        }
        return statusList;
    }

    /**
     * Envia uma mensagem direta (DM) do twitter da aplicação para o twitter
     * passado
     * 
     * @param receiver a conta do twitter que deverá receber a DM
     * @param message o conteúdo da DM
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou
     *         quando a String message possui mais de 140 caracteres
     * @return o objeto DirectMessage
     */
    public DirectMessage sendDirectMessage(ContaTwitter receiver, String message) throws TwitterException {
        return aplicacao.sendDirectMessage(receiver.getIdTwitter(), message);
    }

    /**
     * Bloqueia a conta de Twitter passada para que esta não receba mais as
     * atualizações da aplicação
     * 
     * @param contaTwitter a conta do twitter a ser bloqueada
     * @throws TwitterException Quando não foi possível conectar ao Twitter
     * @return o usuário do twitter que foi bloqueado
     */
    public User blockUser(ContaTwitter contaTwitter) throws TwitterException {
        return aplicacao.createBlock(contaTwitter.getIdTwitter());
    }

    /**
     * Desloqueia a conta de Twitter passada para que esta não receba mais as
     * atualizações da aplicação
     * 
     * @param contaTwitter a conta do twitter a ser desbloqueada
     * @throws TwitterException Quando não foi possível conectar ao Twitter
     * @return o usuário do twitter que foi desbloqueado
     */
    public User unblockUser(ContaTwitter contaTwitter) throws TwitterException {
        return aplicacao.destroyBlock(contaTwitter.getIdTwitter());
    }

    /**
     * Método que faz com que o twitter da aplicação siga a conta de Twitter
     * passada
     * 
     * @param contaTwitter
     * @return o usuário do twitter que está agora sendo seguido
     * @throws TwitterException Quando não foi possível conectar ao Twitter
     */
    public User followUser(ContaTwitter contaTwitter) throws TwitterException {
        return aplicacao.createFriendship(contaTwitter.getIdTwitter());
    }

    /**
     * Método que faz com que a aplicação para de seguir a conta de Twitter
     * passada
     * 
     * @param contaTwitter
     * @return o usuário do twitter que recebeu o unfollow
     * @throws TwitterException Quando não foi possível conectar ao Twitter
     */
    public User unfollowUser(ContaTwitter contaTwitter) throws TwitterException {
        return aplicacao.destroyFriendship(contaTwitter.getIdTwitter());
    }

    /*
     * ========================================= Métodos que utilizam o twitter
     * dos usuários ===========================================
     */

    /**
     * Atualiza o status do usuário cuja conta é passada como parâmetro
     * 
     * @param contaTwitter
     * @param status
     * @return o Status recém postado
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou a
     *         String status possui mais de 140 caracteres
     */
    public Status updateStatus(ContaTwitter contaTwitter, String status) throws TwitterException {
        return createTwitter(contaTwitter).updateStatus(status);
    }

    /**
     * Atualiza o status do usuário cuja conta é passada como parâmetro e o
     * marca como favorito
     * 
     * @param contaTwitter
     * @param status
     * @return o Status recém postado
     * @throws TwitterException Quando não foi possível conectar ao Twitter ou a
     *         String status possui mais de 140 caracteres
     */
    public Status updateStatusAndFavorite(ContaTwitter contaTwitter,
            String status) throws TwitterException {
        Twitter twitter = createTwitter(contaTwitter);
        Status st = twitter.updateStatus(status);
        return twitter.createFavorite(st.getId());
    }

    /**
     * A conta de Twitter marca um tweet como favorito
     * 
     * @param contaTwitter
     * @param status
     * @return o status que foi marcado como favorito
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String status possui mais de 140 caracteres
     */
    public Status toFavorite(ContaTwitter contaTwitter, Status status) throws TwitterException {
        return createTwitter(contaTwitter).createFavorite(status.getId());
    }

    /**
     * @param contaTwitter
     * @param status
     * @return
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String status possui mais de 140 caracteres
     */
    public Status retweet(ContaTwitter contaTwitter, Status status) throws TwitterException {
        return createTwitter(contaTwitter).retweetStatus(status.getId());
    }

    /**
     * Atualiza o status do sender com uma mensagem que faz uma "mention" direta
     * ao receiver
     * 
     * @param sender a conta que postará o tweet
     * @param receiverScreenName o screenName da conta que será citada na
     *        mensagem
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public Status sendMessage(ContaTwitter sender, String receiverScreenName,
            String message) throws TwitterException {
        String newMessage = MessageFormat.format(DEFAULT_MESSAGE_FORMAT, receiverScreenName, message);
        return createTwitter(sender).updateStatus(newMessage);
    }

    /**
     * Atualiza o status do sender com uma mensagem que faz uma "mention" direta
     * ao receiver
     * 
     * @param sender a conta que postará o tweet
     * @param receiver a conta que será citada na mensagem
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public Status sendMessage(ContaTwitter sender, ContaTwitter receiver,
            String message) throws TwitterException {
        String newMessage = MessageFormat.format(DEFAULT_MESSAGE_FORMAT, receiver.getScreenName(), message);
        return createTwitter(sender).updateStatus(newMessage);
    }

    /**
     * Atualiza o status do sender com uma mensagem para cada receiver da lista
     * que for passada e fazendo também uma "mention" a cada um deles
     * 
     * @param sender a conta que postará o tweet
     * @param receivers a lista de contas que serão citadas nas mensgens
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public List<Status> sendMessage(ContaTwitter sender,
            List<ContaTwitter> receivers, String message) throws TwitterException {
        List<Status> statusList = new ArrayList<Status>();
        Twitter twitter = createTwitter(sender);
        for (ContaTwitter receiver : receivers) {
            String newMessage = MessageFormat.format(DEFAULT_MESSAGE_FORMAT, receiver.getScreenName(), message);
            statusList.add(twitter.updateStatus(newMessage));
        }
        return statusList;
    }

    /**
     * Atualiza o status do sender com uma mensagem para cada receiver da lista
     * que for passada e fazendo também uma "mention" a cada um deles
     * 
     * @param sender a conta que postará o tweet
     * @param receivers a lista de contas que serão citadas nas mensgens
     * @param message o conteúdo da mensagem
     * @return o status resultante da postagem
     * @throws TwitterException Quando não for possível conectar ao Twitter ou a
     *         String resultante da postagem possuir mais de 140 caracteres
     */
    public List<Status> sendMessage(ContaTwitter sender,
            List<ContaTwitter> receivers, TwitterTemplate template) throws TwitterException {
        return sendMessage(sender, receivers, template.getMensagem());
    }

    /**
     * Envia uma mensagem direta e privativa (DM) do sender para o receiver
     * 
     * @param sender a conta que envia a DM
     * @param receiver a conta que recebe a DM
     * @param message o conteúdo da mensagem
     * @return a DirectMessage resultante da operação
     * @throws TwitterException uando não for possível conectar ao Twitter ou
     *         quando o receiver não segue o sender (caso em que o twitter não
     *         permite o envio de DMs) ou ainda quando a String resultante da
     *         postagem possuir mais de 140 caracteres
     */
    public DirectMessage sendDirectMessage(ContaTwitter sender,
            ContaTwitter receiver, String message) throws TwitterException {
        return createTwitter(sender).sendDirectMessage(receiver.getIdTwitter(), message);
    }

    /**
     * Método que faz com que a conta de Twitter passada passe a seguir o
     * twitter da aplicação
     * 
     * @param contaTwitter
     * @throws IllegalStateException
     * @throws TwitterException
     */
    public void followApplication(ContaTwitter contaTwitter) throws TwitterException {
        createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
    }

    /**
     * Método que faz com que a conta de Twitter passada pare de seguir o
     * twitter da aplicação
     * 
     * @param contaTwitter
     * @throws IllegalStateException
     * @throws TwitterException
     */
    public void unfollowApplication(ContaTwitter contaTwitter) throws TwitterException {
        createTwitter(contaTwitter).createFriendship(aplicacao.getId(), true);
    }

    // =================== Métodos que afetam o Twitter da aplicação e dos
    // usuários ao mesmo tempo

    /**
     * Faz com que o usuário passado passe a seguir a aplicação e aplicação
     * também passe a seguir o usuário
     * 
     * @param contaTwitter
     * @return o usuário do twitter que passou a ser seguido
     * @throws IllegalStateException
     * @throws TwitterException
     */
    public User mutualApplicationFollow(ContaTwitter contaTwitter) throws TwitterException {
        Twitter twitter = createTwitter(contaTwitter);
        twitter.createFriendship(aplicacao.getId(), true);
        return aplicacao.createFriendship(twitter.getId(), true);
    }

    /**
     * Faz com que o usuário passado pare de seguir a aplicação e aplicação
     * tambem pare de seguir o usuário
     * 
     * @param contaTwitter
     * @return o usuário do twitter que recebeu o unfollow
     * @throws IllegalStateException
     * @throws TwitterException
     */
    public User mutualApplicationUnfollow(ContaTwitter contaTwitter) throws TwitterException {
        Twitter twitter = createTwitter(contaTwitter);
        twitter.createFriendship(aplicacao.getId(), true);
        return aplicacao.createFriendship(twitter.getId(), true);
    }

    // ======================================== Getters
    // ==============================================

    public static TwitterUtil getInstance() {
        if (instance == null) {
            instance = init();
        }
        return instance;
    }

    /**
     * Classe fábrica para criação de objetos Twitter
     * 
     * @return a instância da classe já configurada para a aplicação
     */
    public TwitterFactory getFactory() {
        return factory;
    }

    /**
     * @param usuario
     * @return a conta do twitter do usuário passado como parametro ou null caso
     *         ela não exista
     */
    public ContaTwitter getContaTwitter(UsuarioLogin usuario) {
        if (usuario.getTemContaTwitter()) {
            return contaTwitterManager().getContaTwitterByUsuario(usuario);
        }
        return null;
    }

    /**
     * @param usuario
     * @return a conta do twitter da localização passada como parametro ou null
     *         caso a conta não exista
     */
    public ContaTwitter getContaTwitter(Localizacao localizacao) {
        if (localizacao.getTemContaTwitter()) {
            return contaTwitterManager().getContaTwitterByLocalizacao(localizacao);
        }
        return null;
    }

    /*
     * ================================== Métodos Inicializadores e Auxiliares
     * Públicos ========================================
     */

    public static void restart() {
        instance.config();
    }

    /*
     * =================================== Métodos Inicializadores e Auxiliares
     * Privados ========================================
     */

    private void loadApplicationTwitter() {
        Integer idUsuarioSistema = Integer.valueOf(ParametroUtil.getParametro("idUsuarioSistema"));
        ContaTwitter ct = contaTwitterManager().getContaTwitterByIdUsuario(idUsuarioSistema);
        aplicacao = factory.getInstance(ct.getAccessToken());
    }

    private Twitter createTwitter(ContaTwitter contaTwitter) {
        Twitter twitter = factory.getInstance();
        twitter.setOAuthAccessToken(contaTwitter.getAccessToken());
        return twitter;
    }

    /**
     * Método estático para setar as configurações necessárias para a Factory
     * que instancia os objetos Twitter Utiliza os parâmetros do sistema
     * "oauthConsumerKey" e "oauthConsumerSecret" que são gerados quando a
     * aplicação é registrada em https://dev.twitter.com/apps
     * 
     * @return uma instância do TwitterUtil
     * */
    private static TwitterUtil init() {
        TwitterUtil result = null;
        try {
            result = new TwitterUtil();
            result.config();
            result.loadApplicationTwitter();
        } catch (IllegalArgumentException | NullPointerException | NoResultException e) {
            LOG.error(".init()", e);
        } catch (Exception e) {
            LOG.error(".init()", e);
        }
        return result;
    }

    private static ContaTwitterManager contaTwitterManager() {
        return ComponentUtil.getComponent(ContaTwitterManager.NAME);
    }
}
