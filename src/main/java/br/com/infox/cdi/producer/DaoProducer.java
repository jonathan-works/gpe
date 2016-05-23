package br.com.infox.cdi.producer;

import java.lang.reflect.ParameterizedType;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class DaoProducer {
	
	@Produces
	@Dependent
	@SuppressWarnings("unchecked")
	public <T, ID> br.com.infox.cdi.dao.Dao<T, ID> createDao(InjectionPoint ip) {
		ParameterizedType tipo = (ParameterizedType) ip.getType();
		Class<T> tipoEntidade = (Class<T>) tipo.getActualTypeArguments()[0];
		return new br.com.infox.cdi.dao.Dao<T, ID>(tipoEntidade) {};
	}

}
