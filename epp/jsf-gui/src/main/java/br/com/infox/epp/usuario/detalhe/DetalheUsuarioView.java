package br.com.infox.epp.usuario.detalhe;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.access.TermoAdesaoService;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class DetalheUsuarioView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String tab = "info";
    @Getter
    private String nome;
    @Getter
    private String termo;
    @Getter
    private String urlTermoAdesao;
    @Inject
    private TermoAdesaoService termoAdesaoService;

    @PostConstruct
    private void init() {
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        this.nome = usuario.getNomeUsuario();
        if(usuario.getPessoaFisica() != null && usuario.getPessoaFisica().getTermoAdesao() != null) {
            this.termo = usuario.getPessoaFisica().getTermoAdesao().getModeloDocumento();
            this.urlTermoAdesao = termoAdesaoService.buildUrlDownload(
                getHttpServletRequest().getContextPath(),
                null,
                usuario.getPessoaFisica().getTermoAdesao().getUuid().toString()
            );
        }
    }

    public void onClickTabAssinaturas() {
        System.out.println("asd");
    }

    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

}