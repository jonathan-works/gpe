package br.com.infox.epp.processo.partes.dao;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Name(TipoParteDAO.NAME)
public class TipoParteDAO extends DAO<TipoParte> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tipoParteDAO";

}
