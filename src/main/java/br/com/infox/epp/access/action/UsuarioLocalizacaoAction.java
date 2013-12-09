package br.com.infox.epp.access.action;


import static br.com.infox.core.constants.WarningConstants.RAWTYPES;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.DAOActionInterface;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.itx.util.ComponentUtil;

@Name(UsuarioLocalizacaoAction.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLocalizacaoAction {
	public static final String NAME = "usuarioLocalizacaoAction";
	private static final Log LOG = Logging.getLog(UsuarioLocalizacaoAction.class);
	
	private UsuarioLocalizacao instance;
	
	@In
	private GenericManager genericManager;
	
	private UsuarioLogin usuarioGerenciado;
	
	public UsuarioLocalizacao getInstance() {
		return instance;
	}
	
	public void setInstance(UsuarioLocalizacao instance) {
		this.instance = instance;
	}
	
	@SuppressWarnings(RAWTYPES)
    private void limparArvores() {
        AbstractTreeHandler tree = ComponentUtil.getComponent(LocalizacaoEstruturaTreeHandler.class);
        tree.clearTree();
        tree = ComponentUtil.getComponent(PapelTreeHandler.class);
        tree.clearTree();
	}
	
	public void newInstance() {
		this.instance = new UsuarioLocalizacao();
		this.instance.setResponsavelLocalizacao(false);
		this.instance.setUsuario(usuarioGerenciado);
		limparArvores();
	}
	
	public void persist() {
        try {
			genericManager.persist(instance);
			newInstance();
		} catch (DAOException e) {
			LOG.error(".persist()", e);
			FacesMessages facesMessages = FacesMessages.instance();
            facesMessages.clear();
			facesMessages.add(e.getLocalizedMessage());
		}
	}
	
	public void remove(UsuarioLocalizacao usuarioLocalizacao) {
		setInstance(usuarioLocalizacao);
		try {
			genericManager.remove(instance);
			newInstance();
		} catch (DAOException e) {
			LOG.error(".remove()", e);
			FacesMessages facesMessages = FacesMessages.instance();
            facesMessages.clear();
			facesMessages.add(e.getLocalizedMessage());
		}
	}

    public UsuarioLogin getUsuarioGerenciado() {
        return usuarioGerenciado;
    }

    public void setUsuarioGerenciado(UsuarioLogin usuarioGerenciado) {
        this.usuarioGerenciado = usuarioGerenciado;
        this.instance.setUsuario(usuarioGerenciado);
    }
}
