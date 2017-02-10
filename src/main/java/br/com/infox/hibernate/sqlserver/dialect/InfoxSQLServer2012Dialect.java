package br.com.infox.hibernate.sqlserver.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.type.StandardBasicTypes;

import br.com.infox.hibernate.function.CustomSqlFunctions;
import br.com.infox.hibernate.function.DataAdd;
import br.com.infox.hibernate.function.DataUtilAdd;
import br.com.infox.hibernate.function.DateDiffDaySQLServer;
import br.com.infox.hibernate.function.DocumentoSuficientementeAssinado;
import br.com.infox.hibernate.function.NumeroProcessoRoot;
import br.com.infox.hibernate.function.StringAgg;
import br.com.infox.hibernate.function.ToDateJpql;
import br.com.infox.hibernate.function.ToMD5Binary;
import br.com.infox.hibernate.function.ToTimeSQLServer;

public class InfoxSQLServer2012Dialect extends SQLServer2012Dialect {
    
    public InfoxSQLServer2012Dialect() {
        registerHibernateType(Types.NCHAR, StandardBasicTypes.STRING.getName());
        registerFunction(CustomSqlFunctions.DOCUMENTO_SUFICIENTEMENTE_ASSINADO, new DocumentoSuficientementeAssinado());
        registerFunction(CustomSqlFunctions.NUMERO_PROCESSO_ROOT, new NumeroProcessoRoot());
        registerFunction(CustomSqlFunctions.TO_DATE, new ToDateJpql());
        registerFunction(CustomSqlFunctions.DATA_ADD, new DataAdd());
        registerFunction(CustomSqlFunctions.DATA_UTIL_ADD, new DataUtilAdd());
        registerFunction(CustomSqlFunctions.DATE_DIFF_DAY, new DateDiffDaySQLServer());
        registerFunction(CustomSqlFunctions.TO_TIME, new ToTimeSQLServer());
        registerFunction(CustomSqlFunctions.MD5_BINARY, new ToMD5Binary());
        registerFunction(CustomSqlFunctions.STRING_AGG, new StringAgg());
    }
}
