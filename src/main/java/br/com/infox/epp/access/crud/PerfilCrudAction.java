package br.com.infox.epp.access.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoEstruturaTree;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.access.manager.PerfilManager;

@Name(PerfilCrudAction.NAME)
public class PerfilCrudAction extends AbstractCrudAction<Perfil, PerfilManager> {

    public static final String NAME = "perfilCrudAction";
    private static final long serialVersionUID = 1L;
    
    @Observer(LocalizacaoEstruturaTree.SELECTED_LOCALIZACAO_ESTRUTURA)
    public void setLocalizacoes(Localizacao localizacao, Localizacao paiDaEstrutura) {
        getInstance().setLocalizacao(localizacao);
        getInstance().setPaiDaEstrutura(paiDaEstrutura);
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        limparTrees();
    }

    @SuppressWarnings(UNCHECKED)
    private void limparTrees() {
        ((TreeHandler<Papel>) Component.getInstance(PapelTreeHandler.NAME)).clearTree();
        ((TreeHandler<Localizacao>) Component.getInstance(LocalizacaoEstruturaTree.NAME)).clearTree();
    }

}
