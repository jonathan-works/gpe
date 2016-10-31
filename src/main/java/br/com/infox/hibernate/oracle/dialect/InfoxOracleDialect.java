package br.com.infox.hibernate.oracle.dialect;

import java.sql.Types;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

import br.com.infox.hibernate.function.CustomSqlFunctions;
import br.com.infox.hibernate.function.DataUtilAdd;
import br.com.infox.hibernate.function.DateDiffDayOracle;
import br.com.infox.hibernate.function.NumeroProcessoRoot;
import br.com.infox.hibernate.function.ToDateJpql;
import br.com.infox.hibernate.function.ToMD5Binary;

public class InfoxOracleDialect extends Oracle10gDialect {

    public InfoxOracleDialect() {
        registerFunction(CustomSqlFunctions.NUMERO_PROCESSO_ROOT, new NumeroProcessoRoot());
        registerFunction(CustomSqlFunctions.DATA_UTIL_ADD, new DataUtilAdd());
        registerFunction(CustomSqlFunctions.DATE_DIFF_DAY, new DateDiffDayOracle());
        registerFunction(CustomSqlFunctions.TO_DATE, new ToDateJpql());
        registerFunction(CustomSqlFunctions.MD5_BINARY, new ToMD5Binary());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
    }
}
