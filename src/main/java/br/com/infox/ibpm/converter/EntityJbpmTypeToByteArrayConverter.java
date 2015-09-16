package br.com.infox.ibpm.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import org.jbpm.JbpmException;
import org.jbpm.bytes.ByteArray;
import org.jbpm.context.exe.Converter;

import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.type.EntityJbpmType;
import br.com.infox.seam.util.ComponentUtil;

public class EntityJbpmTypeToByteArrayConverter implements Converter {

	private static final long serialVersionUID = 1L;
	
	public boolean supports(Object value) {
		return value instanceof Serializable || value == null;
	}

	public Object convert(Object o) {
	    if (o == null) return null;
	    EntityJbpmType entityJbpmType = null;
	    try {
	    	entityJbpmType = createEntityJbpmType(o);
	    	ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();
	    	ObjectOutputStream objectStream = new ObjectOutputStream(memoryStream);
	    	objectStream.writeObject(entityJbpmType);
	    	objectStream.flush();
	    	return new ByteArray(memoryStream.toByteArray());
	    }
	    catch (IOException | IllegalAccessException | InvocationTargetException e) {
	    	throw new JbpmException("failed to serialize: " + entityJbpmType, e);
	    }
	}

	public Object revert(Object o) {
	    if (o == null) return o;
	    ByteArray byteArray = (ByteArray) o;
	    InputStream memoryStream = new ByteArrayInputStream(byteArray.getBytes());
	    try {
	    	ObjectInputStream objectStream = new ObjectInputStream(memoryStream);
	    	EntityJbpmType ret = (EntityJbpmType) objectStream.readObject();
			objectStream.close();
	    	return getEntityFromDatabase(ret);
	    }
	    catch (IOException e) {
	    	throw new JbpmException("failed to deserialize object", e);
	    }
	    catch (ClassNotFoundException e) {
	    	throw new JbpmException("serialized class not found", e);
	    }
	}
	
	private EntityJbpmType createEntityJbpmType(Object o) throws IllegalAccessException, InvocationTargetException {
		Object id = EntityUtil.getIdValue(o);
    	EntityJbpmType entityJbpmType = new EntityJbpmType();
    	entityJbpmType.setId(id);
    	entityJbpmType.setEntity(o.getClass());
    	return entityJbpmType;
	}
	
	private Object getEntityFromDatabase(EntityJbpmType jbpmType){
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		return entityManager.find(jbpmType.getClass(), jbpmType.getId());
	}

}
