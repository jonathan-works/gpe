package br.com.infox.epp.assinador.view;

import static br.com.infox.certificado.service.CertificadoDigitalJNLPServlet.PARAMETRO_CODIGO_LOCALIZACAO;
import static br.com.infox.certificado.service.CertificadoDigitalJNLPServlet.PARAMETRO_CODIGO_PERFIL;
import static br.com.infox.certificado.service.CertificadoDigitalJNLPServlet.PARAMETRO_TOKEN;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.certificado.service.CertificadoDigitalJNLPServlet;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorGroupService.StatusToken;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.api.TokenAssinaturaResource;
import br.com.infox.epp.assinador.api.TokenAssinaturaRest;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.path.PathResolver;

@Named
@ViewScoped
public class AssinadorController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private AssinadorService assinadorService;
	@Inject
	private AssinadorGroupService groupService;
	@Inject
	private PathResolver pathResolver;
	
	private String token;


	public String criarListaDocumentos(List<DocumentoBin> listaDocumentos) {
		if(token == null) {
			token = assinadorService.criarListaDocumentos(listaDocumentos);
		}
		return token;
	}

	public String getParametrosServletJNLP(String token) {
		String codigoPerfil = getCodigoPerfil();
		String codigoLocalizacao = getCodigoLocalizacao();
		
		return String.format("%s=%s&%s=%s&%s=%s", PARAMETRO_TOKEN, token, PARAMETRO_CODIGO_PERFIL, codigoPerfil, PARAMETRO_CODIGO_LOCALIZACAO, codigoLocalizacao);
	}
	
	public void apagarToken(String token) {
		groupService.apagarGrupo(token);
	}
	
	public void assinaturasRecebidas() {
		try {
			assinadorService.assinar(token, Authenticator.getUsuarioPerfilAtual());
		} catch (AssinaturaException e) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Erro ao assinar: " + e.getMessage());
		}
		FacesMessages.instance().add(StatusMessage.Severity.INFO, "Assinatura completada com sucesso");
	}
	
	
	public String getURITokenResource(String token, String nomeMetodo) {
		Method metodo;
		Method metodoResource;
		try {
			metodoResource = TokenAssinaturaRest.class.getDeclaredMethod("getTokenAssinaturaResource", String.class);
			metodo = TokenAssinaturaResource.class.getDeclaredMethod(nomeMetodo);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		URI uri = UriBuilder.fromResource(TokenAssinaturaRest.class).path(metodoResource).path(metodo).build(token);
		String retorno = pathResolver.getRestBaseUrl() + "/" + uri.toString(); 
		return retorno;		
	}
	
	public String getTokenStatusURL(String token) {
		return getURITokenResource(token, "getStatus");
	}
	
	public String getJNLPUrl() {
		return pathResolver.getUrlProject() + CertificadoDigitalJNLPServlet.SERVLET_PATH;		
	}

	public String getCodigoPerfil() {
		return Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getCodigo();
	}

	public String getCodigoLocalizacao() {
		return Authenticator.getLocalizacaoAtual().getCodigo();
	}

	public boolean isFinalizado(String token) {
		StatusToken status = groupService.getStatus(token);
		return status != StatusToken.AGUARDANDO_ASSINATURA;
	}

	public boolean isSucesso(String token) {
		StatusToken status = groupService.getStatus(token);
		return status == StatusToken.SUCESSO;
	}

}
