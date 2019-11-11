package br.com.infox.epp.system;

import java.util.UUID;

import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.SingleColumnType;
import org.quartz.impl.jdbcjobstore.PostgreSQLDelegate;

import br.com.infox.core.persistence.DatabaseErrorCodeAdapter;
import br.com.infox.core.persistence.PostgreSQLErrorCodeAdaptor;
import br.com.infox.hibernate.postgres.dialect.InfoxPostgreSQLDialect;

public class PostgresqlDatabase extends AbstractDatabase {

    @Override
    public String getHibernateDialect() {
        return InfoxPostgreSQLDialect.class.getName();
    }

    @Override
    public String getQuartzDelegate() {
        return PostgreSQLDelegate.class.getName();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.PostgreSQL;
    }

    @Override
    public DatabaseErrorCodeAdapter getErrorCodeAdapter() {
        return PostgreSQLErrorCodeAdaptor.INSTANCE;
    }

    @Override
    public SingleColumnType<UUID> getUUIDType() {
        return PostgresUUIDType.INSTANCE;
    }

}
