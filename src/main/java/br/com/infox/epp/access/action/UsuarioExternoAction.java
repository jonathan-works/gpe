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
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.fluxo.bean.ItemBean;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.action.IniciarProcessoAction;
import br.com.infox.ibpm.jbpm.TaskInstanceHome;
import br.com.itx.util.EntityUtil;

@Name(UsuarioExternoAction.NAME)
@Scope(ScopeType.EVENT)
public class UsuarioExternoAction {
	public static final String NAME = "usuarioExternoAction";
	private static final LogProvider LOG = Logging.getLogProvider(UsuarioExternoAction.class);
	
	@In(create = true)
	private IniciarProcessoAction iniciarProcessoAction;
	
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	
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
	        NaturezaCategoriaFluxo naturezaCategoriaFluxo = naturezaCategoriaFluxoManager.getByRelationship(natureza, categoria, fluxo);
	        iniciarProcessoAction.onSelectNatCatFluxo(naturezaCategoriaFluxo);
	        iniciarProcessoAction.onSelectItem(new ItemBean(item));
		} else {
		    FacesMessages.instance().add(Messages.instance().get("acessoExterno.LoggedIn"));
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
			LOG.error("Erro ao redirecionar para a URL " + urlRetorno.toString(), e);
		}
		Identity.instance().logout();
	}
	
	public void setIdCategoria(Integer idCategoria) {
		this.categoria = EntityUtil.find(Categoria.class, idCategoria);
	}
	public Integer getIdCategoria() {
	    return this.categoria.getIdCategoria();
	}

	public void setIdFluxo(Integer idFluxo) {
		this.fluxo = EntityUtil.find(Fluxo.class, idFluxo);
	}
	public Integer getIdFluxo() {
	    return this.fluxo.getIdFluxo();
	}
	
	public void setIdNatureza(Integer idNatureza) {
		this.natureza = EntityUtil.find(Natureza.class, idNatureza);
	}
	public Integer getIdNatureza() {
        return this.natureza.getIdNatureza();
    }
	
	public void setIdItem(Integer idItem) {
		this.item = EntityUtil.find(Item.class, idItem);
	}
	public Integer getIdItem() {
        return this.item.getIdItem();
    }
	
	public void setUrlRetorno(String urlRetorno) {
		try {
			this.urlRetorno = new URL(urlRetorno);
		} catch (MalformedURLException e) {
			Redirect.instance().setViewId("/AcessoExterno/externo.seam");
			Redirect.instance().execute();
		}
	}
	public String getUrlRetorno() {
	    return this.urlRetorno.getRef();
	}
}
