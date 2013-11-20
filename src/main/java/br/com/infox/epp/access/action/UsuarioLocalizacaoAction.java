package br.com.infox.epp.access.action;


import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.home.UsuarioHome;
import br.com.infox.epp.access.list.UsuarioLocalizacaoList;

@Name(UsuarioLocalizacaoAction.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLocalizacaoAction {
	public static final String NAME = "usuarioLocalizacaoAction";
	private static final Log LOG = Logging.getLog(UsuarioLocalizacaoAction.class);
	
	private UsuarioLocalizacao instance;
	
	@In
	private GenericManager genericManager;
	
	private UsuarioLogin usuarioGerenciado;
	
//	@In
//	private UsuarioHome usuarioHome;
	
	@In(value = UsuarioLocalizacaoList.NAME, create = true)
	private UsuarioLocalizacaoList usuarioLocalizacaoList;
	
	public UsuarioLocalizacao getInstance() {
		return instance;
	}
	
	public void setInstance(UsuarioLocalizacao instance) {
		this.instance = instance;
	}
	
	@SuppressWarnings("rawtypes")
    private void limparArvores() {
        AbstractTreeHandler tree = (LocalizacaoEstruturaTreeHandler) Component.getInstance(LocalizacaoEstruturaTreeHandler.class);
        tree.clearTree();
        tree = (PapelTreeHandler) Component.getInstance(PapelTreeHandler.class);
        tree.clearTree();
	}
	
	public void newInstance() {
		this.instance = new UsuarioLocalizacao();
		this.instance.setResponsavelLocalizacao(false);
//		if (usuarioHome.isManaged()) {
			this.instance.setUsuario(usuarioGerenciado);
			usuarioLocalizacaoList.getEntity().setUsuario(this.instance.getUsuario());
//		}
		limparArvores();
	}
	
	public void persist() {
        try {
			genericManager.persist(instance);
			newInstance();
		} catch (DAOException e) {
			LOG.error(".persist()", e);
			FacesMessages.instance().clear();
			FacesMessages.instance().add(e.getLocalizedMessage());
		}
	}
	
	public void remove(UsuarioLocalizacao usuarioLocalizacao) {
		setInstance(usuarioLocalizacao);
		try {
			genericManager.remove(instance);
			newInstance();
		} catch (DAOException e) {
			LOG.error(".remove()", e);
			FacesMessages.instance().clear();
			FacesMessages.instance().add(e.getLocalizedMessage());
		}
	}

    public UsuarioLogin getUsuarioGerenciado() {
        return usuarioGerenciado;
    }

    public void setUsuarioGerenciado(UsuarioLogin usuarioGerenciado) {
        this.usuarioGerenciado = usuarioGerenciado;
    }
}
