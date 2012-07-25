package br.com.infox.ibpm.component.tree;

import java.util.List;

import javax.persistence.Query;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Localizacao;

public class LocalizacaoNodeSearch extends EntityNode<Localizacao> {
	
	private static final long serialVersionUID = 1L;
	private boolean showEstrutura;

	public LocalizacaoNodeSearch(String queryChildren) {
		super(queryChildren);
	}
	
	public LocalizacaoNodeSearch(LocalizacaoNodeSearch localizacaoNodeSearch,
			Localizacao n, String[] queryChildren) {
		super(localizacaoNodeSearch, n, queryChildren);
	}

	public LocalizacaoNodeSearch(String[] queryChildrenList) {
		super(queryChildrenList);
	}
	
	@Override
	public List<EntityNode<Localizacao>> getRoots(Query queryRoots) {
		List<EntityNode<Localizacao>> roots = super.getRoots(queryRoots);
		for (EntityNode<Localizacao> entityNode : roots) {
			LocalizacaoNodeSearch localizacaoNode = (LocalizacaoNodeSearch) entityNode;
			localizacaoNode.setShowEstrutura(showEstrutura);
		}
		return roots;
	}
	
	@Override
	protected List<Localizacao> getChildrenList(String query, Localizacao localizacao) {
		List<Localizacao> list = null;
		Localizacao estruturaFilho = localizacao == null ? null : localizacao.getEstruturaFilho();
		if (showEstrutura) {
			list = super.getChildrenList(query, localizacao);
			if (estruturaFilho != null) {
				list.add(estruturaFilho);
			}
		} else {
			list = super.getChildrenList(query, 
					estruturaFilho != null ? estruturaFilho : localizacao);
		}
		return list;
	}	

	
	@Override
	protected EntityNode<Localizacao> createChildNode(Localizacao n) {
		LocalizacaoNodeSearch node = new LocalizacaoNodeSearch(this, n, getQueryChildren());
		node.setShowEstrutura(getShowEstrutura());
		return node;
	}
	
	@Override
	protected EntityNode<Localizacao> createRootNode(Localizacao n) {
		LocalizacaoNodeSearch node = new LocalizacaoNodeSearch(null, n, getQueryChildren());
		node.setShowEstrutura(getShowEstrutura());
		return node;
	}

	public void setShowEstrutura(boolean showEstrutura) {
		this.showEstrutura = showEstrutura;
	}

	public boolean getShowEstrutura() {
		return showEstrutura;
	}	
	
}