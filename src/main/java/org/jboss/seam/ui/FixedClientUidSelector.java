package org.jboss.seam.ui;

import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.RandomStringUtils;


/**
 * Classe para ajustar o erro que ocorre ao submeter um formulário:
 * org.jboss.seam.ui.UnauthorizedCommandException: viewId: ... - Form signature invalid
 * 
 * http://seamframework.org/Community/SeamTokenTagProblemCSRF
 * 
 */
@Name("org.jboss.seam.ui.clientUidSelector")
@Install(precedence = Install.DEPLOYMENT)
public class FixedClientUidSelector extends ClientUidSelector {

	private static final long serialVersionUID = -4923235748771706010L;
	private String clientUid;

	@Create
	public void onCreate() {
		setCookiePath(FacesContext.getCurrentInstance().getExternalContext()
				.getRequestContextPath());
		setCookieMaxAge(-1);
		setCookieEnabled(true);
		clientUid = getCookieValue();
	}

	public void seed() {
		if (!isSet()) {
			clientUid = RandomStringUtils.random(50, true, true); // Fixed
			setCookieValueIfEnabled(clientUid);
		}
	}

	public boolean isSet() {
		return clientUid != null;
	}

	public String getClientUid() {
		return clientUid;
	}

	@Override
	protected String getCookieName() {
		return "javax.faces.ClientToken";
	}

}
