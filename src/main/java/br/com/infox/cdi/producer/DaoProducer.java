package br.com.infox.cdi.producer;

import java.lang.reflect.ParameterizedType;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

public class DaoProducer {
	
	@SuppressWarnings("rawtypes")
	@Produces
	@GenericDao
	public Dao createDao(InjectionPoint ip) {
		ParameterizedType tipo = (ParameterizedType) ip.getType();
		Class<?> tipoEntidade = (Class<?>) tipo.getActualTypeArguments()[0];
		return new Dao<>(tipoEntidade);
	}

}
