package br.com.infox.epp.mail.dao;

import static br.com.infox.epp.mail.query.ListaEmailQuery.MAXIMO_ID_GRUPO_EMAIL_IN_LISTA_EMAIL;

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

}
