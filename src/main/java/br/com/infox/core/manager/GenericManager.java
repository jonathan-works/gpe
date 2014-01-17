package br.com.infox.core.manager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;

/**
 * Classe que acessa o GenericDAO e disponibiliza
 * os métodos úteis nele contidos, afim de não ser 
 * necessária a criação de um manager ou service
 * toda vez que for preciso utilizar algum método
 * da classe GenericDAO.
 * 
 * @author Daniel
 *
 */
@Name(GenericManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class GenericManager implements Serializable {

	private static final long serialVersionUID = -5694962568615133171L;

	public static final String NAME = "genericManager";
	
	@In
	private GenericDAO genericDAO;
	
	public <T> T persist(T o) throws DAOException {
		return genericDAO.persist(o);
	}
	
	public <T> T update(T o) throws DAOException {
		return genericDAO.update(o);
	}
	
	public <T> T remove(T o) throws DAOException {
		return genericDAO.remove(o);
	}
	
	public <T> T find(Class<T> c, Object id) {
		return genericDAO.find(c, id);
	}
	
	public <T> List<T> findAll(Class<T> clazz) {
		return genericDAO.findAll(clazz);
	}
	
	public boolean contains(Object o) {
		return genericDAO.contains(o);
	}
	
	public <T> T merge(T o) throws DAOException {
		return genericDAO.merge(o);
	}
	
	public <T> void detach(T o) {
		genericDAO.detach(o);
	}
	
	public void clear() {
		genericDAO.clear();
	}
	
	public void flush(){
	    genericDAO.flush();
	}
	
	public <T> void refresh(T o){
	    genericDAO.refresh(o);
	}

    public Long getSingleResult(final String query, final Map<String, Object> params) {
        return genericDAO.getSingleResult(query, params);
    }
}