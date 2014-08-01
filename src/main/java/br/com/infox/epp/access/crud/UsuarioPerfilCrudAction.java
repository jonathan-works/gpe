package br.com.infox.epp.access.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import edu.emory.mathcs.backport.java.util.Collections;

@Name(UsuarioPerfilCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class UsuarioPerfilCrudAction extends AbstractCrudAction<UsuarioPerfil, UsuarioPerfilManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "usuarioPerfilCrudAction";
    
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
    
    @SuppressWarnings(UNCHECKED)
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
    
    @SuppressWarnings(UNCHECKED)
    private void clearTrees() {
        ((TreeHandler<Localizacao>) Component.getInstance(LocalizacaoTreeHandler.NAME)).clearTree();
    }

}
