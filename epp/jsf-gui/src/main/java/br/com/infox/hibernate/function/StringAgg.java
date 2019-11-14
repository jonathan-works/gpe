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

public class StringAgg implements SQLFunction {

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
        if (arguments.size() < 3) {
            throw new QueryException("São necessários 3 argumentos, (valor, delimitador, possuiRepeticao)");
        }
        Dialect dialect = factory.getDialect();    
        if (dialect instanceof InfoxPostgreSQLDialect) {
            return " string_agg_ext(" + arguments.get(0) + ", " + arguments.get(1) + ", " + arguments.get(2) + " ) ";
        } else if (dialect instanceof InfoxOracleDialect) {
            return " LISTAGG(" + arguments.get(0) + ", " + arguments.get(1) + ") WITHIN GROUP (ORDER BY 1) ";
        } else if (dialect instanceof InfoxSQLServer2012Dialect) {
            return " dbo.string_agg(" + arguments.get(0) + ", " + arguments.get(1) + ", " + arguments.get(2) + " ) ";
        } else {
            throw new QueryException("Database type not supported");
        }
    }

}
