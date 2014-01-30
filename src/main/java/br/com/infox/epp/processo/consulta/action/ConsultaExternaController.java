package br.com.infox.epp.processo.consulta.action;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.controller.AbstractController;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;

@Name(ConsultaExternaController.NAME)
@Scope(ScopeType.CONVERSATION)
public class ConsultaExternaController extends AbstractController {
    
    public static final String NAME = "consultaExternaController";
    public static final String TAB_VIEW = "processoExternoView";
    
    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    
    private ProcessoEpa processoEpa;

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa processoEpa) {
        this.processoEpa = processoEpa;
    }
    
    public void selectProcesso(ProcessoEpa processoEpa){
        setTab(TAB_VIEW);
        setProcessoEpa(processoEpa);
    }
    
    public List<ProcessoDocumento> getAnexosPublicos(ProcessoEpaTarefa processoEpaTarefa){
        return processoDocumentoManager.getAnexosPublicos(processoEpaTarefa.getTaskInstance());
    }
    
    public void onClickSearchNumeroTab(){
        setProcessoEpa(null);
    }
    
    public void onClickSearchParteTab(){
        setProcessoEpa(null);
    }
}
