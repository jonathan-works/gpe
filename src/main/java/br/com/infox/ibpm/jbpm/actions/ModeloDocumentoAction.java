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

import java.util.List;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import br.com.infox.epp.manager.ModeloDocumentoManager;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.jbpm.ActionTemplate;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.manager.VariavelManager;
import br.com.itx.component.Util;

@Name(ModeloDocumentoAction.NAME)
@Scope(ScopeType.SESSION)
@Startup
public class ModeloDocumentoAction extends ActionTemplate {
	
	public static final String NAME = "modeloDocumento";
	private static final long serialVersionUID = 1L;
	
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	
	private ModeloDocumento modeloJbpm;
	
	public List<ModeloDocumento> getModeloDocumentoList() {
		return modeloDocumentoManager.getModeloDocumentoList();
	}

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
		return "Atribuir modelo a uma vari�vel";
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
		parameters = getExpressionParameters(expression);
		if (parameters.length > 0) {
			ProcessBuilder.instance().getTaskFitter().getCurrentTask().setCurrentVariable(
					(String) parameters[0]);
		}
	}

	public void set(String variavel, int... idModeloDocumento) {
		variavel += "Modelo";
		StringBuilder s = new StringBuilder();
		for (int i : idModeloDocumento) {
			if (s.length() != 0) {
				s.append(",");
			}
			s.append(i);
		}
		Object valor = JbpmUtil.getProcessVariable(variavel);
		if (valor == null) {
			JbpmUtil.createProcessVariable(variavel, s.toString());
		} else {
			JbpmUtil.setProcessVariable(variavel, valor + "," + s.toString());
		}
	}

	public String getConteudo(int idModeloDocumento)	{
		return getConteudo(getModeloDocumento(idModeloDocumento));
	}
	
	public ModeloDocumento getModeloDocumento(int idModeloDocumento)	{
		return modeloDocumentoManager.find(ModeloDocumento.class, idModeloDocumento);
	}

	public String getConteudo(String tituloModeloDocumento) {
		return getConteudo(getModeloDocumento(tituloModeloDocumento));		
	}

	public ModeloDocumento getModeloDocumento(String tituloModeloDocumento)	{
		return modeloDocumentoManager.getModeloDocumentoByTitulo(tituloModeloDocumento);
	}
	
	
	public String getConteudo(ModeloDocumento modeloDocumento) {
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);	
	}
	
	public List<ModeloDocumento> getModeloItems(String variavel) {
		String listaModelos = (String) new Util().eval(variavel);
		return modeloDocumentoManager.getModelosDocumentoInListaModelo(listaModelos);
	}
	
	public static ModeloDocumentoAction instance()  {
		return (ModeloDocumentoAction) Component.getInstance(ModeloDocumentoAction.class);
	}

}