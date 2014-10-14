package br.com.infox.epp.processo.partes.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.PeriodoParticipacaoProcesso;

@AutoCreate
@Name(PeriodoParticipacaoProcessoDAO.NAME)
public class PeriodoParticipacaoProcessoDAO extends DAO<PeriodoParticipacaoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "periodoParticipacaoProcessoDAO";

}
