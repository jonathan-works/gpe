package br.com.infox.core.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.system.EppProperties;

public class SchemaSingleTableEntityPersister extends SingleTableEntityPersister {
	
	public SchemaSingleTableEntityPersister(PersistentClass persistentClass,
			EntityRegionAccessStrategy cacheAccessStrategy, NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
			SessionFactoryImplementor factory, Mapping mapping) throws HibernateException {
		super(convertSchemaPersistentClass(persistentClass, factory), cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
	}
	
	private static PersistentClass convertSchemaPersistentClass(PersistentClass persistentClass, SessionFactoryImplementor factory) {
		String property = EppProperties.getProperty(EppProperties.PROPERTY_TIPO_BANCO_DADOS);
		if ("oracle".equalsIgnoreCase(property)) {
			Table table = persistentClass.getRootTable();
			String schema = table.getSchema();
			String userName = getUsername(factory).toLowerCase();
			table.setSchema(userName.concat("_").concat(schema));
			customizeSequence(persistentClass, factory, userName);
		}
		return persistentClass;
	}
	

    private static String getUsername(SessionFactoryImplementor factory) {
		DataSource datasource = (DataSource) factory.getProperties().get("hibernate.connection.datasource");
		try (Connection connection = datasource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			return databaseMetaData.getUserName();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

    private static void customizeSequence(PersistentClass persistentClass, SessionFactoryImplementor factory, String userName) {
        String rootName = persistentClass.getRootClass().getEntityName();
        IdentifierGenerator identifierGenerator = factory.getIdentifierGenerator( rootName );
        if (identifierGenerator instanceof SequenceGenerator) {
            SequenceGenerator sequenceGenerator = (SequenceGenerator) identifierGenerator;
            String sequenceName = sequenceGenerator.getSequenceName();
            String newSequenceName = userName.concat("_").concat(sequenceName);
            ReflectionsUtil.setValue(sequenceGenerator, "sequenceName", newSequenceName);
            String sql = factory.getDialect().getSequenceNextValString( newSequenceName );
            ReflectionsUtil.setValue(sequenceGenerator, "sql", sql);
        }
    }
}
