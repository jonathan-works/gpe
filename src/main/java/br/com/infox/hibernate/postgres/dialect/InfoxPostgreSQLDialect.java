package br.com.infox.hibernate.postgres.dialect;

import org.hibernate.dialect.PostgreSQL82Dialect;

import br.com.infox.hibernate.function.DataUtilAdd;
import br.com.infox.hibernate.function.DocumentoSuficientementeAssinado;
import br.com.infox.hibernate.function.NumeroProcessoRoot;

public class InfoxPostgreSQLDialect extends PostgreSQL82Dialect {

    public InfoxPostgreSQLDialect() {
        registerKeyword("true");
        registerKeyword("false");
        registerFunction("DocumentoSuficientementeAssinado", new DocumentoSuficientementeAssinado());
        registerFunction("NumeroProcessoRoot", new NumeroProcessoRoot());
        registerFunction("DataUtilAdd", new DataUtilAdd());
    }

}
