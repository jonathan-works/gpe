package br.com.infox.hibernate.postgres.dialect;

import org.hibernate.dialect.PostgreSQL82Dialect;

public class InfoxPostgreSQLDialect extends PostgreSQL82Dialect {

    public InfoxPostgreSQLDialect() {
        registerKeyword("true");
        registerKeyword("false");
    }

}
