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

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.assinador.rest.api.TokenAssinaturaBaseResource;
import br.com.infox.assinador.rest.api.TokenAssinaturaResource;
import br.com.infox.certificado.service.CertificadoDigitalJNLPServlet;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.cdi.ViewScoped;
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
	
	public String criarGrupoAssinatura(String textoAssinavel) {
		return criarGrupoAssinatura(new AssinavelGenericoProvider(textoAssinavel));		
	}
	
	public String criarGrupoAssinatura(List<String> textoAssinavelList) {
		return criarGrupoAssinatura(new AssinavelGenericoProvider(textoAssinavelList));		
	}
	
	public String criarGrupoAssinaturaWithDocumentoBin(DocumentoBin documentoBin) {
		return criarGrupoAssinatura(new AssinavelDocumentoBinProvider(documentoBin));		
	}
	
	public String criarGrupoAssinaturaWithDocumentoBin(List<DocumentoBin> documentoBinList) {
		return criarGrupoAssinatura(new AssinavelDocumentoBinProvider(documentoBinList));		
	}
	
	public String criarGrupoAssinatura(AssinavelProvider assinavelProvider) {
		token = assinadorService.criarListaAssinaveis(assinavelProvider);
		return token;
	}

	public String getParametrosServletJNLP(String token) {
		String codigoPerfil = getCodigoPerfil();
		String codigoLocalizacao = getCodigoLocalizacao();
		
		return String.format("%s=%s&%s=%s&%s=%s", PARAMETRO_TOKEN, token, PARAMETRO_CODIGO_PERFIL, codigoPerfil, PARAMETRO_CODIGO_LOCALIZACAO, codigoLocalizacao);
	}
	
	public void apagarGrupo() {
		groupService.apagarGrupo(token);
	}
	
	public String getURITokenResource(String token, String nomeMetodo) {
		Method metodo;
		Method metodoResource;
		try {
			metodoResource = TokenAssinaturaBaseResource.class.getDeclaredMethod("getTokenAssinaturaResource", String.class);
			metodo = TokenAssinaturaResource.class.getDeclaredMethod(nomeMetodo);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		URI uri = UriBuilder.fromResource(TokenAssinaturaBaseResource.class).path(metodoResource).path(metodo).build(token);
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
