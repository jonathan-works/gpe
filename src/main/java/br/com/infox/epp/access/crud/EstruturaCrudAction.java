package br.com.infox.epp.access.crud;

import javax.enterprise.context.ConversationScoped;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.component.tree.EstruturaLocalizacaoTreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacoesDaEstruturaTreeHandler;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.list.LocalizacaoComEstruturaList;
import br.com.infox.epp.access.manager.EstruturaManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.fluxo.manager.RaiaPerfilManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(EstruturaCrudAction.NAME)
@ConversationScoped
@AutoCreate
public class EstruturaCrudAction extends AbstractCrudAction<Estrutura, EstruturaManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "estruturaCrudAction";
    private static final LogProvider LOG = Logging.getLogProvider(EstruturaCrudAction.class);
    
    @In private LocalizacaoComEstruturaList localizacaoComEstruturaList;
    @In private LocalizacaoManager localizacaoManager;
    @In private ActionMessagesService actionMessagesService;
    @In private LocalizacoesDaEstruturaTreeHandler localizacoesDaEstruturaTree;
    @In private EstruturaLocalizacaoTreeHandler estruturaLocalizacaoTree;
    @In private RaiaPerfilManager raiaPerfilManager;
    private Localizacao localizacaoFilho;
    
    @Override
    public void newInstance() {
        setId(null);
        super.newInstance();
        novaLocalizacao();
    }
    
    @Override
    public void setInstance(Estrutura instance) {
        super.setInstance(instance);
        if (isManaged()) {
            localizacaoComEstruturaList.getEntity().setEstruturaPai(getInstance());
            localizacoesDaEstruturaTree.setEstruturaPai(getInstance());
            estruturaLocalizacaoTree.setEstrutura(getInstance());
        }
    }
    
    public Localizacao getLocalizacaoFilho() {
        return localizacaoFilho;
    }
    
    public void setLocalizacaoFilho(Localizacao localizacaoFilho) {
        localizacoesDaEstruturaTree.clearTree();
        this.localizacaoFilho = localizacaoFilho;
    }
    
    public String formatCaminhoCompleto(Localizacao localizacao) {
        return localizacaoManager.formatCaminhoCompleto(localizacao);
    }

    public void adicionarLocalizacao() {
        localizacaoFilho.setEstruturaPai(getInstance());
        try {
            localizacaoManager.persist(localizacaoFilho);
            novaLocalizacao();
            FacesMessages.instance().add("#{infoxMessages['estrutura.localizacaoFilhoAdicionada']}");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            localizacaoFilho.setIdLocalizacao(null);
            LOG.error(e);
        }
    }
    
    public void inativarLocalizacao(Localizacao localizacao) {
        try {
            localizacaoManager.inativar(localizacao);
            novaLocalizacao();
            FacesMessages.instance().add("#{infoxMessages['estrutura.localizacaoFilhoRemovida']}");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error(e);
        }
    }
    
    public void atualizarLocalizacao() {
        try {
            localizacaoManager.update(localizacaoFilho);
            FacesMessages.instance().add("#{infoxMessages['estrutura.localizacaoFilhoAtualizada']}");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error(e);
        }
    }
    
    public void novaLocalizacao() {
        localizacaoFilho = new Localizacao();
        localizacaoFilho.setAtivo(true);
        localizacoesDaEstruturaTree.clearTree();
        estruturaLocalizacaoTree.clearTree();
    }
    
    @Override
    public String inactive(Estrutura t) {
        try {
            for (Localizacao localizacao : t.getLocalizacoes()) {
                localizacaoManager.inativar(localizacao);
            }
            return super.inactive(t);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error(e);
        }
        return null;
    }
    
    @Override
    public void onClickFormTab() {
        novaLocalizacao();
    }
}
