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
package br.com.infox.component.suggest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.util.StopWatch;

import br.com.itx.util.EntityUtil;


@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public abstract class AbstractSuggestBean<E> implements SuggestBean<E>, Serializable {

	private static final int LIMIT_SUGGEST_DEFAULT = 15;

	private static final LogProvider LOG = Logging.getLogProvider(AbstractSuggestBean.class);
	
	private static final long serialVersionUID = 1L;

	protected static final String INPUT_PARAMETER = "input";
	
	private E instance;

	private String expression;
	
	@Override
	public List<E> suggestList(Object typed) {
		StopWatch sw = new StopWatch(true);
		List<E> result = null;
		if (getEjbql() != null) {
			Query query = EntityUtil.getEntityManager().createQuery(getEjbql())
				.setParameter(INPUT_PARAMETER, typed);
			if (getLimitSuggest() != null) {
				query.setMaxResults(getLimitSuggest());
			}
			result = query.getResultList();
		} else {
			result = new ArrayList<E>();
		}
		LOG.info("suggestList(" + typed + ") :" + sw.getTime());
		return result;
	}
	
	@Override
	public E getInstance() {
		if (expression == null) {
			return instance;
		}
		return (E) Expressions.instance().createValueExpression(expression).getValue();
	}
	
	@Override
	public void setInstance(E instance) {
		if (expression == null) {
			this.instance = instance;
		} else {
			Expressions.instance().createValueExpression(expression).setValue(instance);
		}
		Events.instance().raiseEvent(getEventSelected(), instance);
	}	

	protected String getEventSelected() {
		return null;
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = "#{" + expression + "}";
	}	

	@Override
	public void setDefaultValue(String obj) {
		//A classe AbstractSuggestBean faz o warp deste método, dessa forma as implementações dessa classe
		//só precisam sobre-escrever esse método se forem de fato realizar alguma ação.
	}
	
	@Override
	public String getDefaultValue() {
		return getInstance() != null ? getInstance().toString() : "";
	}
	
	/**
	 * Metodo que devolve o limite para o resultado do suggest. Caso queira retirar esse limite
	 * basta sobrescrever este metodo retornando null.
	 * @return
	 */
	public Integer getLimitSuggest() {
		return LIMIT_SUGGEST_DEFAULT;
	}
	
	
}