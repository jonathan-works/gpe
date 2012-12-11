package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import br.com.infox.epa.type.TipoTwitterEnum;
import br.com.infox.ibpm.entity.ContaTwitter;
import br.com.infox.ibpm.entity.PessoaFisica;
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
	private String pin;
	
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
	
	public void getAutorizacao(String pin){
		try {
			accessToken = null;
			//while (accessToken == null){
				try{
					if (pin.length() > 0)
						accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					else 
						accessToken = twitter.getOAuthAccessToken();
				} catch (TwitterException te) {
			        if(401 == te.getStatusCode()){
			        	System.out.println("Unable to get the access token.");
				    }else{
				    	te.printStackTrace();
				    }
				}
			//}
			newInstance();
			Usuario usuario = Authenticator.getUsuarioLogado();
			this.instance.setUsuario(usuario);
			this.instance.setTipoTwitter(TipoTwitterEnum.U);
			this.instance.setAccessToken(accessToken);
			
			if (!usuario.getTemContaTwitter())	{
				usuario.setTemContaTwitter(true);
				getEntityManager().merge(usuario);
				getEntityManager().flush();
				this.persist();
			} else {
				this.update();
			}
			
		} catch (Exception te){
			te.printStackTrace();
		}
	}
	
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public void save(String pin){
		System.out.println(pin);
	}
	
	public void carrega(){
		getAutorizacao(pin);
	}
	
	public void setContaTwitter(){
		try {
			TwitterUtil.getInstance().sendNews("news");
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		String hql = "select o from ContaTwitter o where o.usuario = :usuario";
		ContaTwitter conta = (ContaTwitter) EntityUtil.createQuery(hql)
				.setParameter("usuario", Authenticator.getUsuarioLogado())
				.getSingleResult();
		setInstance(conta);
	}
	
}
