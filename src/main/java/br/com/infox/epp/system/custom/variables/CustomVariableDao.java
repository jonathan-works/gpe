package br.com.infox.epp.system.custom.variables;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
public class CustomVariableDao {

	@Inject
    @GenericDao
    private Dao<CustomVariable, Long> dao;
	
	public void persist(CustomVariable customVariable) {
		dao.persist(customVariable);
	}
	
	public CustomVariable update(CustomVariable customVariable) {
		return dao.update(customVariable);
	}
	
	public void remove(CustomVariable customVariable) {
		dao.remove(customVariable);
	}
}
