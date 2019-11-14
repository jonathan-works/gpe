package br.com.infox.epp.ajuda.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ajuda.dao.PaginaDAO;
import br.com.infox.epp.ajuda.entity.Pagina;

@Name(PaginaManager.NAME)
@AutoCreate
@Stateless
public class PaginaManager extends Manager<PaginaDAO, Pagina> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "paginaManager";

    public Pagina getPaginaByUrl(String url) {
        return getDao().getPaginaByUrl(url);
    }

    public Pagina criarNovaPagina(String url) throws DAOException {
        Pagina page = new Pagina();
        page.setUrl(url);
        page.setDescricao(url);
        persist(page);
        return find(page.getIdPagina());
    }

}
