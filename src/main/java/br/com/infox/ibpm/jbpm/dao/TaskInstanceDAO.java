package br.com.infox.ibpm.jbpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.ibpm.jbpm.UsuarioTaskInstance;

@AutoCreate
@Name(TaskInstanceDAO.NAME)
public class TaskInstanceDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceDAO";

    public void removeUsuario(final Long idTaskInstance) throws DAOException {
        remove(find(UsuarioTaskInstance.class, idTaskInstance));
    }
    
}
