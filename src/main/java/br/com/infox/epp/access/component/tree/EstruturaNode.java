package br.com.infox.epp.access.component.tree;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;

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

	@Override
	protected EntityNode<Localizacao> createChildNode(Localizacao n) {
		return new EstruturaNode(this, n, getQueryChildren());
	}

	@Override
	protected EntityNode<Localizacao> createRootNode(Localizacao n) {
		return new EstruturaNode(null, n, getQueryChildren());
	}

}