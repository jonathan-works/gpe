package br.com.infox.epp.access.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.dao.RecursoDAO;

@Name(RecursoManager.NAME)
@AutoCreate
public class RecursoManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoManager";
    
    @In private RecursoDAO recursoDAO;
    
    public boolean existsRecurso(String identificador){
        return recursoDAO.existsRecurso(identificador);
    }

}
