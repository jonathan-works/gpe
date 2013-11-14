package br.com.infox.epp.access.action;


import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTreeHandler;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.home.UsuarioHome;
import br.com.infox.epp.access.list.UsuarioLocalizacaoList;
import br.com.infox.ibpm.component.tree.PapelTreeHandler;

@Name(UsuarioLocalizacaoAction.NAME)
@Scope(ScopeType.PAGE)
public class UsuarioLocalizacaoAction {
	public static final String NAME = "usuarioLocalizacaoAction";
	
	private UsuarioLocalizacao instance;
	
	@In
	private GenericManager genericManager;
	
	@In
	private UsuarioHome usuarioHome;
	
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
		if (usuarioHome.isManaged()) {
			this.instance.setUsuario(usuarioHome.getInstance());
			usuarioLocalizacaoList.getEntity().setUsuario(this.instance.getUsuario());
		}
		limparArvores();
	}
	
	public void persist() {
        genericManager.persist(instance);
        newInstance();
	}
	
	public void remove(UsuarioLocalizacao usuarioLocalizacao) {
		setInstance(usuarioLocalizacao);
		genericManager.remove(instance);
		newInstance();
	}
}
