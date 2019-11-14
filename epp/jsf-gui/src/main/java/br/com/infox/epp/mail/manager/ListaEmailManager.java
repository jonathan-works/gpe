package br.com.infox.epp.mail.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.mail.dao.ListaEmailDAO;
import br.com.infox.epp.mail.entity.ListaEmail;

@Name(ListaEmailManager.NAME)
@AutoCreate
public class ListaEmailManager extends Manager<ListaEmailDAO, ListaEmail> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "listaEmailManager";

    public Integer getMaxIdGrupoEmailInListaEmail() {
        return getDao().getMaxIdGrupoEmailInListaEmail();
    }

    public List<ListaEmail> getListaEmailByIdGrupoEmail(Integer idGrupoEmail) {
        return getDao().getListaEmailByIdGrupoEmail(idGrupoEmail);
    }

    public List<String> resolve(int idGrupoEmail) {
        return getDao().resolve(idGrupoEmail);
    }

}
