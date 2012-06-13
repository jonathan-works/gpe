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

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions;

import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.jbpm.ActionTemplate;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;


@Name("modeloDocumento")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Startup
public class ModeloDocumentoAction extends ActionTemplate {
	
	private static final long serialVersionUID = 1L;
	
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
		parameters = getExpressionParameters(expression);
		ProcessBuilder.instance().getCurrentTask().setCurrentVariable(
				(String) parameters[0]);
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

	private EntityManager getEntityManager() {
		EntityManager em = EntityUtil.getEntityManager();
		return em;
	}
	
	public String getConteudo(ModeloDocumento modeloDocumento) {
		if (modeloDocumento == null) {
			return null;
		}
		StringBuilder modeloProcessado = new StringBuilder();		
		String[] linhas = modeloDocumento.getModeloDocumento().split("\n");
		for (int i = 0; i < linhas.length; i++) {
			if (modeloProcessado.length() > 0) {
				modeloProcessado.append('\n');
			}
			try {
				String linha =(String) Expressions.instance()
					.createValueExpression(linhas[i]).getValue();
				modeloProcessado.append(linha);
			} catch (RuntimeException e) {
				modeloProcessado.append("Erro na linha: '" +linhas[i]);
				modeloProcessado.append("': " + e.getMessage());
				e.printStackTrace();
			}
		}
		return modeloProcessado.toString();	
	}
	
	@SuppressWarnings("unchecked")
	public List<ModeloDocumento> getModeloItems(String variavel) {
		String listaModelos = (String) new Util().eval(variavel);
		List<ModeloDocumento> list = getEntityManager()
			.createQuery("select o from ModeloDocumento o " +
					"where o.idModeloDocumento in (" +
					listaModelos + ") order by modeloDocumento")
			.getResultList();
		return list;
	}
	
	public static ModeloDocumentoAction instance()  {
		return (ModeloDocumentoAction) Component.getInstance(ModeloDocumentoAction.class);
	}


}