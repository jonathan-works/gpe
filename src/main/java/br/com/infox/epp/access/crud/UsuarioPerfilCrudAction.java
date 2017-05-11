package br.com.infox.epp.access.crud;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;

@Named
@ViewScoped
public class UsuarioPerfilCrudAction extends AbstractCrudAction<UsuarioPerfil, UsuarioPerfilManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioPerfilManager usuarioPerfilManager;

    private UsuarioLogin usuarioLogin;

    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }
    
    @Override
    protected boolean isInstanceValid() {
        getInstance().setUsuarioLogin(usuarioLogin);
        return super.isInstanceValid();
    }
    
    @Override
    protected void afterSave(String ret) {
        newInstance();
        super.afterSave(ret);
    }
    
    public List<PerfilTemplate> getPerfisPermitidos() {
        if (getInstance().getLocalizacao() == null) {
            return Collections.emptyList();
        }
        return getManager().getPerfisPermitidos(getInstance().getLocalizacao());
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        clearTrees();
    }
    
    private void clearTrees() {
        ((TreeHandler<Localizacao>) BeanManager.INSTANCE.getReference(LocalizacaoTreeHandler.class)).clearTree();
    }
    
    @Override
	public String inactive(UsuarioPerfil t) {
		String result = super.inactive(t);
		if (UPDATED.equals(result)){
			final StatusMessages messages = getMessagesHandler();
			messages.clear();
			messages.add(infoxMessages.get("entity_deleted"));
		}
		return result; 
	}

    @Override
    protected UsuarioPerfilManager getManager() {
        return usuarioPerfilManager;
    }
}
