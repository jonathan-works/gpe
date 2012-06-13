package br.com.infox.core.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;

/**
 * Classe que acessa o GenericDAO e disponibiliza
 * os m�todos �teis nele contidos, afim de n�o ser 
 * necess�ria a cria��o de um manager ou service
 * toda vez que for preciso utilizar algum m�todo
 * da classe GenericDAO.
 * 
 * @author Daniel
 *
 */
@Name(GenericManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class GenericManager {

	public static final String NAME = "genericManager";
	
	@In
	private GenericDAO genericDAO;
	
	public <T> T persist(T o) {
		return genericDAO.persist(o);
	}
	
	public <T> T update(T o) {
		return genericDAO.update(o);
	}
	
	public <T> T remove(T o) {
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
}