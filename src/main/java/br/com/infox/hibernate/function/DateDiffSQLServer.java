package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class DateDiffSQLServer implements SQLFunction{

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
		return StandardBasicTypes.INTEGER;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory)
			throws QueryException {
		if (arguments.size() != 3) {
			throw new QueryException("Função necessita 3 argumentos. Ex: datediff('day', data1, data2)");
		}
		String interval = (String) arguments.get(0);
		interval = interval.substring(1, interval.length()-1);
		return "DATEDIFF(" + interval + ", " + arguments.get(1) + ", " + arguments.get(2) + ")";
	}

}
