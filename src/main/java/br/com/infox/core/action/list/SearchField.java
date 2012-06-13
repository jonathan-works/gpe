package br.com.infox.core.action.list;

import java.text.MessageFormat;

public class SearchField {
	
	private String fieldName;
	private SearchCriteria criteria;
	private String expression;
	private String entityListName;

	public SearchField(String entityListName, String fieldName, 
					   SearchCriteria criteria, String expression) {
		this.fieldName = fieldName;
		this.criteria = criteria;
		this.expression = expression;
		this.entityListName = entityListName;
	}

	public SearchField(String entityListName, String name, SearchCriteria criteria) {
		this(entityListName, name, criteria, null);
	}

	public String getName() {
		return fieldName;
	}

	public void setName(String name) {
		this.fieldName = name;
	}

	public SearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	public String getExpression() {
		if (expression == null) {
			entityListName = entityListName.substring(0, 1).toLowerCase() + entityListName.substring(1);
			expression = MessageFormat.format(criteria.getPattern(), fieldName, entityListName);
		}
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}
