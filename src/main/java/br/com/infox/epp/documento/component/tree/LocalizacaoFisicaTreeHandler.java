package br.com.infox.epp.documento.component.tree;

import org.jboss.seam.annotations.Name;
import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.itx.util.ComponentUtil;

@Name(LocalizacaoFisicaTreeHandler.NAME)
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
