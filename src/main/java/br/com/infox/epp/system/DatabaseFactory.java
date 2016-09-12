package br.com.infox.epp.system;

import br.com.infox.epp.system.Database.DatabaseType;

public final class DatabaseFactory {

    public static Database create(String productName) {
        DatabaseType databaseType = DatabaseType.fromProductName(productName);
        if (DatabaseType.Oracle.equals(databaseType)) {
            return new OracleDatabase();
        } else if (DatabaseType.PostgreSQL.equals(databaseType)) {
            return new PostgresqlDatabase();
        } else if (DatabaseType.SQLServer.equals(databaseType)) {
            return new SqlServerDatabase();
        } else {
            throw new IllegalArgumentException("Unknow database type '" + productName + "' ");
        }
    }
}
