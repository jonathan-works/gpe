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
		return "Selecionar assistente de express�o";
	}
	
	@Override
	public void extractParameters(String expression) {
	}

	@Override
	public boolean isPublic() {
		return false;
	}


}