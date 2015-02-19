package br.com.infox.epp.processo.handler;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.bpm.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.ibpm.task.bean.TaskBean;
import br.com.infox.ibpm.task.manager.UsuarioTaskInstanceManager;
import br.com.infox.ibpm.variable.VariableHandler;

@Name(ProcessoHandler.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoHandler";

    private List<TaskInstance> taskInstanceList;
    private List<TaskInstance> taskDocumentList;
    private Map<TaskInstance, List<Documento>> anexoMap = new HashMap<TaskInstance, List<Documento>>();

    private int inicio;

    @In
    private ProcessoManager processoManager;
    @In
    private DocumentoManager documentoManager;
    @In
    private UsuarioTaskInstanceManager usuarioTaskInstanceManager;

    @SuppressWarnings(UNCHECKED)
    public List<TaskInstance> getTaskInstanceList() {
        if (taskInstanceList == null) {
            Collection<TaskInstance> taskInstances = ProcessInstance.instance().getTaskMgmtInstance().getTaskInstances();
            taskInstanceList = new ArrayList<TaskInstance>(taskInstances);

            Session session = ManagedJbpmContext.instance().getSession();
            List<org.jbpm.graph.exe.ProcessInstance> l = session.getNamedQuery("GraphSession.findSubProcessInstances").setParameter("processInstance", ProcessInstance.instance()).list();

            for (org.jbpm.graph.exe.ProcessInstance p : l) {
                Collection<TaskInstance> tis = p.getTaskMgmtInstance().getTaskInstances();
                if (tis != null) {
                    taskInstanceList.addAll(tis);
                }
            }

            Collections.sort(taskInstanceList, new Comparator<TaskInstance>() {
                public int compare(TaskInstance o1, TaskInstance o2) {
                    int i1 = Integer.MAX_VALUE;
                    int i2 = Integer.MAX_VALUE;
                    if (o1.getStart() != null) {
                        i1 = (int) o1.getStart().getTime();
                    }
                    if (o2.getStart() != null) {
                        i2 = (int) o2.getStart().getTime();
                    }
                    return i2 - i1;
                }
            });
        }
        return taskInstanceList;
    }
    
    public List<TaskBean> getTaskBeanList() {
    	List<TaskInstance> list = getTaskInstanceList();
    	List<TaskBean> beans = new ArrayList<TaskBean>();
    	for (TaskInstance taskInstance : list) {
    		beans.add(new TaskBean(taskInstance, usuarioTaskInstanceManager.find(taskInstance.getId())));
    	}
    	return beans;
    }

    public List<TaskInstance> getTaskDocumentList() {
        if (taskDocumentList == null) {
            taskDocumentList = new ArrayList<TaskInstance>(getTaskInstanceList());
            for (Iterator<TaskInstance> it = taskDocumentList.iterator(); it.hasNext();) {
                TaskInstance t = it.next();
                if (VariableHandler.instance().getTaskVariables(t.getId()).isEmpty()) {
                    it.remove();
                }
            }
        }
        return taskDocumentList;
    }

    public List<Documento> getAnexosPublicos(TaskInstance task) {
        return documentoManager.getAnexosPublicos(task.getId());
    }

    public List<Documento> getAnexos(TaskInstance task) {
        List<Documento> anexoList = anexoMap.get(task);
        if (anexoList == null) {
            anexoList = documentoManager.getDocumentoByTask(task);
            anexoMap.put(task, anexoList);
        }
        return anexoList;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        if (inicio != 0) {
            this.inicio = inicio;
        }
    }

    public long getTaskId() {
        return 0;
    }

    public void setTaskId(long id) {
        if (id != 0) {
            BusinessProcess.instance().setTaskId(id);
            TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
            long processId = taskInstance.getProcessInstance().getId();
            BusinessProcess.instance().setProcessId(processId);
            taskDocumentList = null;
            taskInstanceList = null;
            inicio = getTaskDocumentList().indexOf(taskInstance) + 1;
        }
    }
    
    public long getProcessoId() {
        return 0;
    }
    
    public void setProcessoId(long processId) {
        if (processId != 0) {
            BusinessProcess.instance().setProcessId(processId);
            taskDocumentList = null;
            taskInstanceList = null;
            inicio = 1;
        }
    }

    public boolean hasPartes() {
        Long idJbpm = ProcessInstance.instance().getId();
        return processoManager.hasPartes(idJbpm);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        return processoManager.getPessoaFisicaList();
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        return processoManager.getPessoaJuridicaList();
    }

}
