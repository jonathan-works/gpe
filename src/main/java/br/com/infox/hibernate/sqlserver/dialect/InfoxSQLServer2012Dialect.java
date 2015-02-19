package br.com.infox.hibernate.sqlserver.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.type.StandardBasicTypes;

public class InfoxSQLServer2012Dialect extends SQLServer2012Dialect {
    public InfoxSQLServer2012Dialect() {
        registerHibernateType(Types.NCHAR, StandardBasicTypes.STRING.getName());
        registerFunction("DocumentoSuficientementeAssinado", new DocumentoSuficientementeAssinado());
    }
}
