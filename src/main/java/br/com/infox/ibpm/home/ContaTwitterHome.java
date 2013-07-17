package br.com.infox.ibpm.home;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import br.com.infox.epp.type.TipoTwitterEnum;
import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.util.TwitterUtil;
import br.com.itx.component.AbstractHome;

@Name(ContaTwitterHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ContaTwitterHome extends AbstractHome<ContaTwitter>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "contaTwitterHome";
	
	private Twitter twitter = TwitterUtil.getInstance().getFactory().getInstance();
	private RequestToken requestToken;
	private AccessToken accessToken;
	private Localizacao localizacao;
	private UsuarioLogin usuario;
	private String pin;
	private boolean requesting = false;
	
	public boolean usuarioLogadoHasTwitter(){
		return Authenticator.getUsuarioLogado().getTemContaTwitter();
	}
	
	/**
	 * Inicializa o Token de Requisição de Autorização
	 * e gera a URL correspondente
	 * */
	public String getAuthorizationURL(){
		try {
			requestToken = twitter.getOAuthRequestToken();
			return requestToken.getAuthorizationURL();
		} catch (TwitterException te){
			te.printStackTrace();
			return "";
		} catch (IllegalStateException ise){
			ise.printStackTrace();
			return "";
		}
	}
	
	private AccessToken getAccessToken(String pin) throws TwitterException	{
		AccessToken tok;
		if (pin.length() > 0)
			tok = twitter.getOAuthAccessToken(requestToken, pin);
		else 
			tok = twitter.getOAuthAccessToken();
		return tok;
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		this.instance.setAccessToken(accessToken);
		this.instance.setUsuario(usuario);
		this.instance.setLocalizacao(localizacao);
		return true;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		if ("persisted".equals(ret) || "updated".equals(ret))	{
			EntityManager em = getEntityManager();
			switch (this.instance.getTipoTwitter()) {
			case U:
				if (!usuario.getTemContaTwitter())	{
					usuario.setTemContaTwitter(true);
					em.merge(usuario);
				}
				break;
			case S:
				if (!usuario.getTemContaTwitter())	{
					usuario.setTemContaTwitter(true);
					em.merge(usuario);
					TwitterUtil.restart();
				}
				break;
			case L:
				localizacao.setTemContaTwitter(true);
				em.merge(localizacao);
				break;
			}
			em.flush();
		}
		return ret;
	}
	
	public void getAutorizacao(String tipoAutorizacao){
		if (requesting) {
			accessToken = null;
			try {
				accessToken = getAccessToken(pin);
				TipoTwitterEnum tipo = Enum.valueOf(TipoTwitterEnum.class, tipoAutorizacao);
				this.instance.setTipoTwitter(tipo);
				switch (tipo) {
				case L:
					localizacao = LocalizacaoHome.instance().getInstance();
					break;
				case U:
					usuario = Authenticator.getUsuarioLogado();
					break;
				case S:
					usuario = getEntityManager().find(UsuarioLogin.class, Integer.parseInt(ParametroHome.getParametro("idUsuarioSistema")));
					break;
				}
				persist();
			} catch (TwitterException e) {
				if(401 == e.getStatusCode()){
					FacesMessages.instance().add(Severity.ERROR, "Unable to get the access token.");
			    }else{
			    	e.printStackTrace();
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
		if ("removed".equals(super.remove()))	{
			EntityManager em = getEntityManager();
			switch (instance.getTipoTwitter()) {
			case U:
			case S:
				UsuarioLogin usr = instance.getUsuario(); 
				usr.setTemContaTwitter(false);
				em.merge(usr);	
				break;
			case L:
				Localizacao loc = instance.getLocalizacao();
				loc.setTemContaTwitter(false);
				em.merge(loc);
				break;
			}
			em.flush();
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
	
	public void setContaTwitter(String tipoDeConta){
		switch (Enum.valueOf(TipoTwitterEnum.class, tipoDeConta)) {
		case U:
			usuario = Authenticator.getUsuarioLogado();
			break;
		case S:
			usuario = getEntityManager().find(UsuarioLogin.class, Integer.parseInt(ParametroHome.getParametro("idUsuarioSistema")));
			break;
		default:
			break;
		}
		ContaTwitter conta = null;
		try {
			conta = TwitterUtil.getInstance().getContaTwitter(usuario);
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		if (conta != null) {
			setId(conta.getIdTwitter());
		}
	}
	
}