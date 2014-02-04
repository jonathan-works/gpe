package br.com.infox.epp.estatistica.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.estatistica.entity.TempoMedioProcesso;

@Name(TempoMedioProcessoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class TempoMedioProcessoDAO extends DAO<TempoMedioProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tempoMedioProcessoDAO";

}
