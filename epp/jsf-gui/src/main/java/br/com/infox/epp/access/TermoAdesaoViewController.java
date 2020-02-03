package br.com.infox.epp.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.core.exception.SystemException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelDocumentoBinProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class TermoAdesaoViewController implements Serializable, AssinaturaCallback {

    private static final long serialVersionUID = 1L;

    @Inject private TermoAdesaoService termoAdesaoService;
    @Inject private AuthenticatorService authenticatorService;
    @Inject private InfoxMessages infoxMessages;

    private PessoaFisica pessoaFisica;
    private String urlTermoAdesao;
    private DocumentoBin termoAdesao;

    private String digestDocumento;

    private String jwt;
    private boolean viaLoginUsuario = false;

    @PostConstruct
    protected void init() {
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        if (usuarioLogado != null) {
            Authenticator.instance().unAuthenticate();

            pessoaFisica = usuarioLogado.getPessoaFisica();
            viaLoginUsuario = true;
        } else if ((this.jwt = getRequestParameter("jwt")) == null) {
            try {
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();
                response.sendError(404);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ExceptionHandled
    public void onRenderView() {
        if (!viaLoginUsuario) {
            if (jwt != null) {
                try {
                    this.pessoaFisica = termoAdesaoService.retrievePessoaFisica(jwt.trim());
                } catch (SystemException|BusinessException e){
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
                } catch (EJBException e){
                    if (e.getCause() instanceof SystemException || e.getCause() instanceof BusinessException){
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), null));
                    }
                }
            }
        }
        if (getTermoAdesao() == null) {
            this.setTermoAdesao(retrieveTermoAdesao());
            this.setDigestDocumento(this.getTermoAdesao().getMd5Documento());
        }
        this.urlTermoAdesao = termoAdesaoService.buildUrlDownload(getContextPath(), jwt,
                this.getTermoAdesao().getUuid().toString());
    }

    @ExceptionHandled
    public boolean isTermoAdesaoAssinado() {
        return pessoaFisica != null && termoAdesaoService.isTermoAdesaoAssinado(pessoaFisica.getCpf());
    }

    public String getUrlTermoAdesao() {
        return urlTermoAdesao;
    }

    public DocumentoBin getTermoAdesao() {
        return termoAdesao;
    }

    public void setTermoAdesao(DocumentoBin termoAdesao) {
        this.termoAdesao = termoAdesao;
    }

    public String getDigestDocumento() {
        return digestDocumento;
    }

    public void setDigestDocumento(String digestDocumento) {
        this.digestDocumento = digestDocumento;
    }
    @Override
    @ExceptionHandled
    public void onSuccess(List<DadosAssinatura> dadosAssinatura) {
        if (!CollectionUtil.isEmpty(dadosAssinatura)){
            this.termoAdesaoService.assinarTermoAdesao(dadosAssinatura.get(0), pessoaFisica);
            if (viaLoginUsuario) {
                authenticatorService.loginWithoutPassword(pessoaFisica.getUsuarioLogin());
                Redirect redirect = Redirect.instance();
                redirect.getParameters().clear();
                redirect.setViewId(Authenticator.instance().getCaminhoPainel());
                redirect.execute();
            }
            FacesMessages.instance().add(Severity.INFO, infoxMessages.get("termoAdesao.sign.success"));
        } else {
            FacesMessages.instance().add(Severity.INFO, infoxMessages.get("termoAdesao.sign.fail"));
        }
    }
    @Override
    @ExceptionHandled
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
        FacesMessages.instance().add(Severity.INFO, infoxMessages.get("termoAdesao.sign.fail"));
    }

    private String getRequestParameter(String name) {
        return getHttpServletRequest().getParameter(name);
    }

    private DocumentoBin retrieveTermoAdesao() {
        if (!isTermoAdesaoAssinado()) {
            return termoAdesaoService.createTermoAdesaoFor(pessoaFisica);
        } else {
            return pessoaFisica.getTermoAdesao();
        }
    }

    private String getContextPath() {
        return getHttpServletRequest().getContextPath();
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    public AssinavelProvider getAssinavelProvider() {
        return new AssinavelDocumentoBinProvider(
            new AssinavelDocumentoBinProvider.DocumentoComRegraAssinatura(null, getTermoAdesao())
        );
    }

}
