package br.com.infox.epp.view;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.reflections.Reflections;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.hibernate.util.HibernateUtil;

@Startup
@Singleton
public class ViewLoader implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ViewLoader.class.getName());
	
	@PostConstruct
	public void init() {
		Reflections reflections = new Reflections("br.com.infox");
		Set<Class<?>> entityViews = reflections.getTypesAnnotatedWith(View.class);
		String serverName = getViewServerName();
		String databseName = getDatabaseName();
		Field fieldTableName = getFieldTableName();
		SessionFactoryImpl sessionFactoryImpl = HibernateUtil.getSessionFactoryImpl();
		for (Class<?> entity : entityViews) {
			SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sessionFactoryImpl.getEntityPersister(entity.getName());
			addViewName(entityPersister, fieldTableName, serverName, databseName);
		}
	}
	
	private void addViewName(SingleTableEntityPersister entityPersister, Field fieldTable, String serverName, String databaseName) {
		String tableName = entityPersister.getTableName();
		if (StringUtil.isEmpty(serverName)) {
			tableName = databaseName + "." + tableName;
		} else {
			tableName = serverName + "." + databaseName + "." + tableName;
		}
		String[] qualifiedTableNames = {tableName};
		try {
			fieldTable.set(entityPersister, qualifiedTableNames);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "addViewName", e);
		}
	}
	
	private String getViewServerName() {
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		String hql = "select p.valorVariavel from Parametro p where p.nomeVariavel = 'etceUnidadeGestoraDatabaseServverName'";
		TypedQuery<String> typedQuery = entityManager.createQuery(hql, String.class).setMaxResults(1);
		List<String> result = typedQuery.getResultList();
		return result.isEmpty() ? "" : result.get(0);
	}
	
	private String getDatabaseName() {
		try {
			DataSource dataSource = JNDI.lookup("java:jboss/datasources/ETCE_UnidadeGestoraDS");
			Connection connection = dataSource.getConnection();
	        Matcher matcher = Pattern.compile("databaseName=([\\w]*)\\W").matcher(connection.getMetaData().getURL());
	        String url = "";
	        if (matcher.find()) {
	        	url = matcher.group(1);
	        }
	        connection.close();
	        return url;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "getDatabaseName", e);
		}
		return null;
	}
	
	private Field getFieldTableName() {
		try {
			Field field = SingleTableEntityPersister.class.getDeclaredField("qualifiedTableNames");
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.log(Level.SEVERE, "getFieldTableName", e);
		}
		return null;
	}
	
}
