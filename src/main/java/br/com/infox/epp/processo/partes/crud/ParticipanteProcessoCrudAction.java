package br.com.infox.epp.processo.partes.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;

@Named
@ViewScoped
public class ParticipanteProcessoCrudAction extends AbstractCrudAction<ParticipanteProcesso, ParticipanteProcessoManager> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ParticipanteProcessoDAO participanteProcessoDAO;
    @Inject
    private ParticipanteProcessoManager participanteProcessoManager;

    private String motivoModificacao;
    private Boolean showHistory;

    public String getMotivoModificacao() {
        return motivoModificacao;
    }

    public void setMotivoModificacao(String motivoModificacao) {
        this.motivoModificacao = motivoModificacao;
    }

    @ExceptionHandled(MethodType.UPDATE)
    public void inverterSituacao() {
    	participanteProcessoDAO.inverterSituacao(getInstance(), getMotivoModificacao());
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

    @Override
    protected ParticipanteProcessoManager getManager() {
        return participanteProcessoManager;
    }
}
