package br.com.infox.epp.unidadedecisora.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiadaMonocratica;

@Stateless
@AutoCreate
@Name(UnidadeDecisoraColegiadaMonocraticaDAO.NAME)
public class UnidadeDecisoraColegiadaMonocraticaDAO extends DAO<UnidadeDecisoraColegiadaMonocratica> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaMonocraticaDAO";
}
