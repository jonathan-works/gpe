package br.com.infox.epp.access.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoFullTreeHandler;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Perfil;
import br.com.infox.epp.access.manager.PerfilManager;

@Name(PerfilCrudAction.NAME)
public class PerfilCrudAction extends AbstractCrudAction<Perfil, PerfilManager> {

    public static final String NAME = "perfilCrudAction";
    private static final long serialVersionUID = 1L;

    @Observer(LocalizacaoFullTreeHandler.SELECTED_LOCALIZACAO_ESTRUTURA)
    public void setLocalizacoes(Localizacao localizacao,
            Localizacao paiDaEstrutura) {
        getInstance().setLocalizacao(localizacao);
        getInstance().setPaiDaEstrutura(paiDaEstrutura);
    }

    @Override
    public void newInstance() {
        super.newInstance();
        limparTrees();
    }

    @Override
    protected boolean isInstanceValid() {
        if(getManager().existePerfil(getInstance().getIdPerfil(), getInstance().getLocalizacao(), getInstance().getPapel(), getInstance().getPaiDaEstrutura())) {
            FacesMessages.instance().clear();
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "#{messages['constraintViolation.uniqueViolation']}");
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings(UNCHECKED)
    private void limparTrees() {
        ((TreeHandler<Papel>) Component.getInstance(PapelTreeHandler.NAME)).clearTree();
        ((TreeHandler<Localizacao>) Component.getInstance(LocalizacaoFullTreeHandler.NAME)).clearTree();
    }
    
    public void clear() {
        limparTrees();
    }

}
