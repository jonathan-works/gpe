/* $Id: PainelUsuarioHome.java 704 2010-08-12 23:21:10Z jplacerda $ */

package br.com.infox.ibpm.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Redirect;
import org.richfaces.event.DropEvent;

import br.com.infox.epp.processo.consulta.list.ConsultaProcessoEpaList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(PainelUsuarioHome.NAME)
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings(WarningConstants.UNCHECKED)
public class PainelUsuarioHome implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "painelUsuarioHome"; 

	private Map<String, Object> selected;
	private List<Integer> processoIdList;
	
	@In private ConsultaProcessoEpaList consultaProcessoEpaList;
	@In private ProcessoManager processoManager;
	@In private SituacaoProcessoManager situacaoProcessoManager;
	
	@Observer("selectedTarefasTree")
	public void onSelected(Object obj){
		this.selected = (Map<String, Object>) obj;
		processoIdList = null;
	}
	
	public Integer getIdCaixa() {
		if (selected != null) {
			return (Integer) selected.get("idCaixa");
		}
		return null;
	}

	public List<Integer> getProcessoIdList() {
		if (selected != null) {
			if (processoIdList == null) {
				processoIdList = situacaoProcessoManager.getProcessosAbertosByIdTarefa(getTarefaId(), selected);
			}
			if(processoIdList.size() == 0){
				processoIdList.add(-1);
			}
			return processoIdList;
			
		}
		return null;
	}

	public void processoCaixa(DropEvent evt) {
		Caixa caixa = EntityUtil.find(Caixa.class, evt.getDropValue());
		setProcessoCaixa(getProcessoIdList(evt.getDragValue()), caixa);
		this.processoIdList = null;
	}

	public void setProcessoCaixa(List<Integer> idList, Caixa caixa) {
		processoManager.moverProcessosParaCaixa(idList, caixa);
		refresh();
	}

	private List<Integer> getProcessoIdList(Object o) {
		List<Integer> list = new ArrayList<Integer>();
		if (o instanceof Processo) {
			list.add(((Processo) o).getIdProcesso());
		} else if (o instanceof List) {
			List<Processo> processoList = (List<Processo>) o;
			for (Processo cpt : processoList) {
				list.add(cpt.getIdProcesso());
			}
		}
		return list;
	}

	public void processoCaixaTarefa(DropEvent evt) {
		setProcessoCaixa(getProcessoIdList(evt.getDragValue()), null);
	}

	public Long getTaskId() {
		if (selected != null) {
			return (Long)selected.get("idTask");
		}
		return null;
	}
	
	public Long getTaskInstanceId() {
	    if (selected != null) {
            return (Long) selected.get("idTaskInstance");
        }
        return null;
	}
	
	public Integer getTarefaId(){
		if (selected != null) {
			return (Integer) selected.get("idTarefa");
		}
		return null;
	}

	public void setSelected(Map<String, Object> selected) {
		this.selected = selected;
	}

	public Map<String, Object> getSelected() {
		return selected;
	}
	
	public void editaCaixa() {
		Redirect r = new Redirect();
		r.setViewId("/Caixa/listView.xhtml");
		r.setParameter("tab", "form");
		r.setParameter("id", selected.get("idCaixa"));
		r.execute();
	}
	
	public void refresh() {
		TarefasTreeHandler tree = ComponentUtil.getComponent("tarefasTree");
		tree.refresh();
	}
	
	public static PainelUsuarioHome instance() {
		return (PainelUsuarioHome) Contexts.getConversationContext().get(NAME);
	}

	public void setProcessoCaixa(Caixa caixa) {
		if(consultaProcessoEpaList.getResultCount() > 0) {
			List<Integer> idList = getProcessoIdList(consultaProcessoEpaList.getResultList());
			setProcessoCaixa(idList, caixa);
			refresh();
			processoIdList = null;
		}
	}
	
}
