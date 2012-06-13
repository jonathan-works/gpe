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

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Observer;

import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.jbpm.ActionTemplate;
import br.com.infox.ibpm.jbpm.handler.ActionTemplateHandler;
import br.com.itx.util.EntityUtil;


@SuppressWarnings("unchecked")
public abstract class AbstractEventoAction extends ActionTemplate {
	
	private static final long serialVersionUID = 1L;
	
	private List<Agrupamento> agrupamentos;
	private List<Agrupamento> registrados;

	private String createdExpression;
	
	@Override
	public void extractParameters(String expression) {
		if (expression == null || expression.equals("")) {
			registrados = new ArrayList<Agrupamento>();
			return;
		}
		String[] params = getExpressionParameters(expression);
		List<Integer> ids = new ArrayList<Integer>();
		for (String p : params) {
			ids.add(Integer.parseInt(p));
		}
		registrados = EntityUtil.getEntityManager().createQuery(
				"select o from Agrupamento o where " +
				"o.idAgrupamento in (:ids)")
				.setParameter("ids", ids)
				.getResultList();
	}

	public void createExpression() {
		if (registrados.isEmpty()) {
			createdExpression = "";
		} else {
			StringBuilder sb = new StringBuilder("#{");
			sb.append(getExpression()).append("(");
			for (Agrupamento ae : registrados) {
				if (! sb.toString().endsWith("(")) {
					sb.append(",");
				}
				sb.append(ae.getIdAgrupamento());
			}
			sb.append(")}");
			createdExpression = sb.toString();
		}
	}
	
	public String getCreatedExpression() {
		return createdExpression;
	}
	
	public void setAgrupamentos(List<Agrupamento> agrupamentos) {
		this.agrupamentos = agrupamentos;
	}

	public List<Agrupamento> getAgrupamentos() {
		if (agrupamentos == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("select distinct a from Agrupamento a inner join ")
			  .append("a.eventoAgrupamentoList eal where ")
			  .append("eal.evento.eventoList.size = 0 and ")
			  .append("eal.multiplo = false");
			agrupamentos = EntityUtil.getEntityManager().createQuery(sb.toString())
											 			.getResultList();
		}
		return agrupamentos;
	}

	public void setRegistrados(List<String> registrados) {
		if (this.registrados == null) {
			this.registrados = new ArrayList<Agrupamento>();
		} else {
			this.registrados.clear();
		}
		for (String s : registrados) {
			for (Agrupamento ae : agrupamentos) {
				if (ae.getAgrupamento().equals(s)) {
					this.registrados.add(ae);
					break;
				}
			}
		}
	}

	@Observer(ActionTemplateHandler.SET_CURRENT_TEMPLATE_EVENT)
	public void clearListOnChangeNode() {
		registrados = null;
	}
	
	public List<Agrupamento> getRegistrados() {
		return registrados;
	}
	
}