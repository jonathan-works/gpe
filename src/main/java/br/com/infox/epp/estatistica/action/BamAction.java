package br.com.infox.epp.estatistica.action;

import static org.jboss.seam.faces.FacesMessages.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;
import br.com.infox.epp.tarefa.type.PrazoEnum;

/**
 * Classe Action para o BAM
 * @author tassio
 */
@Name(BamAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class BamAction {

	public static final String NAME = "bamAction";
	
	@In
	private FluxoManager fluxoManager;
	@In
	private ProcessoEpaManager processoEpaManager;
	@In
	private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	private List<Fluxo> fluxoList;
	
	/**
	 * Retorna todos os fluxos ativos no sistema
	 * @return lista com todos os fluxos ativos
	 */
	public List<Fluxo> getFluxoList() {
		if (fluxoList == null) {
			fluxoList = fluxoManager.getFluxoList(); 
		}
		return fluxoList;
	}
	
	public List<Fluxo> getFluxoSuggest(Object suggest) {
		String prefix = (String) suggest;
		List<Fluxo> fluxoList = getFluxoList();
		List<Fluxo> result = new ArrayList<Fluxo>();
		for (Fluxo fluxo: fluxoList) {
			if (fluxo.getFluxo().startsWith(prefix)) {
				result.add(fluxo);
			}
		}
		return result;
	}
	
	public Boolean contemProcessoAtrasado(Fluxo fluxo) {
		return fluxoManager.contemProcessoAtrasado(fluxo);
	}
	
	/**
	 * Retorna os processo não finalizados de um determinado fluxo
	 */
	public List<ProcessoEpa> getProcessosNaoFinalizados(Fluxo fluxo) {
		return processoEpaManager.listNotEnded(fluxo);
	}

	public TaskInstance getTaskInstance(Long idTaskInstance) {
		return ManagedJbpmContext.instance().getTaskInstance(idTaskInstance);
	}
    
	public void forceUpdateTarefasFinalizadas() {
	    for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getTarefaEnded()) {
            pt.setUltimoDisparo(pt.getDataInicio());
            pt.setTempoGasto(0);
            pt.setPorcentagem(0);
            try {
                processoEpaTarefaManager.updateTempoGasto(pt.getDataFim(), pt);
            } catch (DAOException e) {
                instance().add(Severity.ERROR,"forceUpdateFinalizadas()",e);
            }
        }
        try {
            processoEpaManager.updateTempoGastoProcessoEpa();
        } catch (DAOException e) {
            instance().add(Severity.ERROR,"forceUpdateFinalizadasProcesso()",e);
        }
	}
	
    public void forceUpdateTarefasNaoFinalizadas() {
        Date fireTime = new Date();
        for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getTarefaNotEnded(PrazoEnum.H)) {
            pt.setUltimoDisparo(pt.getDataInicio());
            pt.setTempoGasto(0);
            pt.setPorcentagem(0);
            try {
                processoEpaTarefaManager.updateTempoGasto(fireTime, pt);
            } catch (DAOException e) {
                instance().add(Severity.ERROR,"forceUpdate(H)",e);
            }
        }
        for (ProcessoEpaTarefa pt : processoEpaTarefaManager.getTarefaNotEnded(PrazoEnum.D)) {
            pt.setUltimoDisparo(pt.getDataInicio());
            try {
                processoEpaTarefaManager.updateTempoGasto(fireTime, pt);
            } catch (DAOException e) {
                instance().add(Severity.ERROR,"forceUpdate(D)",e);
            }
        }
        try {
            processoEpaManager.updateTempoGastoProcessoEpa();
        } catch (DAOException e) {
            instance().add(Severity.ERROR,"forceUpdateProcesso()",e);
        }
    }
}