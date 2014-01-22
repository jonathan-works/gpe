package br.com.infox.epp.ajuda.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ajuda.dao.PaginaDAO;
import br.com.infox.epp.ajuda.entity.Pagina;

@Name(PaginaManager.NAME)
@AutoCreate
public class PaginaManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    private static final Class<Pagina> CLASS = Pagina.class;
    public static final String NAME = "paginaManager";

    @In
    private PaginaDAO paginaDAO;

    public Pagina getPaginaByUrl(String url) {
        return paginaDAO.getPaginaByUrl(url);
    }
    
    public Pagina find(Object id) {
        return super.find(CLASS, id);
    }
    
    public Pagina criarNovaPagina(String url) throws DAOException{
        Pagina page = new Pagina();
        page.setUrl(url);
        page.setDescricao(url);
        persist(page);
        return find(page.getIdPagina());
    }

}
