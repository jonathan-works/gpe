package br.com.infox.epp.system;

import org.quartz.impl.jdbcjobstore.oracle.OracleDelegate;

import br.com.infox.hibernate.oracle.dialect.InfoxOracleDialect;

public class OracleDatabase extends AbstractDatabase {

    @Override
    public String getHibernateDialect() {
        return InfoxOracleDialect.class.getName();
    }

    @Override
    public String getQuartzDelegate() {
        return OracleDelegate.class.getName();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.Oracle;
    }

}
