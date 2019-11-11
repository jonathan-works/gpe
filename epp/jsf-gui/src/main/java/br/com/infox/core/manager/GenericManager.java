package br.com.infox.core.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

/**
 * Classe que acessa o GenericDAO e disponibiliza os métodos úteis nele
 * contidos, afim de não ser necessária a criação de um manager ou service toda
 * vez que for preciso utilizar algum método da classe GenericDAO.
 * 
 * @author Daniel
 * 
 */
@Stateless
@AutoCreate
@Name(GenericManager.NAME)
public class GenericManager extends Manager<GenericDAO, Object> {

    private static final long serialVersionUID = -5694962568615133171L;

    public static final String NAME = "genericManager";

    public <T> T find(Class<T> entityClass, Object id) {
        return getDao().find(entityClass, id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return getDao().findAll(entityClass);
    }

    public <T> T getReference(Class<T> entityClass, Object id) {
        return getDao().getReference(entityClass, id);
    }
}
