package br.com.infox.ibpm.home;

import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.ibpm.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.entity.Caixa;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.jbpm.actions.JbpmEventsHandler;
import br.com.infox.ibpm.manager.TarefaManager;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(CaixaHome.NAME)
public class CaixaHome extends AbstractCaixaHome<Caixa> {

	public static final String NAME = "caixaHome";
	public static final String ADD_CAIXA_EVENT = "addCaixaEvent";
	private static final long serialVersionUID = 1L;
	private String nomeNovaCaixa;
	private Integer idTarefaAnterior;
	private Integer idTarefaSearch;
	
	@In private TarefaManager tarefaManager;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if(idTarefaAnterior != null) {
			instance.setTarefaAnterior(getEntityManager().find(Tarefa.class, idTarefaAnterior));
		}else{
			instance.setTarefaAnterior(null);
		}
		
		return super.beforePersistOrUpdate();
	}

	public List<SelectItem> getPreviousTasks() {
		return getPreviousTasks(instance.getTarefa());
	}
	
	public List<SelectItem> getPreviousTasks(Integer idTarefa) {
		return getPreviousTasks(EntityUtil.find(Tarefa.class, idTarefa));
	}
	
    public List<SelectItem> getPreviousTasks(Tarefa tarefa) {
		return tarefaManager.getPreviousTasks(tarefa);
	}

	
	public void setNomeNovaCaixa(String nomeNovaCaixa) {
		this.nomeNovaCaixa = nomeNovaCaixa;
	}

	public String getNomeNovaCaixa() {
		return nomeNovaCaixa;
	}
	
	@Override
	public void newInstance() {
		nomeNovaCaixa = null;
		super.newInstance();
	}
	
	public void addCaixa(int idTarefa) {
		instance = createInstance();
		instance.setNomeCaixa(nomeNovaCaixa);
		instance.setTarefa(getEntityManager().find(Tarefa.class, idTarefa));
		String persist = super.persist();
		if(persist != null && !"".equals(persist)) {
			EntityUtil.flush();
			JbpmEventsHandler.updatePostDeploy();
		}
		TarefasTreeHandler tree = getComponent("tarefasTree");
		tree.clearTree();
		getEntityManager().clear();
		newInstance();
	}
	
	public static CaixaHome instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public Integer getIdTarefaSearch() {
		return idTarefaSearch;
	}

	public void setIdTarefaSearch(Integer idTarefaSearch) {
		this.idTarefaSearch = idTarefaSearch;
	}

	public Integer getIdTarefaAnterior() {
		if(isManaged() && instance.getTarefaAnterior() != null) {
			idTarefaAnterior = instance.getTarefaAnterior().getIdTarefa();
		}
		return idTarefaAnterior;
	}

	public void setIdTarefaAnterior(Integer idTarefaAnterior) {
		this.idTarefaAnterior = idTarefaAnterior;
	}
}