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
package br.com.infox.ibpm.jbpm.actions;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.jbpm.ActionTemplate;


@Name("selectTemplate")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Startup
public class SelectTemplateAction extends ActionTemplate {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getExpression() {
		return null;
	}
	
	@Override
	public String getFileName() {
		return "selectTemplate.xhtml";
	}
	
	@Override
	public String getLabel() {
		return "Selecionar assistente de expressão";
	}
	
	@Override
	public void extractParameters(String expression) {
	}

	@Override
	public boolean isPublic() {
		return false;
	}


}