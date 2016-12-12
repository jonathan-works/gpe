package br.com.infox.epp.processo.partes.crud;

import javax.inject.Inject;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.partes.dao.ParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;

@Name(ParticipanteProcessoCrudAction.NAME)
@ContextDependency
public class ParticipanteProcessoCrudAction extends AbstractCrudAction<ParticipanteProcesso, ParticipanteProcessoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "participanteProcessoCrudAction";
    
    @Inject
    private ParticipanteProcessoDAO participanteProcessoDAO;

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
}
