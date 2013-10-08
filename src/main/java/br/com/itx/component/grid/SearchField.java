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
package br.com.itx.component.grid;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.international.Messages;

public class SearchField implements Serializable {
	
	private static final int DEFAULT_MAX_RESULTS = 100;

    private static final long serialVersionUID = 1L;

	private String id;

	private String label;

	private String type;

	private Object value;

	private boolean required;

	private Map<String, Object> properties = new LinkedHashMap<String, Object>();

	private GridQuery<?> grid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		if (label == null) {
			String key = grid.getGridId() + "." + id;
			Map<String, String> msg = Messages.instance();
			if (msg != null && msg.containsKey(key)) {
				label =  msg.get(key);
			} else {
				label = id;
			}
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	private String getQueryExpression() {
		if (properties.containsKey("queryExpression")) {
			return (String) properties.get("queryExpression");
		}
		String count = "count(o.{0})";
		Boolean hideCount = (Boolean) properties.get("hideCount"); 
		if (hideCount != null && hideCount.booleanValue()) {
			count = "''' '''";
		}

		String order = "1";
		Boolean isRanking = (Boolean) properties.get("ranking"); 
		if (isRanking != null && isRanking.booleanValue()) {
			order = "2 desc";
		}
		String entity = grid.getEntity().getClass().getSimpleName();
		String pattern = "select o.{0} as name, " + count +
				" as qtd from {1} o "
			+ "where lower({0}) like concat(lower(:input), '''%''') " +
					"group by o.{0} order by " + order;
		return MessageFormat.format(pattern, id, entity);
	}
	
	public List<?> suggest(Object typed) {
		String s = getQueryExpression();
		Query query = grid.getEntityManager().createQuery(s);
		query.setParameter("input", typed);
		query.setMaxResults(DEFAULT_MAX_RESULTS);
		return query.getResultList();
	}

	public void setGrid(GridQuery<?> grid) {
		this.grid = grid;
	}
}