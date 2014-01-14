package br.com.infox.epp.mail.dao;

import static br.com.infox.epp.mail.query.ListaEmailQuery.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.mail.entity.ListaEmail;

@Name(ListaEmailDAO.NAME)
@AutoCreate
public class ListaEmailDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "listaEmailDAO";
    
    public Integer getMaxIdGrupoEmailInListaEmail(){
        return getNamedSingleResult(MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL);
    }
    
    public List<ListaEmail> getListaEmailByIdGrupoEmail(Integer idGrupoEmail){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_GRUPO_EMAIL_PARAM, idGrupoEmail);
        return getNamedResultList(LISTA_EMAIL_BY_ID_GRUPO, parameters);
    }
    
    public List<String> resolve(int idGrupoEmail) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_GRUPO_EMAIL_PARAM, idGrupoEmail);
        return getNamedResultList(RESOLVE_LISTA_EMAIL_BY_ID_GRUPO, parameters);
    }

}
