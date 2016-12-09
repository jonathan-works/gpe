package br.com.infox.epp.access.crud;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.manager.EstruturaManager;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(LocalizacaoCrudAction.NAME)
public class LocalizacaoCrudAction extends AbstractRecursiveCrudAction<Localizacao, LocalizacaoManager> {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(LocalizacaoCrudAction.class);
    public static final String NAME = "localizacaoCrudAction";
    
    @In private EstruturaManager estruturaManager;
    @In private ActionMessagesService actionMessagesService;
    private List<Estrutura> estruturasDisponiveis;

    @Override
    public void newInstance() {
        super.newInstance();
        limparTrees();
        estruturasDisponiveis = null;
    }
    
    @Override
    public void setInstance(Localizacao instance) {
        super.setInstance(instance);
    }

    public boolean hasPermissionToEdit() {
        final Localizacao localizacaoUsuarioLogado = Authenticator.getLocalizacaoAtual();
        if (getInstance().getIdLocalizacao() == null) {
            return true;
        }
        return getManager().isLocalizacaoAncestor(localizacaoUsuarioLogado, getInstance()) && !getInstance().equals(localizacaoUsuarioLogado);
    }

    protected void limparTrees() {
        final LocalizacaoTreeHandler ret = (LocalizacaoTreeHandler) Component.getInstance(LocalizacaoTreeHandler.NAME);
        if (ret != null) {
            ret.clearTree();
        }
    }

    @Override
    protected String update() {
        String ret = null;
        final Localizacao localizacao = getInstance();
        if ((localizacao.getAtivo() != null)
                && (localizacao.getAtivo() || inativarFilhos(localizacao))) {
            ret = super.update();
        }
        return ret;
    }

    @Override
    public String inactive(Localizacao t) {
        try {
            getManager().inativar(t);
            return AbstractAction.UPDATED;
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
            LOG.error(e);
        }
        return null;
    }
    
    private boolean inativarFilhos(final Localizacao localizacao) {
        localizacao.setAtivo(Boolean.FALSE);
        for (int i = 0, quantidadeFilhos = localizacao.getLocalizacaoList().size(); i < quantidadeFilhos; i++) {
            inativarFilhos(localizacao.getLocalizacaoList().get(i));
        }
        return Boolean.TRUE;
    }

    public List<Estrutura> getEstruturasDisponiveis() {
        if (estruturasDisponiveis == null) {
            estruturasDisponiveis = estruturaManager.getEstruturasDisponiveis();
        }
        return estruturasDisponiveis;
    }
    
    public String formatCaminhoCompleto(Localizacao localizacao) {
        return getManager().formatCaminhoCompleto(localizacao);
    }
    
    public Localizacao getLocalizacaoPai() {
        return getInstance().getLocalizacaoPai();
    }
    
    public void setLocalizacaoPai(Localizacao localizacaoPai) {
        getInstance().setLocalizacaoPai(localizacaoPai);
        if (localizacaoPai != null) {
            getInstance().setEstruturaPai(localizacaoPai.getEstruturaPai());
        }
    }
    
    public void clear() {
        limparTrees();
    }
}
