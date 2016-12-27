package br.com.infox.ibpm.exec;

import java.io.Serializable;
import java.util.List;
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

@Named
@ViewScoped
public class BpmExecutionInfoView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Fluxo> fluxos;
	private Fluxo fluxo;
	@Inject
	private AccessTokenManager accessTokenManager;
	@Inject
	private BpmExecutionInfoService bpmExecutionInfoService;
	
	private String restApiUrl;
	private AccessToken accessToken;
	
	@PostConstruct
	private void init() {
		accessToken = new AccessToken();
		accessToken.setTokenRequester(TokenRequester.BPM_EXECUTION_INFO);
		accessToken.setToken(UUID.randomUUID());
		accessTokenManager.persist(accessToken);
		fluxos = bpmExecutionInfoService.getFluxosValidos();
	}
	
	public Boolean getShowGraph(){
		return fluxo != null 
				&& fluxo.getDefinicaoProcesso().getSvg() != null && !fluxo.getDefinicaoProcesso().getSvg().trim().isEmpty();
	}
	
	public String getToken() {
		return accessToken.getToken().toString();
	}
	
	public List<Fluxo> getFluxos() {
		return fluxos;
	}
	public void setFluxos(List<Fluxo> fluxos) {
		this.fluxos = fluxos;
	}
	
	public void setFluxo(Fluxo fluxo){
		this.fluxo = fluxo;
	}
	
	public Fluxo getFluxo(){
		return fluxo;
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
	
	@PreDestroy
	private void destroy() {
		accessTokenManager.remove(accessToken);
	}
}
