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
		//A classe AbstractSuggestBean faz o warp deste m�todo, dessa forma as implementa��es dessa classe
		//s� precisam sobre-escrever esse m�todo se forem de fato realizar alguma a��o.
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