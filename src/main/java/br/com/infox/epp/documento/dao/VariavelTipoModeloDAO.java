package br.com.infox.epp.documento.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Stateless
@AutoCreate
@Name(VariavelTipoModeloDAO.NAME)
public class VariavelTipoModeloDAO extends DAO<VariavelTipoModelo> {

    public static final String NAME = "variavelTipoModeloDAO";
    private static final long serialVersionUID = 1L;
}
