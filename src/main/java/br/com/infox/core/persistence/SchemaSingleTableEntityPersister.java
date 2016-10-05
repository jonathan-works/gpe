package br.com.infox.core.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Iterator;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.metamodel.binding.EntityBinding;
import org.hibernate.persister.entity.SingleTableEntityPersister;

import br.com.infox.core.util.ReflectionsUtil;
import br.com.infox.epp.system.Configuration;
import br.com.infox.epp.system.Database.DatabaseType;

public class SchemaSingleTableEntityPersister extends SingleTableEntityPersister {
	
	public SchemaSingleTableEntityPersister(PersistentClass persistentClass,
			EntityRegionAccessStrategy cacheAccessStrategy, NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
			SessionFactoryImplementor factory, Mapping mapping) throws HibernateException {
		super(convertSchemaPersistentClass(persistentClass, factory), cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
	}
	
	public SchemaSingleTableEntityPersister(EntityBinding entityBinding, EntityRegionAccessStrategy cacheAccessStrategy,
            NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy, SessionFactoryImplementor factory,
            Mapping mapping) throws HibernateException {
	    super(entityBinding, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
	}
	
	@SuppressWarnings("unchecked")
    private static PersistentClass convertSchemaPersistentClass(PersistentClass persistentClass, SessionFactoryImplementor factory) {
		DatabaseType databaseType = Configuration.getInstance().getDatabase().getDatabaseType();
		if (databaseType.equals(DatabaseType.Oracle)) {
			String userName = getUsername(factory).toLowerCase();
			updateSchema(persistentClass.getRootTable(), userName);
			customizeSequence(persistentClass, factory, userName);
			Iterator<Property> iterator = persistentClass.getDeclaredPropertyIterator();
			while (iterator.hasNext()) {
			    Property next = iterator.next();
			    Value value = next.getValue();
			    if (value instanceof Collection) {
			        Collection collection = (Collection) value;
			        updateSchema(collection.getCollectionTable(), userName);
			    }
			}
		}
		return persistentClass;
	}
	
	private static void updateSchema(Table table, String username) {
	    String schema = table.getSchema();
	    if (schema != null) {
	        table.setSchema(username.concat("_").concat(schema));
	    }
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
