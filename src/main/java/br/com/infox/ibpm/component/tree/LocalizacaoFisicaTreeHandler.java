package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.annotations.Recursive;
import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.LocalizacaoFisica;
import br.com.itx.util.ComponentUtil;

@Name(LocalizacaoFisicaTreeHandler.NAME)
@BypassInterceptors
public class LocalizacaoFisicaTreeHandler extends AbstractTreeHandler<LocalizacaoFisica> {
	
	public static final String NAME = "localizacaoFisicaTree";
	private static final long serialVersionUID = 1L;

	@Override
	protected String getQueryRoots() {
		return "select o from LocalizacaoFisica o "
				+ "where localizacaoFisicaPai is null "
				+ "order by descricao";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from LocalizacaoFisica n where localizacaoFisicaPai = :"
				+ EntityNode.PARENT_NODE;
	}

	@Override
	protected LocalizacaoFisica getEntityToIgnore() {
		return ComponentUtil.getInstance("localizacaoFisicaHome");
	}
	
}
