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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.EventoAgrupamento;
import br.com.infox.ibpm.entity.ProcessoEvento;
import br.com.infox.ibpm.entity.TarefaTransicaoEvento;
import br.com.infox.ibpm.entity.TarefaTransicaoEventoAgrupamento;
import br.com.infox.ibpm.jbpm.ActionTemplate;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.itx.util.EntityUtil;

@Name("verificaEventoAction")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Startup
public class VerificaEventoAction extends ActionTemplate {

	public static final String VERIFICA_EVENTO_EXPRESSION = "verificaEventoAction.canTransit";
	private static final long serialVersionUID = 1L;
	private List<Agrupamento> registrados = new ArrayList<Agrupamento>();
	private List<Agrupamento> agrupamentos;
	private Boolean andCondition = Boolean.TRUE;
	private Boolean notCondition = Boolean.FALSE;
	private String createdExpression;
	
	@Override
	public String getExpression() {
		return VERIFICA_EVENTO_EXPRESSION;
	}

	@Override
	public String getFileName() {
		return "verificaEvento.xhtml";
	}

	@Override
	public String getLabel() {
		return "Verifica se um evento já foi registrado no processo";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void extractParameters(String expression) {
		if (expression == null || expression.equals("")) {
			registrados = new ArrayList<Agrupamento>();
			return;
		}
		String[] params = getExpressionParameters(expression);
		List<Integer> ids = new ArrayList<Integer>();
		for (String p : params) {
			try { 
				ids.add(Integer.parseInt(p));
			} catch(NumberFormatException nfe) {
				
			}
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
			if (notCondition) {
				sb.append("!");
			}
			sb.append(getExpression()).append("(");
			sb.append(andCondition);
			sb.append(",");
			for (Agrupamento ae : registrados) {
				if (! sb.toString().endsWith(",")) {
					sb.append(",");
				}
				sb.append("'");
				sb.append(ae.getIdAgrupamento());
				sb.append("'");
			}
			sb.append(")}");
			createdExpression = sb.toString();
		}
	}
	
	/**
	 * É necessário manter esse método para que a consistência das versões
	 * anteriores do projeto seja mantida e não de erros. Não apagar!
	 * 
	 * @param eventos
	 * @return
	 */
	public boolean processoContemEvento(String... eventos) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canTransit(boolean isAndCondition, String... agrupamentos) {
		String q = "select pe from ProcessoEvento pe where pe.processo = :processo";
		Query query = EntityUtil.getEntityManager().createQuery(q);
		query.setParameter("processo", JbpmUtil.getProcesso());
		List<ProcessoEvento> peList = query.getResultList();
		for (String id : agrupamentos) {
			Agrupamento agrup = EntityUtil.find(Agrupamento.class, Integer.parseInt(id));
			eventos: 
			for (EventoAgrupamento ea : agrup.getEventoAgrupamentoList()) {
				for (ProcessoEvento pe : peList) {
					if (pe.getEvento().getCaminhoCompleto().startsWith(
									ea.getEvento().getCaminhoCompleto())) {
						if (isAndCondition) {
							continue eventos;
						} else {
							return true;
						}
					}
				}
				return false;
			}
		}
		return false;
	}
	
	public boolean verificarEventos(long idTarefaOrigem, long idTarefaDestino) {
		return verificarEventos(idTarefaOrigem, idTarefaDestino, true);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Método que irá retornar uma se a transição já esta disponível, o select 
	 * em TarefaTransiçãoEvento verifica se existe registrado algum agrupamento a 
	 * ser verificado para informar se a transição será ou não colocada na lista 
	 * de possíveis transições. Porém os loops tornam-se um pouco complexos pois 
	 * para cada transição existe diversos agrupamentos de eventos e, 
	 * a transição estará habilitada somente se houver para cada agrupamento, ao menos
	 * um, filho (Evento) folha registrado na tabela ProcessoEvento o processo a ser
	 * trasitado, caso contrário não será possível efetuar a transição para a tarefa 
	 * em questão.
	 * @param idTarefaOrigem From da Transição, nodeType = Task.
	 * @param idTarefaDestino To da Transição, nodeType = Task.
	 * @return True se o processo já contiver os eventos necessários para a transição.
	 */
	public boolean verificarEventos(long idTarefaOrigem, long idTarefaDestino, boolean isAndCondition) {
		StringBuilder sb = new StringBuilder();
		sb.append("select t from TarefaTransicaoEvento t where ")
		  .append("t.tarefaOrigem.idTarefa = :tarefaOrigem and ")
		  .append("t.tarefaDestino.idTarefa = :tarefaDestino");
		Query q = EntityUtil.getEntityManager().createQuery(sb.toString());
		q.setParameter("tarefaOrigem", (int) idTarefaOrigem);
		q.setParameter("tarefaDestino", (int) idTarefaDestino);
		TarefaTransicaoEvento tte = EntityUtil.getSingleResult(q);
		if (tte == null) {
			return true;
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("select pe from ProcessoEvento pe where pe.processo = :processo");
			Query query = EntityUtil.getEntityManager().createQuery(builder.toString());
			query.setParameter("processo", JbpmUtil.getProcesso());
			List<ProcessoEvento> peList = query.getResultList();
			if (peList != null && peList.size() > 0) {
				for (TarefaTransicaoEventoAgrupamento ttea : tte.getTarefaTransicaoEventoAgrupamentoList()) {
					eventos: 
					for (EventoAgrupamento ea : ttea.getAgrupamento().getEventoAgrupamentoList()) {
						for (ProcessoEvento pe : peList) {
							if (pe.getEvento().getCaminhoCompleto().startsWith(
											ea.getEvento().getCaminhoCompleto())) {
								if (isAndCondition) {
									continue eventos;
								} else {
									return true;
								}
							}
						}
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}

	public void setAndCondition(Boolean andCondition) {
		this.andCondition = andCondition;
	}

	public Boolean getAndCondition() {
		return andCondition;
	}

	public void setNotCondition(Boolean notCondition) {
		this.notCondition = notCondition;
	}

	public Boolean getNotCondition() {
		return notCondition;
	}

	public List<Agrupamento> getAgrupamentos() {
		if(agrupamentos == null) {
			 agrupamentos = EntityUtil.getEntityList(Agrupamento.class);
			 createdExpression = ProcessBuilder.instance().getCurrentTransition().getCondition();
			 extractParameters(createdExpression);
		}
		return agrupamentos;
	}
	
	public void setAgrupamentos(List<Agrupamento> agrupamentos) {
		this.agrupamentos = agrupamentos;
	}
	
	public List<Agrupamento> getRegistrados() {
		return registrados;
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

	public void setCreatedExpression(String createdExpression) {
		this.createdExpression = createdExpression;
	}

	public String getCreatedExpression() {
		return createdExpression;
	}
	
}