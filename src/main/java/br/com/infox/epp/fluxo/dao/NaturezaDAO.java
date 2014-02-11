package br.com.infox.epp.fluxo.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Natureza;

@Name(NaturezaDAO.NAME)
@AutoCreate
public class NaturezaDAO extends DAO<Natureza> {

    private static final long serialVersionUID = -7175831474709085125L;
    public static final String NAME = "naturezaDAO";

    @Override
    public Natureza persist(Natureza object) throws DAOException {
        validarNecessidadeDePartes(object);
        return super.persist(object);
    }

    @Override
    public Natureza update(Natureza object) throws DAOException {
        validarNecessidadeDePartes(object);
        return super.update(object);
    }

    private void validarNecessidadeDePartes(Natureza natureza) throws DAOException {
        if (natureza.getHasPartes()
                && (natureza.getTipoPartes() == null || natureza.getTipoPartes() == null)) {
            throw new DAOException("Se a natureza possui partes então nem o tipo das partes "
                    + "nem a quantidade podem ser nulos");
        }
    }

}
