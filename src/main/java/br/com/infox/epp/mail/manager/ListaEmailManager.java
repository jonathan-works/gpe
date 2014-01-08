package br.com.infox.epp.mail.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.mail.dao.ListaEmailDAO;
import br.com.infox.epp.mail.entity.ListaEmail;

@Name(ListaEmailManager.NAME)
@AutoCreate
public class ListaEmailManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "listaEmailManager";
    
    @In private ListaEmailDAO listaEmailDAO;
    
    public Integer getMaxIdGrupoEmailInListaEmail(){
        return listaEmailDAO.getMaxIdGrupoEmailInListaEmail();
    }
    
    public List<ListaEmail> getListaEmailByIdGrupoEmail(Integer idGrupoEmail){
        return listaEmailDAO.getListaEmailByIdGrupoEmail(idGrupoEmail);
    }

}
