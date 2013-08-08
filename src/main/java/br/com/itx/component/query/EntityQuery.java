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
package br.com.itx.component.query;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.component.MeasureTime;
import br.com.itx.component.Util;
import br.com.itx.component.grid.GridQuery;

public class EntityQuery extends org.jboss.seam.framework.EntityQuery {
	
	private static final LogProvider LOG = Logging.getLogProvider(EntityQuery.class);

	private static final long serialVersionUID = 1L;
	
	private List<String> conditions = new ArrayList<String>();
	
	private String persistenceContextName;
	
	private String countEjbql;

	private String beforeResultEvent;	
	
	private List fullList;

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}
	
	@Override
	public String getPersistenceContextName() {
		if (persistenceContextName == null) {
			return super.getPersistenceContextName();
		}	
		return persistenceContextName;
	}

	public void setPersistenceContextName(String persistenceContextName) {
		this.persistenceContextName = persistenceContextName;
	}

	@Override
	public String getCountEjbql() {
		if (countEjbql == null) {
			return super.getCountEjbql();
		}
		return countEjbql;
	}

	public void setCountEjbql(String countEjbql) {
		this.countEjbql = countEjbql;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List getResultList() {
		if (!checkConditions()) {
			return null;
		}
		MeasureTime mt = new MeasureTime(true);
		if(beforeResultEvent != null && !"".equals(beforeResultEvent)) {
			Events.instance().raiseEvent(beforeResultEvent);
		}
		List resultListSuper = super.getResultList();
		mt.stop();
		if (this instanceof GridQuery) {
			GridQuery grid = (GridQuery) this;
			LOG.info("Grid: " + grid.getGridId() + " - " + getEjbql() + " (" +
					resultListSuper.size() +
					" registros): " + mt.getTime());					
		} else {
			LOG.info("getResultList(): " + getEjbql()  + " (" +
					resultListSuper.size() +
					" registros): " + mt.getTime());			
		}
		return resultListSuper;
	}
	
	@Override
	public DataModel getDataModel() {
		MeasureTime mt = new MeasureTime(true);
		DataModel dataModel = super.getDataModel();
		mt.stop();
		LOG.info("getDataModel(): " + mt.getTime());			
		return dataModel;
	}
	
	@SuppressWarnings("unchecked")
	public List getFullList() {
		if (fullList != null) {
			return fullList;
		}
		Integer firstResult = getFirstResult();
		Integer maxResults = getMaxResults();
		setFirstResult(null);
		setMaxResults(null);
		fullList = getResultList();
		setFirstResult(firstResult);
		setMaxResults(maxResults);
		return fullList;
	}
	
	@Override
	public Object getSingleResult() {
		if (!checkConditions()){
			return null;
		}
		return super.getSingleResult();
	}
		
	@Override
	public Long getResultCount() {
		if (!checkConditions()){
			return 0L;
		}
		return super.getResultCount();
	}
	
	private boolean checkConditions() {
		if (! conditions.isEmpty()) {
			for (String s : conditions) {
				Boolean condition = new Util().eval(s);
				if (!condition) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public Integer getFirstResult() {
		Integer i = super.getFirstResult();
		return (i == null ? 0 : i);
	}

	public String getBeforeResultEvent() {
		return beforeResultEvent;
	}

	public void setBeforeResultEvent(String beforeResultEvent) {
		this.beforeResultEvent = beforeResultEvent;
	}
	
}