package br.com.infox.epp.system;

import org.quartz.impl.jdbcjobstore.MSSQLDelegate;

import br.com.infox.hibernate.sqlserver.dialect.InfoxSQLServer2012Dialect;

public class SqlServerDatabase extends AbstractDatabase {

    @Override
    public String getHibernateDialect() {
        return InfoxSQLServer2012Dialect.class.getName();
    }

    @Override
    public String getQuartzDialect() {
        return MSSQLDelegate.class.getName();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SQLServer;
    }

}
