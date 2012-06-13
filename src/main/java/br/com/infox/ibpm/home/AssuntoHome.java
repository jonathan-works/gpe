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
package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.ibpm.component.tree.AssuntoTreeHandler;
import br.com.infox.ibpm.entity.Assunto;
import br.com.itx.component.AbstractHome;


/**
 * Classe para operações com "Assunto(TUA)"
 *
 */
@Name("assuntoHome")
@BypassInterceptors
public class AssuntoHome extends AbstractHome<Assunto> {

	public static final String NAME = "assuntoHome";
	private static final long serialVersionUID = 1L;

	public void limparTrees() {
		AssuntoTreeHandler ath = getComponent(AssuntoTreeHandler.NAME);
		ath.clearTree();
	}
	
	@Override
	public void newInstance() {
		limparTrees();
		super.newInstance();
	}
	
	@Override
	public String inactive(Assunto assunto) {
		RecursiveManager.inactiveRecursive(assunto);
		return super.inactive(assunto);
	}

	@Override
	public String update() {
		if (!getInstance().getAtivo()){
			RecursiveManager.inactiveRecursive(getInstance());
			return "updated";
		} else{
			return super.update();
		}
	}

	@Override
	public void setId(Object id) {
		super.setId(id);
		if(isManaged()) {
			((AssuntoTreeHandler)getComponent("assuntoTree")).setSelected(getInstance().getAssuntoPai());
		}
	}
	
}