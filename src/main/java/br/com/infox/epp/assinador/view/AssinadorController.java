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

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.assinador.rest.api.TokenAssinaturaBaseResource;
import br.com.infox.assinador.rest.api.TokenAssinaturaResource;
import br.com.infox.certificado.service.CertificadoDigitalJNLPServlet;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.assinador.AssinadorGroupService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.certificado.enums.CertificateSignatureGroupStatus;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.path.PathResolver;

@Named
@ViewScoped
public class AssinadorController implements Serializable, AssinaturaCallback {

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

        return String.format("%s=%s&%s=%s&%s=%s", PARAMETRO_TOKEN, token, PARAMETRO_CODIGO_PERFIL, codigoPerfil,
                PARAMETRO_CODIGO_LOCALIZACAO, codigoLocalizacao);
    }

    public void apagarGrupo() {
        groupService.apagarGrupo(token);
    }

    public String getURITokenResource(String token, String nomeMetodo) {
        Method metodo;
        Method metodoResource;
        try {
            metodoResource = TokenAssinaturaBaseResource.class.getDeclaredMethod("getTokenAssinaturaResource",
                    String.class);
            metodo = TokenAssinaturaResource.class.getDeclaredMethod(nomeMetodo);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        URI uri = UriBuilder.fromResource(TokenAssinaturaBaseResource.class).path(metodoResource).path(metodo)
                .build(token);
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

    public void assinaturasRecebidas(String token, AssinaturaCallback assinaturaCallback) {
        if (assinaturaCallback == null)
            assinaturaCallback = this;
        if (isFinalizado(token)) {
            List<DadosAssinatura> dadosAssinatura = groupService.getDadosAssinatura(token);
            if (isSucesso(token)) {
                assinaturaCallback.onSuccess(dadosAssinatura);
            } else {
                assinaturaCallback.onFail(groupService.getStatus(token), dadosAssinatura);
            }
        }
    }
    
    public AssinavelProvider criarAssinavelProvider(String textoAssinavel) {
        return new AssinavelGenericoProvider(textoAssinavel);
    }

    public AssinavelProvider criarAssinavelProvider(List<String> textoAssinavelList) {
        return new AssinavelGenericoProvider(textoAssinavelList);
    }

    public AssinavelProvider criarAssinavelProviderBin(DocumentoBin documentoBin) {
        return new AssinavelDocumentoBinProvider(documentoBin);
    }

    public AssinavelProvider criarAssinavelProviderBin(List<DocumentoBin> documentoBinList) {
        return new AssinavelDocumentoBinProvider(documentoBinList);
    }

    @Override
    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
        try {
            assinadorService.assinar(dadosAssinatura, Authenticator.getUsuarioPerfilAtual());
        } catch (AssinaturaException e) {
            FacesMessages.instance().add(e.getMessage());
        }
    }

    @Override
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinaturas) {
        CertificateSignatureGroupStatus groupStatus = CertificateSignatureGroupStatus.U;
        if (StatusToken.ERRO.equals(statusToken)) {
            groupStatus = CertificateSignatureGroupStatus.E;
        } else if (StatusToken.EXPIRADO.equals(statusToken)) {
            groupStatus = CertificateSignatureGroupStatus.X;
        } else {
            groupStatus = CertificateSignatureGroupStatus.U;
        }
        FacesMessages.instance().add(InfoxMessages.getInstance().get(groupStatus));
    }

}
