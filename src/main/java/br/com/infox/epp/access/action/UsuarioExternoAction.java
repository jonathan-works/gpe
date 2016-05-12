package br.com.infox.epp.access.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.security.Identity;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(UsuarioExternoAction.NAME)
@Scope(ScopeType.EVENT)
public class UsuarioExternoAction {
    public static final String NAME = "usuarioExternoAction";
    private static final LogProvider LOG = Logging.getLogProvider(UsuarioExternoAction.class);
    
    @In
    private InfoxMessages infoxMessages;
    @In
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    @In
    private GenericManager genericManager;
    @In(create = true)
    private TaskInstanceHome taskInstanceHome;

    private Categoria categoria;
    private Fluxo fluxo;
    private Natureza natureza;
    private Item item;
    private URL urlRetorno;

    public void handleUsuarioExterno() {
        final boolean wasLoggedIn = Identity.instance().isLoggedIn();
        Identity.instance().logout();
        if (!wasLoggedIn) {
            Authenticator.loginUsuarioExterno();
            taskInstanceHome.setUrlRetornoAcessoExterno(urlRetorno);
        } else {
            FacesMessages.instance().add(infoxMessages.get("acessoExterno.LoggedIn"));
        }
    }

    public void endAcaoUsuarioExterno() {
        try {
            if (urlRetorno != null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(urlRetorno.toString());
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("about:blank");
            }
        } catch (IOException e) {
            if (urlRetorno != null) {
                LOG.error("Erro ao redirecionar para a URL "
                        + urlRetorno.toString(), e);
            } else {
                LOG.error(".endAcaoUsuarioExterno()", e);
            }
        }
        Identity.instance().logout();
    }

    public void setIdCategoria(Integer idCategoria) {
        this.categoria = genericManager.find(Categoria.class, idCategoria);
    }

    public Integer getIdCategoria() {
        return this.categoria.getIdCategoria();
    }

    public void setIdFluxo(Integer idFluxo) {
        this.fluxo = genericManager.find(Fluxo.class, idFluxo);
    }

    public Integer getIdFluxo() {
        return this.fluxo.getIdFluxo();
    }

    public void setIdNatureza(Integer idNatureza) {
        this.natureza = genericManager.find(Natureza.class, idNatureza);
    }

    public Integer getIdNatureza() {
        return this.natureza.getIdNatureza();
    }

    public void setIdItem(Integer idItem) {
        this.item = genericManager.find(Item.class, idItem);
    }

    public Integer getIdItem() {
        return this.item.getIdItem();
    }

    public void setUrlRetorno(String urlRetorno) {
        try {
            this.urlRetorno = new URL(urlRetorno);
        } catch (MalformedURLException e) {
            LOG.warn(".setUrlRetorno(urlRetorno)", e);
            Redirect.instance().setViewId("/AcessoExterno/externo.seam");
            Redirect.instance().execute();
        }
    }

    public String getUrlRetorno() {
        return this.urlRetorno.getRef();
    }
}
