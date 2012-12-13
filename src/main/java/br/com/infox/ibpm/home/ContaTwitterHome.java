package br.com.infox.ibpm.home;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import br.com.infox.epa.type.TipoTwitterEnum;
import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.util.TwitterUtil;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

@Name(ContaTwitterHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ContaTwitterHome extends AbstractHome<ContaTwitter>{

	private static final long serialVersionUID = 203098369650080L;
	public static final String NAME = "contaTwitterHome";
	
	private Twitter twitter = TwitterUtil.getInstance().getFactory().getInstance();
	private RequestToken requestToken;
	private AccessToken accessToken;
	private Localizacao localizacao;
	private Usuario usuario;
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
		switch (this.instance.getTipoTwitter()) {
		case U:
		case S:
			this.instance.setUsuario(usuario);
			break;
		case L:
			break;
		}
		return true;
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		if ("persisted".equals(ret) || "updated".equals(ret))	{
			switch (this.instance.getTipoTwitter()) {
			case U:
			case S:
				if (!usuario.getTemContaTwitter())	{
					usuario.setTemContaTwitter(true);
					EntityManager em = getEntityManager(); 
					em.merge(usuario);
					em.flush();
				}
				break;
			case L:
				break;
			}
		}
		return ret;
	}
	
	public void getAutorizacao(String tipoAutorizacao){
		if (requesting) {
			accessToken = null;
			try {
				accessToken = getAccessToken(pin);
				switch (Enum.valueOf(TipoTwitterEnum.class, tipoAutorizacao)) {
				case L:
					localizacao = LocalizacaoHome.instance().getInstance();
					break;
				case U:
					usuario = Authenticator.getUsuarioLogado();
					break;
				case S:
					usuario = getEntityManager().find(Usuario.class, Integer.parseInt(ParametroHome.getParametro("idUsuarioSistema")));
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
			//------------
			Usuario usr = instance.getUsuario(); 
			usr.setTemContaTwitter(false);
			em.merge(usr);
			//-------------
			em.flush();
		}
		newInstance();
		return "removed";
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
			usuario = getEntityManager().find(Usuario.class, Integer.parseInt(ParametroHome.getParametro("idUsuarioSistema")));
			break;
		default:
			break;
		}
		
		String hql = "select o from ContaTwitter o " +
						"where o.usuario = :usuario " +
						"or o.localizacao = :localizacao";
		ContaTwitter conta = (ContaTwitter) EntityUtil.createQuery(hql)
				.setParameter("usuario", usuario)
				.setParameter("localizacao", localizacao)
				.getSingleResult();
		
		if (conta != null) {	
			setId(conta.getIdTwitter());
		}
	}
	
}