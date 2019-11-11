package br.com.infox.ibpm.converter;

import javax.persistence.EntityManager;

import org.jbpm.context.exe.Converter;
import org.jbpm.context.exe.variableinstance.JpaInstance.JpaValue;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.hibernate.util.HibernateUtil;

public class JpaConverter implements Converter {

    private static final long serialVersionUID = 1L;

    public boolean supports(Object value) {
        return  value != null;
    }

    public Object convert(Object o) {
        return createJpaValue(o);
    }

    public Object revert(Object o) {
        JpaValue jpaValue = (JpaValue) o;
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        Object typedId = ReflectionsUtil.newInstance(jpaValue.getIdType(), String.class, jpaValue.getIdValue());
        return entityManager.find(jpaValue.getType(), typedId);
    }

    private JpaValue createJpaValue(Object o) {
        Object id = EntityManagerProducer.instance().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(o);
        return new JpaValue(id.getClass(), id.toString(), HibernateUtil.removeProxy(o).getClass());
    }

}
