package br.com.infox.hibernate.postgres.dialect;

import org.hibernate.dialect.PostgresPlusDialect;

import br.com.infox.hibernate.function.DataUtilAdd;
import br.com.infox.hibernate.function.DateDiffDayPostgresSQL;
import br.com.infox.hibernate.function.DocumentoSuficientementeAssinado;
import br.com.infox.hibernate.function.NumeroProcessoRoot;
import br.com.infox.hibernate.function.ToDateJpql;

public class InfoxPostgreSQLDialect extends PostgresPlusDialect {

    public InfoxPostgreSQLDialect() {
        registerKeyword("true");
        registerKeyword("false");
        registerFunction("DocumentoSuficientementeAssinado", new DocumentoSuficientementeAssinado());
        registerFunction("NumeroProcessoRoot", new NumeroProcessoRoot());
        registerFunction("DataUtilAdd", new DataUtilAdd());
        registerFunction("DateDiffDay", new DateDiffDayPostgresSQL());
        registerFunction("to_date", new ToDateJpql());
    }

}
