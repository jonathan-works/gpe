package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.com.infox.hibernate.oracle.dialect.InfoxOracleDialect;
import br.com.infox.hibernate.postgres.dialect.InfoxPostgreSQLDialect;
import br.com.infox.hibernate.sqlserver.dialect.InfoxSQLServer2012Dialect;

public class DateToChar implements SQLFunction {
    
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
        return StringType.INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
        if (arguments.size() != 1) {
            throw new QueryException("É necessário um argumento, (data)");
        }
        Dialect dialect = factory.getDialect();    
        if (dialect instanceof InfoxSQLServer2012Dialect) {
            return " dbo.convert(VARCHAR, " + arguments.get(0) + ", 126) ";
        } else if (dialect instanceof InfoxOracleDialect) {
            return " to_char( " + arguments.get(0) + " , 'YYYY-MM-DD\"T\"HH24:MI:SS.ff3' ) ";
        } else if (dialect instanceof InfoxPostgreSQLDialect) {
            return " to_char( " + arguments.get(0) + " , 'YYYY-MM-DD\"T\"HH24:MI:SS.MS' ) ";
        } else {
            throw new QueryException("Database type not supported");
        }
    }
    
}
