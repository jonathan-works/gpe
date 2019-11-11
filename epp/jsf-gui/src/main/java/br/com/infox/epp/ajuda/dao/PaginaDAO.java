package br.com.infox.epp.ajuda.dao;

import static br.com.infox.epp.ajuda.query.PaginaQuery.PAGINA_BY_URL;
import static br.com.infox.epp.ajuda.query.PaginaQuery.PARAM_URL;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.ajuda.entity.Pagina;

@Stateless
@AutoCreate
@Name(PaginaDAO.NAME)
public class PaginaDAO extends DAO<Pagina> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "paginaDAO";

    public Pagina getPaginaByUrl(String url) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_URL, url);
        return getNamedSingleResult(PAGINA_BY_URL, parameters);
    }

}
