package br.com.infox.ibpm.component.tree;

import java.util.List;

import javax.persistence.Query;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Localizacao;

public class EstruturaNode extends EntityNode<Localizacao> {

	private static final long serialVersionUID = 1L;

	public EstruturaNode(String[] queryChildren) {
		super(queryChildren);
	}

	public EstruturaNode(EstruturaNode estruturaNode, Localizacao n,
			String[] queryChildren) {
		super(estruturaNode, n, queryChildren);
	}


	@Override
	public String getType() {
		return getEntity().getEstrutura() ? "folder" : "leaf";
	}

	protected EntityNode<Localizacao> createChildNode(Localizacao n) {
		return new EstruturaNode(this, n, getQueryChildren());
	}

	protected EntityNode<Localizacao> createRootNode(Localizacao n) {
		return new EstruturaNode(null, n, getQueryChildren());
	}

}