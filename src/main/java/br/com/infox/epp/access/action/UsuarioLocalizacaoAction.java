package br.com.infox.epp.access.action;


import static br.com.infox.core.constants.WarningConstants.RAWTYPES;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLocalizacaoManager;

@Name(UsuarioLocalizacaoAction.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLocalizacaoAction {
	public static final String NAME = "usuarioLocalizacaoAction";
	private static final Log LOG = Logging.getLog(UsuarioLocalizacaoAction.class);
	
	private UsuarioLocalizacao instance;
	
	@In
	private UsuarioLocalizacaoManager usuarioLocalizacaoManager;
	
	private UsuarioLogin usuarioGerenciado;
	
	public UsuarioLocalizacao getInstance() {
		return instance;
	}
	
	public void setInstance(final UsuarioLocalizacao instance) {
		this.instance = instance;
	}
	
	@SuppressWarnings(RAWTYPES)
    private void limparArvores() {
        AbstractTreeHandler tree = (AbstractTreeHandler)Component.getInstance(LocalizacaoEstruturaTreeHandler.NAME);
        tree.clearTree();
        tree = (AbstractTreeHandler)Component.getInstance(PapelTreeHandler.NAME);
        tree.clearTree();
	}
	
	public void newInstance() {
		this.instance = new UsuarioLocalizacao();
		this.instance.setResponsavelLocalizacao(Boolean.FALSE);
		this.instance.setUsuario(usuarioGerenciado);
		limparArvores();
	}
	
	public void persist() {
        try {
        	usuarioLocalizacaoManager.persist(instance);
			newInstance();
		} catch (DAOException e) {
            processDAOException(e, ".persist()");
		}
	}
	
	public void remove(final UsuarioLocalizacao usuarioLocalizacao) {
		setInstance(usuarioLocalizacao);
		try {
			usuarioLocalizacaoManager.remove(instance);
			newInstance();
		} catch (DAOException e) {
            processDAOException(e, ".remove()");
		}
	}

    private void processDAOException(final DAOException e, final String message) {
        LOG.error(message, e);
        FacesMessages facesMessages = FacesMessages.instance();
        facesMessages.clear();
        if (e.getLocalizedMessage() != null) {
        	facesMessages.add(e.getLocalizedMessage());
        } else {
        	facesMessages.add(e.getMessage());
        }
    }

    public UsuarioLogin getUsuarioGerenciado() {
        return usuarioGerenciado;
    }

    public void setUsuarioGerenciado(final UsuarioLogin usuarioGerenciado) {
        this.usuarioGerenciado = usuarioGerenciado;
        this.instance.setUsuario(usuarioGerenciado);
    }
}
