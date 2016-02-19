package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.core.token.AccessToken;
import br.com.infox.core.token.AccessTokenManager;
import br.com.infox.core.token.TokenRequester;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Named
@ViewScoped
public class BpmnView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private AccessTokenManager accessTokenManager;
	@Inject
	private FluxoManager fluxoManager;
	
	private String restApiUrl;
	private Fluxo fluxo;
	private AccessToken accessToken;
	
	@PostConstruct
	private void init() {
		accessToken = new AccessToken();
		accessToken.setTokenRequester(TokenRequester.BPMN_MODELER);
		accessToken.setToken(UUID.randomUUID());
		accessTokenManager.persist(accessToken);
	}
	
	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public String getRestApiUrl() {
		if (restApiUrl == null) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			StringBuilder url = new StringBuilder(externalContext.getRequestScheme());
			url.append("://");
			url.append(externalContext.getRequestServerName());
			url.append(":");
			url.append(externalContext.getRequestServerPort());
			url.append(request.getServletContext().getContextPath());
			url.append("/rest");
			restApiUrl = url.toString();
		}
		return restApiUrl;
	}
	
	public String getToken() {
		return accessToken.getToken().toString();
	}
	
	@PreDestroy
	private void destroy() {
		accessTokenManager.remove(accessToken);
	}
	
	public void refresh() {
		if (fluxo != null) {
			fluxo = fluxoManager.find(fluxo.getIdFluxo());
			fluxoManager.refresh(fluxo);
			ProcessBuilder.instance().load(fluxo);
		}
	}
}