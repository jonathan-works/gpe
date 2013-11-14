package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.core.Events;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.EntityUtil;

public class TarefasEntityNode<E> extends EntityNode<Map<String,Object>> {
	
	private static final long serialVersionUID = 1L;
	private List<TarefasEntityNode<E>> rootNodes;
	private List<TarefasEntityNode<E>> nodes;
	private List<EntityNode<E>> caixas;
	private List<Query> queryCaixas = new ArrayList<Query>();
	
	public TarefasEntityNode(String queryChildren) {
		super(queryChildren);
	}

	public TarefasEntityNode(String[] queryChildren, List<Query> queryCaixas) {
		super(queryChildren);
		this.queryCaixas = queryCaixas;
	}

	public TarefasEntityNode(EntityNode<Map<String,Object>> parent, 
			Map<String,Object> entity,
			String[] queryChildren, List<Query> queryCaixas) {
		super(parent, entity, queryChildren);
		this.queryCaixas = queryCaixas;
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<EntityNode<E>> getCaixas() {
		if (caixas == null) {
			caixas = new ArrayList<EntityNode<E>>();
			boolean parent = true;
			for (Query query : queryCaixas) {
				if (!isLeaf()) {
					List<E> children = (List<E>) getCaixasList(query, getEntity()); 
					for (E n : children) {
						if (!n.equals(getIgnore())) {
							EntityNode<E> node = (EntityNode<E>) createChildNode((Map<String, Object>) n);
							node.setIgnore((E) getIgnore());
							node.setLeaf(!parent);
							caixas.add(node);
						}
					}
					parent = false;
				}
			}
			
			Events.instance().raiseEvent("entityNodesPostGetNodes", caixas);
		}
		return caixas;
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<TarefasEntityNode<E>> getRootsFluxos(Query queryRoots) {
		if (rootNodes == null) {
			rootNodes = new ArrayList<TarefasEntityNode<E>>();
			List<E> roots = queryRoots.getResultList();
			for (E e : roots) {
				if (!e.equals(getIgnore())) {
					TarefasEntityNode<Map<String, Object>> node = createRootNode((Map<String, Object>) e);
					node.setIgnore(getIgnore());
					rootNodes.add((TarefasEntityNode<E>) node);
				}
			}
		}
		return rootNodes;
	}	
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<TarefasEntityNode<E>> getNodesTarefas() {
		if (nodes == null) {
			nodes = new ArrayList<TarefasEntityNode<E>>();
			boolean parent = true;
			for (String query : getQueryChildrenList()) {
				if (!isLeaf()) {
					List<E> children = (List<E>) getChildrenList(query, getEntity()); 
					for (E n : children) {
						if (!n.equals(getIgnore())) {
							TarefasEntityNode<Map<String, Object>> node = createChildNode((Map<String, Object>) n);
							node.setIgnore(getIgnore());
							node.setLeaf(!parent);
							nodes.add((TarefasEntityNode<E>) node);
						}
					}
					parent = false;
				}
			}
			
			Events.instance().raiseEvent("entityNodesPostGetNodes", nodes);
		}
		return nodes;
	}
	
	@Override
	protected TarefasEntityNode<Map<String,Object>> createRootNode(Map<String,Object> n) {
		return new TarefasEntityNode<Map<String,Object>>(null, n, getQueryChildren(), queryCaixas);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Override
	protected List<Map<String,Object>> getChildrenList(String hql, Map<String,Object> entity) {
		Query query = EntityUtil.createQuery(hql);
		return query.setParameter("idFluxo", entity.get("idFluxo"))
				.getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected List<Map<String,Object>> getCaixasList(Query query, Map<String,Object> entity) {
		return query.setParameter("taskId", entity.get("idTarefa"))
				.getResultList();
	}

	@Override
	public String getType() {
		return (String) getEntity().get("type");
	}
	
	@Override
	protected TarefasEntityNode<Map<String,Object>> createChildNode(Map<String,Object> n) {
		return new TarefasEntityNode<Map<String,Object>>(this, n, getQueryChildren(), queryCaixas);
	}

	public Integer getTarefaId() {
	    if (getEntity() != null) {
            return (Integer) getEntity().get("idTarefa");
        }
        return 0;
	}
	
	public Integer getTaskId() { 
		if (getEntity() != null) {
			return ((Long) getEntity().get("idTask")).intValue();
		}
		return 0;
	}

	public void setQueryCaixas(List<Query> queryCaixas) {
		this.queryCaixas = queryCaixas;
	}

	public List<Query> getQueryCaixas() {
		return queryCaixas;
	}

}