/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Assunto;
import br.com.itx.util.ComponentUtil;


@Name(AssuntoTreeHandler.NAME)
@BypassInterceptors
public class AssuntoTreeHandler extends AbstractTreeHandler<Assunto> {

	public static final String NAME = "assuntoTree";
	private static final long serialVersionUID = 1L;
	
	@Override
	protected String getQueryRoots() {
		return "select n from Assunto n "
			+ "where assuntoPai is null "
			+ "order by assunto";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Assunto n where assuntoPai = :"
		+ EntityNode.PARENT_NODE;
	}

	@Override
	protected Assunto getEntityToIgnore() {
		return ComponentUtil.getInstance("assuntoHome");
	}
		
}

