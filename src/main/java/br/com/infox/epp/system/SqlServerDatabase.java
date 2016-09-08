package br.com.infox.epp.system;

import java.util.Properties;

import org.quartz.impl.jdbcjobstore.MSSQLDelegate;

import br.com.infox.hibernate.sqlserver.dialect.InfoxSQLServer2012Dialect;

public class SqlServerDatabase extends AbstractDatabase {

    @Override
    public String getHibernateDialect() {
        return InfoxSQLServer2012Dialect.class.getName();
    }

    @Override
    public String getQuartzDelegate() {
        return MSSQLDelegate.class.getName();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SQLServer;
    }
    
    @Override
    public void performQuartzProperties(Properties properties) {
        super.performQuartzProperties(properties);
        properties.put("org.quartz.jobStore.selectWithLockSQL", "SELECT LOCK_NAME FROM {0}LOCKS WITH (ROWLOCK, UPDLOCK) WHERE SCHED_NAME = {1} AND LOCK_NAME=?");
        properties.put("org.quartz.jobStore.lockHandler.class", "org.quartz.impl.jdbcjobstore.UpdateLockRowSemaphore");
    }

}
