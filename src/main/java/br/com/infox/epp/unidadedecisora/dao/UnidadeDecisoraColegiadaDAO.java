package br.com.infox.epp.unidadedecisora.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;

@AutoCreate
@Name(UnidadeDecisoraColegiadaDAO.NAME)
public class UnidadeDecisoraColegiadaDAO extends DAO<UnidadeDecisoraColegiada> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaDAO";

}
