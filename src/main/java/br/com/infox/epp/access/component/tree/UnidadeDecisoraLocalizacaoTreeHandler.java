package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;

@Name(UnidadeDecisoraLocalizacaoTreeHandler.NAME)
@AutoCreate
public class UnidadeDecisoraLocalizacaoTreeHandler extends AbstractTreeHandler<Localizacao> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraLocalizacaoTree";
	
	private static final String QUERY_ROOTS = "from Localizacao order by localizacao";

	@Override
	protected String getQueryRoots() {
		return QUERY_ROOTS;
	}

	@Override
	protected String getQueryChildren() {
		return "from Localizacao where localizacaoPai = :" + EntityNode.PARENT_NODE;
	}
	
}
