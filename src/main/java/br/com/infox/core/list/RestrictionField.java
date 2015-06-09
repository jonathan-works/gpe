package br.com.infox.core.list;

import java.io.Serializable;
import java.text.MessageFormat;

public class RestrictionField implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fieldName;
	private RestrictionType restriction;
	private String expression;
	private String entityListName;

	private RestrictionField(String entityListName, String fieldName, RestrictionType restriction, String expression) {
		this.fieldName = fieldName;
		this.restriction = restriction;
		this.expression = expression;
		this.entityListName = entityListName;
	}

	public RestrictionField(String entityListName, String name, String compareField, RestrictionType restriction) {
		this(entityListName, name, restriction, null);
		setExpression(getExpression(name, compareField));
	}

	public RestrictionField(String entityListName, String name, RestrictionType restriction) {
		this(entityListName, name, restriction, null);
		setExpression(getExpression(name));
	}

	public RestrictionField(String entityListName, String name, String expression) {
		this(entityListName, name, null, expression);
	}

	public String getName() {
		return fieldName;
	}

	public void setName(String name) {
		this.fieldName = name;
	}

	public RestrictionType getRestriction() {
		return restriction;
	}

	public void setRestriction(RestrictionType restriction) {
		this.restriction = restriction;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	private String getExpression(String name) {
		return MessageFormat.format(restriction.getPattern(), "o.".concat(name), entityListName, name);
	}

	private String getExpression(String name, String compareField) {
		return MessageFormat.format(restriction.getPattern(), compareField, entityListName, name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((restriction == null) ? 0 : restriction.hashCode());
		result = prime * result + ((entityListName == null) ? 0 : entityListName.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestrictionField other = (RestrictionField) obj;
		if (restriction != other.restriction)
			return false;
		if (entityListName == null) {
			if (other.entityListName != null)
				return false;
		} else if (!entityListName.equals(other.entityListName))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

}
