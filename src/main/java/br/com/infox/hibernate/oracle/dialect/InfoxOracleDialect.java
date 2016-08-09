package br.com.infox.hibernate.oracle.dialect;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

import br.com.infox.hibernate.function.DataUtilAdd;
import br.com.infox.hibernate.function.DateDiffDayOracle;
import br.com.infox.hibernate.function.NumeroProcessoRoot;
import br.com.infox.hibernate.function.ToDateJpql;

public class InfoxOracleDialect extends Oracle10gDialect {

    public InfoxOracleDialect() {
        registerFunction("NumeroProcessoRoot", new NumeroProcessoRoot());
        registerFunction("DataUtilAdd", new DataUtilAdd());
        registerFunction("DateDiffDay", new DateDiffDayOracle());
        registerFunction("to_date", new ToDateJpql());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
    }
}
