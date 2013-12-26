package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.CategoriaQuery.LIST_PROCESSO_EPP_BY_CATEGORIA;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(CategoriaDAO.NAME)
@AutoCreate
public class CategoriaDAO extends GenericDAO {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "categoriaDAO";

    public List<Object[]> listProcessoByCategoria() {
        return getNamedResultList(LIST_PROCESSO_EPP_BY_CATEGORIA);
    }

}
