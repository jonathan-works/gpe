package br.com.infox.epp.processo.legado;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.seam.exception.BusinessException;

@Named
@RequestScoped
public class ProcessoLegadoViewController {

    @Inject
    private ProcessoLegadoService processoLegadoService;

    public void redirecionarUsuario() throws URISyntaxException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        if (!"GET".equals(request.getMethod())) {
            throw new BusinessException("Requisição inválida");
        }

        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        String url = processoLegadoService.gerarUrlAcessoSistemaProcessosLegados(Authenticator.getUsuarioLogado());
        try {
            response.sendRedirect(url);
            response.flushBuffer();
        } catch (IOException e) {
            throw new BusinessException("Ocorreu um erro no redirecionamento", e);
        }
    }
}
