package br.com.infox.epp.system;

import org.quartz.impl.jdbcjobstore.PostgreSQLDelegate;

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

}
