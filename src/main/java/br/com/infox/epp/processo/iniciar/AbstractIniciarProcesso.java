package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.service.ProcessoService;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

public abstract class AbstractIniciarProcesso implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    protected JsfUtil jsfUtil;
    @Inject
    protected SituacaoProcessoDAO situacaoProcessoDAO;
    @Inject
    protected ProcessoService processoService;
    @Inject
    protected TaskInstanceManager taskInstanceManager; 
    
    public abstract String iniciar();
    
    protected void iniciarProcesso(Processo processo) {
        iniciarProcesso(processo, null);
    }
    
    protected void iniciarProcesso(Processo processo, Map<String, Object> variables) {
        ProcessInstance processInstance = processoService.iniciarProcessoRemoverMetadadoStatus(processo, variables);
        openMovimentarIfAccessible(processInstance, processo);
    }
    
    protected void openMovimentarIfAccessible(ProcessInstance processInstance, Processo processo) {
        Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        if (taskInstances != null) {
            for (TaskInstance taskInstance : taskInstances) {
                boolean canOpenTask = situacaoProcessoDAO.canOpenTask(taskInstance.getId(), null, false);
                if (canOpenTask) {
                    abrirExecucaoTarefa(processo, taskInstance);
                    break;
                }
            }
        }
    }
    
    protected void abrirExecucaoTarefa(Processo processo, TaskInstance taskInstance) {
        String url_path = "infox.openPopUp('executar{idProcesso}', '{contextPath}/Processo/movimentar.seam?idProcesso={idProcesso}&amp;idTaskInstance={idTaskInstance}')";
        PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
        String script = url_path.replace("{contextPath}", pathResolver.getContextPath())
                .replace("{idTaskInstance}", String.valueOf(taskInstance.getId()))
                .replace("{idProcesso}", String.valueOf(processo.getIdProcesso()));
        jsfUtil.execute(script);
    }

}
