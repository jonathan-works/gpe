/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.processo.handler;

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

import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.ibpm.jbpm.handler.VariableHandler;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.EntityUtil;


@Name(ProcessoHandler.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoHandler";
	
	private List<TaskInstance> taskInstanceList;
	private List<TaskInstance> taskDocumentList;
	private Map<TaskInstance, List<ProcessoDocumento>> anexoMap = 
			new HashMap<TaskInstance, List<ProcessoDocumento>>();

	private int inicio;
	
	@In private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@In private ProcessoEpaManager processoEpaManager;
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<TaskInstance> getTaskInstanceList() {
		if (taskInstanceList == null) {
			Collection<TaskInstance> taskInstances = 
				ProcessInstance.instance().getTaskMgmtInstance().getTaskInstances();
			taskInstanceList = new ArrayList<TaskInstance>(taskInstances);
			
			Session session = ManagedJbpmContext.instance().getSession();		
			List<org.jbpm.graph.exe.ProcessInstance> l = session.getNamedQuery("GraphSession.findSubProcessInstances")
				.setParameter("processInstance", ProcessInstance.instance())
				.list();
			
			for (org.jbpm.graph.exe.ProcessInstance p : l) {
				Collection<TaskInstance> tis = p.getTaskMgmtInstance().getTaskInstances();
				taskInstanceList.addAll(tis);
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
					return i1 - i2;
				}
			});
		}
		return taskInstanceList;
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
		return taskDocumentList ;
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
    public List<ProcessoDocumento> getAnexosPublicos(TaskInstance task) {
	    List<ProcessoDocumento> anexoList;
        final String hql = "select o " +
                "from ProcessoDocumento o " +
                "inner join o.tipoProcessoDocumento tpd " +
                "where o.idJbpmTask = :id " +
                "and (tpd.visibilidade='A' " +
                "or tpd.visibilidade='E')";
        anexoList = EntityUtil.createQuery(hql)
                .setParameter("id", task.getId())
                .getResultList();
        return anexoList ;
	}
	
	public List<ProcessoDocumento> getAnexos(TaskInstance task) {
		List<ProcessoDocumento> anexoList = anexoMap.get(task);
		if (anexoList == null) {
			anexoList = classificacaoDocumentoManager.getProcessoDocumentoByTask(task);
			anexoMap.put(task, anexoList);
		}
		return anexoList ;
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
	
	public Item getItemDoProcesso(int idProcesso){
		return processoEpaManager.getItemDoProcesso(idProcesso);
	}
	
	public boolean hasPartes(){
		Long idJbpm = ProcessInstance.instance().getId();
		return processoEpaManager.hasPartes(idJbpm);
	}
	
	public List<PessoaFisica> getPessoaFisicaList(){
		return processoEpaManager.getPessoaFisicaList();
	}
	
	public List<PessoaJuridica> getPessoaJuridicaList(){
		return processoEpaManager.getPessoaJuridicaList();
	}
	
}