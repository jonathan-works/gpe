package br.com.infox.ibpm.component.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTreeNode;
import org.richfaces.event.NodeSelectedEvent;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.itx.util.EntityUtil;

@Name(TarefasTreeHandler.NAME)
@Install(precedence=Install.FRAMEWORK)
@BypassInterceptors
public class TarefasTreeHandler extends AbstractTreeHandler<Map<String,Object>> {

	public static final String NAME = "tarefasTree";
	public static final String FILTER_TAREFAS_TREE = "br.com.infox.ibpm.component.tree.FilterTarefasTree";
	public static final String CLEAR_TAREFAS_TREE_EVENT = "clearTarefasTreeEvent";
	private static final long serialVersionUID = 1L;
	protected List<TarefasEntityNode<Map<String,Object>>> rootList;		
	 
	@Override
	protected String getQueryRoots() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new map(s.nomeFluxo as nomeFluxo, ");
		sb.append("max(s.idFluxo) as idFluxo, ");
		sb.append("'Fluxo' as type) ");
		sb.append("from SituacaoProcesso s ");
		sb.append("group by s.nomeFluxo ");
		sb.append("order by s.nomeFluxo");
		return sb.toString();
	}
	
	@Override
	protected String getQueryChildren() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new map(max(s.idSituacaoProcesso) as id, ");
		sb.append("s.nomeTarefa as nomeTarefa, ");
		sb.append("max(s.idTarefa) as idTask, ");
		sb.append("count(s.nomeCaixa) as qtdEmCaixa, ");
		sb.append("count(s.idProcesso) as qtd, ");
		sb.append("'");
		sb.append(getTreeType());
		sb.append("' as tree, ");
		sb.append("'Task' as type) ");
		sb.append("from SituacaoProcesso s ");
		sb.append("where s.idFluxo = :idFluxo ");
		sb.append("group by s.nomeTarefa ");
		sb.append("order by s.nomeTarefa");
		return sb.toString();
	}
	
	protected String getTreeType() {
		return "caixa";
	}
	
	protected String getQueryCaixas() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new map(c.idCaixa as idCaixa, ");
		sb.append("c.tarefa.idTarefa as idTarefa, ");
		sb.append("c.nomeCaixa as nomeCaixa, ");
		sb.append("'Caixa' as type, ");
		sb.append("(select count(distinct sp.idProcesso) from SituacaoProcesso sp where sp.idCaixa = c.idCaixa) as qtd) ");
		sb.append("from Caixa c where c.tarefa.idTarefa = :taskId order by c.nomeCaixa");
		return sb.toString();
	}
	
	@Override
	protected String getEventSelected() {
		return "selectedTarefasTree";
	}
	
	public Integer getTaskId() {
		if (getSelected() != null) {
			return (Integer) getSelected().get("idTask");
		}
		return 0;
	}
	
	public static TarefasTreeHandler instance() {
		return (TarefasTreeHandler) Component.getInstance(TarefasTreeHandler.NAME);
	}
	
	@Override
	protected TarefasEntityNode<Map<String,Object>> createNode() {
		return new TarefasEntityNode<Map<String,Object>>(getQueryChildrenList(), getQueryCaixasList());
	}
	
	public List<TarefasEntityNode<Map<String,Object>>> getTarefasRoots() {
		if (rootList == null || rootList.isEmpty()) {
			Events.instance().raiseEvent(FILTER_TAREFAS_TREE);
			Query query = EntityUtil.getEntityManager().createQuery(getQueryRoots());
			TarefasEntityNode<Map<String,Object>> entityNode = createNode();
			rootList = entityNode.getRootsFluxos(query);
		}
		return rootList;
	}
	
	public void refresh() {
		if (rootList != null) {
			rootList.clear();
		}
	}
	
	private List<Query> getQueryCaixasList() {
		List<Query> list = new ArrayList<Query>();
		Query query = getEntityManager().createQuery(getQueryCaixas());
		list.add(query);
		return list;
	}
	
	@Override
	public void clearTree() {
		Events.instance().raiseEvent(CLEAR_TAREFAS_TREE_EVENT);
		rootList = null;
		super.clearTree();
	}
	
	@Override
	public void selectListener(NodeSelectedEvent nodeSelectedEvent)
			throws AbortProcessingException {
		HtmlTreeNode tree = (HtmlTreeNode) nodeSelectedEvent.getSource();
		treeId = tree.getId();
		UITree uiTree = tree.getUITree();
		EntityNode<Map<String,Object>> en = (EntityNode<Map<String,Object>>) uiTree.getRowData(); 
		setSelected(en.getEntity());
		en.getEntity().get("idTask");
		Events.instance().raiseEvent(getEventSelected(), getSelected());
	}
}