/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.access.component.tree;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.itx.util.ComponentUtil;


@Name(LocalizacaoTreeHandler.NAME)
@AutoCreate
public class LocalizacaoTreeHandler extends AbstractTreeHandler<Localizacao> {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "localizacaoTree";
	
	@Override
	protected String getQueryRoots() {
		return "select n from Localizacao n " 
		    + "where localizacaoPai is null "
			+ "order by localizacao";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Localizacao n where localizacaoPai = :"
			+ EntityNode.PARENT_NODE;
	}
	
	@Override
	protected String getEventSelected() {
		return "evtSelectLocalizacao";
	}

	@Override
	protected Localizacao getEntityToIgnore() {
		LocalizacaoCrudAction lh = ComponentUtil.getComponent("localizacaoCrudAction");
		return lh.getInstance();
	}
}