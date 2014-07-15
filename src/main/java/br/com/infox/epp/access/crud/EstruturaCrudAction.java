package br.com.infox.epp.access.crud;

import javax.enterprise.context.ConversationScoped;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.component.tree.LocalizacaoSemEstruturaTreeHandler;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.list.LocalizacaoComEstruturaList;
import br.com.infox.epp.access.manager.EstruturaManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;

@Name(EstruturaCrudAction.NAME)
@ConversationScoped
public class EstruturaCrudAction extends AbstractCrudAction<Estrutura, EstruturaManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(EstruturaCrudAction.class);
    
    @In private LocalizacaoComEstruturaList localizacaoComEstruturaList;
    @In private LocalizacaoManager localizacaoManager;
    @In private ActionMessagesService actionMessagesService;
    @In private LocalizacaoSemEstruturaTreeHandler localizacaoSemEstruturaTree;
    private Localizacao localizacaoFilho;
    
    @Override
    public void newInstance() {
        super.newInstance();
        localizacaoSemEstruturaTree.clearTree();
    }
    
    @Override
    public void setInstance(Estrutura instance) {
        super.setInstance(instance);
        if (isManaged()) {
            localizacaoComEstruturaList.getEntity().setEstruturaPai(getInstance());
        }
    }
    
    public void addLocalizacaoFilho() {
        try {
            localizacaoManager.atualizarEstruturaPai(getInstance(), localizacaoFilho);
            localizacaoSemEstruturaTree.clearTree();
            FacesMessages.instance().add("#{messages['estrutura.localizacaoFilhoAdicionada']}");
        } catch (DAOException e) {
            LOG.error("Erro ao adicionar localização filho à estrutura", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void removeLocalizacaoFilho(Localizacao localizacao) {
        try {
            localizacaoManager.removerEstruturaPai(localizacao);
            localizacaoSemEstruturaTree.clearTree();
            FacesMessages.instance().add("#{messages['estrutura.localizacaoFilhoRemovida']}");
        } catch (DAOException e) {
            LOG.error("Erro ao remover localização filho da estrutura", e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public Localizacao getLocalizacaoFilho() {
        return localizacaoFilho;
    }
    
    public void setLocalizacaoFilho(Localizacao localizacaoFilho) {
        this.localizacaoFilho = localizacaoFilho;
    }
    
    public String formatCaminhoCompleto(Localizacao localizacao) {
        return localizacaoManager.formatCaminhoCompleto(localizacao);
    }
}
