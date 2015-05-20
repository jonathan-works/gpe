package br.com.infox.core.persistence.generator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.hibernate.dialect.Dialect;

import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public abstract class CustomIdGenerator<T> {
	
	private static final LogProvider LOG = Logging.getLogProvider(CustomIdGenerator.class);
	
	public abstract T nextValue() throws PersistenceException;
	
	public static CustomIdGenerator<Long> create(final String sequenceName) {
		return new CustomIdGenerator<Long>() {
			@Override
			public Long nextValue() throws PersistenceException {
				Dialect dialect = HibernateUtil.getDialect();
				String sql = dialect.getSequenceNextValString(sequenceName);
				Connection connection = HibernateUtil.getConnection(getEntityManager());
				try {
					Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = st.executeQuery(sql);
					if (rs.next()) {
						Long id = rs.getLong(1);
						return id;
					}
				} catch (SQLException e) {
					LOG.error("nextValue()", e);
					throw new PersistenceException(e);
				}
				return null;
			}
		};
	}
	
	protected EntityManager getEntityManager() {
		try {//java:jboss/EPAPersistenceUnit
			EntityManagerFactory emf = (EntityManagerFactory) new InitialContext().lookup("java:jboss/EPAPersistenceUnit");
			return emf.createEntityManager();
		} catch (NamingException e) {
			LOG.error("getEntityManager", e);
		}
		return null;
	}
}
