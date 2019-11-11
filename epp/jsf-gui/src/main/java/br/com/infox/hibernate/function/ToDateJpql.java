package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

public class ToDateJpql implements SQLFunction{

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

	@Override
	@SuppressWarnings("rawtypes")
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
		if (arguments.isEmpty()) {
            throw new QueryException("É necessário a data");
        }
		if (factory.getDialect() instanceof SQLServerDialect) {
		    return "convert(Date, " + arguments.get(0) + ", 103)";
		} else {
		    return "to_date(" + arguments.get(0) + ", 'DD/MM/YYYY HH24:MI:SS') ";
		}
	}

}
