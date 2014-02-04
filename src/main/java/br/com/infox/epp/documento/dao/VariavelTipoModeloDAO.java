package br.com.infox.epp.documento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Name(VariavelTipoModeloDAO.NAME)
@AutoCreate
public class VariavelTipoModeloDAO extends DAO<VariavelTipoModelo> {

	public static final String NAME = "variavelTipoModeloDAO";
	private static final long serialVersionUID = 1L;
}
