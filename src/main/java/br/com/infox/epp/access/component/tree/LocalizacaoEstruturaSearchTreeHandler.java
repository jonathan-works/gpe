package br.com.infox.epp.access.component.tree;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.ibpm.home.LocalizacaoHome;
import br.com.itx.util.ComponentUtil;

@Name("localizacaoEstruturaSearchTreeHandler")
@BypassInterceptors
public class LocalizacaoEstruturaSearchTreeHandler extends LocalizacaoEstruturaTreeHandler {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected String getQueryRoots() {
		StringBuilder sb = new StringBuilder("select n from Localizacao n ");
		sb.append("where localizacaoPai is null ");
		if (getMostrarSomenteEstrutura()) {
			sb.append("and estrutura = true ");
		}
		sb.append("order by localizacao");
		return sb.toString();
	}
	
	private boolean getMostrarSomenteEstrutura() {
		LocalizacaoHome home  = ComponentUtil.getComponent(
				"localizacaoHome", ScopeType.CONVERSATION);
		return home.getMostrarSomenteEstrutura();
	}

	@Override
	protected EntityNode<Localizacao> createNode() {
		return new LocalizacaoNodeSearch(getQueryChildrenList());
	}	

}