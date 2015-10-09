package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

public class DataUtilAdd implements SQLFunction {

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
		return DateType.INSTANCE;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory)	throws QueryException {
		if (arguments.size() != 3) {
            throw new QueryException("É necessário informar o tipo, quantidade e data");
        }
        String schema = "";
        if (factory.getDialect() instanceof SQLServerDialect) {
            schema = "dbo.";
        }
        return schema + "DataUtilAdd(" + arguments.get(0) + " , " + arguments.get(1) + " , " + arguments.get(2) + " )";
	}

}
