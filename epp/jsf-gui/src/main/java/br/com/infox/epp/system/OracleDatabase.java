package br.com.infox.epp.system;

import java.util.UUID;

import org.hibernate.type.SingleColumnType;
import org.hibernate.type.UUIDBinaryType;
import org.quartz.impl.jdbcjobstore.oracle.OracleDelegate;

import br.com.infox.core.persistence.DatabaseErrorCodeAdapter;
import br.com.infox.core.persistence.OracleErrorAdaptor;
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

    @Override
    public DatabaseErrorCodeAdapter getErrorCodeAdapter() {
        return OracleErrorAdaptor.INSTANCE;
    }

    @Override
    public SingleColumnType<UUID> getUUIDType() {
        return  UUIDBinaryType.INSTANCE;
    }

}
