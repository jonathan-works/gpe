package br.com.infox.epp.processo.partes.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.HistoricoParticipanteProcessoManager;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;

@Name(ParticipanteProcessoCrudAction.NAME)
public class ParticipanteProcessoCrudAction extends AbstractCrudAction<ParticipanteProcesso, ParticipanteProcessoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "participanteProcessoCrudAction";

    @In
    private HistoricoParticipanteProcessoManager historicoParticipanteProcessoManager;

    private String motivoModificacao;
    private Boolean showHistory;

    public String getMotivoModificacao() {
        return motivoModificacao;
    }

    public void setMotivoModificacao(String motivoModificacao) {
        this.motivoModificacao = motivoModificacao;
    }

    public void inverterSituacao() {
        try {
        	historicoParticipanteProcessoManager.createHistorico(getInstance(), getMotivoModificacao());
            getInstance().setAtivo(!getInstance().getAtivo());
        } catch (DAOException daoException) {
            onDAOExcecption(daoException);
        }
        super.save();
        getManager().refresh(getInstance());
        newInstance();
        setMotivoModificacao(null);
    }

    public Boolean getShowHistory() {
        return showHistory;
    }

    public void setShowHistory(Boolean showHistory) {
        this.showHistory = showHistory;
    }
    
    public void setShowHistory(ParticipanteProcesso instance) {
        setInstance(instance);
        setShowHistory(true);
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        setShowHistory(false);
    }
    
    @Override
    public void setInstance(ParticipanteProcesso instance) {
        super.setInstance(instance);
        setShowHistory(false);
    }
}
