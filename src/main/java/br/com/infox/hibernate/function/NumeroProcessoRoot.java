package br.com.infox.hibernate.function;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class NumeroProcessoRoot implements SQLFunction {

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
        if (arguments.isEmpty()) {
            throw new QueryException("É necessácio informar o id do processo");
        } 
        return "(select p.nr_processo from tb_processo p where p.id_processo = "
        		+ "(select p1.id_processo_root from tb_processo p1 where p1.id_processo = "+ arguments.get(0)+ ") )" ;
    }

}