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
package br.com.infox.epp.documento.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.core.action.ActionTemplate;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(ModeloDocumentoAction.NAME)
@Scope(ScopeType.SESSION)
@Startup
public class ModeloDocumentoAction extends ActionTemplate {
	
	public static final String NAME = "modeloDocumento";
	private static final long serialVersionUID = 1L;
	
	private ModeloDocumento modeloJbpm;
	
	public ModeloDocumento getModeloJbpm() {
		return modeloJbpm;
	}
	public void setModeloJbpm(ModeloDocumento modeloJbpm) {
		this.modeloJbpm = modeloJbpm;
	}

	@Override
	public String getExpression() {
		return "modeloDocumento.set";
	}
	
	@Override
	public String getFileName() {
		return "setModeloDocumento.xhtml";
	}
	
	@Override
	public String getLabel() {
		return "Atribuir modelo a uma variável";
	}
	
	@Override
	public boolean isPublic() {
		return false;
	}
	
	@Override
	public void extractParameters(String expression) {
		if (expression == null || "".equals(expression)) {
			return;
		}
		setParameters(getExpressionParameters(expression));
		if (getParameters().length > 0) {
			ProcessBuilder.instance().getTaskFitter().getCurrentTask().setCurrentVariable(
					(String) getParameters()[0]);
		}
	}

	public void set(String variavel, int... idModeloDocumento) {
		String variavelModelo = variavel + "Modelo";
		StringBuilder s = new StringBuilder();
		for (int i : idModeloDocumento) {
			if (s.length() != 0) {
				s.append(",");
			}
			s.append(i);
		}
		Object valor = JbpmUtil.getProcessVariable(variavelModelo);
		if (valor == null) {
			JbpmUtil.createProcessVariable(variavelModelo, s.toString());
		} else {
			JbpmUtil.setProcessVariable(variavelModelo, valor + "," + s.toString());
		}
	}

}