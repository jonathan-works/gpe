package br.com.infox.epp.access;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class TermoAdesaoViewController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private TermoAdesaoService termoAdesaoService;
    @Inject private AuthenticatorService authenticatorService;
    @Inject private InfoxMessages infoxMessages;

    private PessoaFisica pessoaFisica;
    private String urlTermoAdesao;
    private DocumentoBin termoAdesao;

    private String digestDocumento;
    private String token;

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
                } catch (BusinessException e){
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
                }
            }
        }

        this.termoAdesao = retrieveTermoAdesao();
        this.setDigestDocumento(this.termoAdesao.getMd5Documento());

        this.urlTermoAdesao = termoAdesaoService.buildUrlDownload(getContextPath(), jwt,
                this.termoAdesao.getUuid().toString());
    }

    @ExceptionHandled
    public boolean isTermoAdesaoAssinado() {
        return pessoaFisica != null && termoAdesaoService.isTermoAdesaoAssinado(pessoaFisica.getCpf());
    }

    public String getUrlTermoAdesao() {
        return urlTermoAdesao;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDigestDocumento() {
        return digestDocumento;
    }

    public void setDigestDocumento(String digestDocumento) {
        this.digestDocumento = digestDocumento;
    }

    @ExceptionHandled
    public void assinarTermoAdesao() {
        this.termoAdesaoService.assinarTermoAdesao(token, pessoaFisica, pessoaFisica.getTermoAdesao().getUuid());
        if (viaLoginUsuario) {
            authenticatorService.loginWithoutPassword(pessoaFisica.getUsuarioLogin());
            Redirect redirect = Redirect.instance();
            redirect.getParameters().clear();
            redirect.setViewId(Authenticator.instance().getCaminhoPainel());
            redirect.execute();
        }
        FacesMessages.instance().add(Severity.INFO, infoxMessages.get("termoAdesao.sign.success"));
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

}
