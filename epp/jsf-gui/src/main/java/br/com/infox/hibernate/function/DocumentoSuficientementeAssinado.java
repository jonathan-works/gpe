package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

public class DocumentoSuficientementeAssinado implements SQLFunction {

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
		return BooleanType.INSTANCE;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
		if (arguments.isEmpty()) {
			throw new QueryException("É necessário informar o id do documento");
		}
		String schema = "";
		if (factory.getDialect() instanceof SQLServerDialect) {
			schema = "dbo.";
		}
		return schema + "DocumentoSuficientementeAssinado(" + arguments.get(0) + ")";
	}
}
