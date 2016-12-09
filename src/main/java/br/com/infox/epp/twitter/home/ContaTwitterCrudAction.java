package br.com.infox.epp.twitter.home;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.twitter.entity.ContaTwitter;
import br.com.infox.epp.twitter.manager.ContaTwitterManager;
import br.com.infox.epp.twitter.type.TipoTwitterEnum;
import br.com.infox.epp.twitter.util.TwitterUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Name(ContaTwitterCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ContaTwitterCrudAction extends AbstractCrudAction<ContaTwitter, ContaTwitterManager> {

    private static final String ASSOCIATION_TO_USER = " ao usuário ";
    private static final String ASSOCIATION_NOT_POSSIBLE = "Não foi possível associar o twitter ";

    private static final long serialVersionUID = 1L;
    private static final int UNAUTHORIZED = 401;
    public static final String NAME = "contaTwitterCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(ContaTwitterCrudAction.class);

    private Twitter twitter = TwitterUtil.getInstance().getFactory().getInstance();
    private RequestToken requestToken;
    private AccessToken accessToken;
    private Localizacao localizacao;
    private UsuarioLogin usuario;
    private String pin;
    private boolean requesting = false;

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private LocalizacaoManager localizacaoManager;
    @In
    private InfoxMessages infoxMessages;

    public boolean usuarioLogadoHasTwitter() {
        return Authenticator.getUsuarioLogado().getTemContaTwitter();
    }

    /**
     * Inicializa o Token de Requisição de Autorização e gera a URL
     * correspondente
     * */
    public String getAuthorizationURL() {
        try {
            requestToken = twitter.getOAuthRequestToken();
            return requestToken.getAuthorizationURL();
        } catch (TwitterException | IllegalStateException e) {
            LOG.error(".getAuthorizationURL()", e);
            return "";
        }
    }

    private AccessToken getAccessToken(String pin) throws TwitterException {
        AccessToken tok;
        if (pin.length() > 0) {
            tok = twitter.getOAuthAccessToken(requestToken, pin);
        } else {
            tok = twitter.getOAuthAccessToken();
        }
        return tok;
    }

    @Override
    protected boolean isInstanceValid() {
        this.getInstance().setAccessToken(accessToken);
        this.getInstance().setUsuario(usuario);
        this.getInstance().setLocalizacao(localizacao);
        return super.isInstanceValid();
    }

    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        if ("persisted".equals(ret) || "updated".equals(ret)) {
            switch (this.getInstance().getTipoTwitter()) {
                case U:
                    if (!usuario.getTemContaTwitter()) {
                        usuario.setTemContaTwitter(true);
                        try {
                            usuarioLoginManager.update(usuario);
                        } catch (DAOException e) {
                            LOG.error(ASSOCIATION_NOT_POSSIBLE
                                    + getInstance().getScreenName()
                                    + ASSOCIATION_TO_USER + usuario, e);
                        }
                    }
                break;
                case S:
                    if (!usuario.getTemContaTwitter()) {
                        usuario.setTemContaTwitter(true);
                        try {
                            usuarioLoginManager.update(usuario);
                        } catch (DAOException e) {
                            LOG.error(ASSOCIATION_NOT_POSSIBLE
                                    + getInstance().getScreenName()
                                    + ASSOCIATION_TO_USER + usuario, e);
                        }
                        TwitterUtil.restart();
                    }
                break;
                case L:
                    localizacao.setTemContaTwitter(true);
                    try {
                        localizacaoManager.update(localizacao);
                    } catch (DAOException e) {
                        LOG.error(ASSOCIATION_NOT_POSSIBLE
                                + getInstance().getScreenName()
                                + " à localização " + localizacao, e);
                    }
                break;
            }
        }
    }

    public void getAutorizacao(String tipoAutorizacao) {
        if (requesting) {
            accessToken = null;
            try {
                accessToken = getAccessToken(pin);
                TipoTwitterEnum tipo = Enum.valueOf(TipoTwitterEnum.class, tipoAutorizacao);
                this.getInstance().setTipoTwitter(tipo);
                switch (tipo) {
                    case L:
                    // TODO implement this
                    break;
                    case U:
                        usuario = Authenticator.getUsuarioLogado();
                    break;
                    case S:
                        usuario = usuarioLoginManager.find(Integer.parseInt(ParametroUtil.getParametro("idUsuarioSistema")));
                    break;
                }
                save();
            } catch (TwitterException e) {
                if (UNAUTHORIZED == e.getStatusCode()) {
                    FacesMessages.instance().add(Severity.ERROR, infoxMessages.get("contaTwitter.error.tokenNotFound"));
                } else {
                    LOG.error(".getAutorizacao()", e);
                }
            }
        } else {
            requesting = true;
        }
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String remove() {
        if ("removed".equals(super.remove())) {
            switch (getInstance().getTipoTwitter()) {
                case U:
                case S:
                    UsuarioLogin usr = getInstance().getUsuario();
                    usr.setTemContaTwitter(false);
                    try {
                        usuarioLoginManager.update(usr);
                    } catch (DAOException e) {
                        LOG.error("Não foi possível remover o twitter do usuário "
                                + usr, e);
                    }
                break;
                case L:
                    Localizacao loc = getInstance().getLocalizacao();
                    loc.setTemContaTwitter(false);
                    try {
                        localizacaoManager.update(loc);
                    } catch (DAOException e) {
                        LOG.error("Não foi possível remover o twitter da localização"
                                + loc, e);
                    }
                break;
            }
        }
        newInstance();
        return "removed";
    }

    public void setRequesting(boolean requesting) {
        this.requesting = requesting;
    }

    public boolean isRequesting() {
        return requesting;
    }

    public void setContaTwitter(String tipoDeConta) {
        switch (Enum.valueOf(TipoTwitterEnum.class, tipoDeConta)) {
            case U:
                usuario = Authenticator.getUsuarioLogado();
            break;
            case S:
                usuario = usuarioLoginManager.find(Integer.parseInt(ParametroUtil.getParametro("idUsuarioSistema")));
            break;
            default:
            break;
        }
        ContaTwitter conta = null;
        try {
            conta = TwitterUtil.getInstance().getContaTwitter(usuario);
        } catch (NoResultException e) {
            LOG.error(".setContaTwitter()", e);
        }
        if (conta != null) {
            setId(conta.getIdTwitter());
        }
    }

}
