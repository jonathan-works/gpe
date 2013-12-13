package br.com.infox.epp.access.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.dao.RecursoDAO;
import br.com.infox.epp.access.entity.Permissao;
import br.com.infox.epp.access.entity.Recurso;

@Name(RecursoManager.NAME)
@AutoCreate
public class RecursoManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "recursoManager";
    
    @In private RecursoDAO recursoDAO;
    
    public boolean existsRecurso(String identificador){
        return recursoDAO.existsRecurso(identificador);
    }
    
    public List<Recurso> getRecursosFromPermissoes(List<Permissao> permissoes){
        return recursoDAO.getRecursosFromPermissoes(permissoes);
    }
    
    public List<Recurso> getRecursosWithoutPermissoes(List<Permissao> permissoes){
        return recursoDAO.getRecursosWithoutPermissoes(permissoes);
    }

}
