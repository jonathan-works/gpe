package br.com.infox.ibpm.type;

import java.io.Serializable;

public class EntityJbpmType implements Serializable {

	private static final long serialVersionUID = 3479367316635984449L;

	private Object id;
	
	private Class<?> entity;

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public Class<?> getEntity() {
		return entity;
	}

	public void setEntity(Class<?> entity) {
		this.entity = entity;
	}
	
}
