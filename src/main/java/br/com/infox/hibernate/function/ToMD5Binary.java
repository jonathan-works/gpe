package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BinaryType;
import org.hibernate.type.Type;

import br.com.infox.hibernate.oracle.dialect.InfoxOracleDialect;
import br.com.infox.hibernate.postgres.dialect.InfoxPostgreSQLDialect;
import br.com.infox.hibernate.sqlserver.dialect.InfoxSQLServer2012Dialect;

public class ToMD5Binary implements SQLFunction {

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
        return BinaryType.INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
        if (arguments.isEmpty()) {
            throw new QueryException("É necessário valor a ser convertido");
        }
        Dialect dialect = factory.getDialect();    
        if (dialect instanceof InfoxSQLServer2012Dialect) {
            return " HASHBYTES('MD5', convert(varbinary, " + arguments.get(0) + " )) ";
        } else if (dialect instanceof InfoxOracleDialect) {
            return " DBMS_CRYPTO.HASH( " + arguments.get(0) + " , 2 ) "; // 2 indica o algoritmo MD5
        } else if (dialect instanceof InfoxPostgreSQLDialect) {
            return " convert_to(md5( " + arguments.get(0) + " ), 'UTF-8') ";
        } else {
            throw new QueryException("Database type not supported");
        }
    }

}
