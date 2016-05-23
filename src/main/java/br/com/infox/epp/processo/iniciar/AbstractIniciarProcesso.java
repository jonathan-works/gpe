package br.com.infox.epp.processo.iniciar;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

public abstract class AbstractIniciarProcesso implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected static final String SCRIPT = "open('{contextPath}/Processo/movimentar.seam?idProcesso={idProcesso}&idTaskInstance={idTaskInstance}', '{idProcesso}popUpFluxo', 'fullscreen=1, resizable=yes,scrollbars=1');";
    
    @Inject
    protected JsfUtil jsfUtil;
    @Inject
    protected SituacaoProcessoDAO situacaoProcessoDAO;
    
    public abstract Processo getProcesso();
    
    public abstract String iniciar();
    
    protected void openMovimentarIfAccessible(ProcessInstance processInstance) {
        Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
        if (taskInstances != null) {
            for (TaskInstance taskInstance : taskInstances) {
                boolean canOpenTask = situacaoProcessoDAO.canOpenTask(taskInstance.getId(), null, false);
                if (canOpenTask) {
                    PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
                    String script = SCRIPT.replace("{contextPath}", pathResolver.getContextPath())
                            .replace("{idTaskInstance}", String.valueOf(taskInstance.getId()))
                            .replace("{idProcesso}", getProcesso().getIdProcesso().toString());
                    jsfUtil.execute(script);
                    break;
                }
            }
        }
    }
    

}
