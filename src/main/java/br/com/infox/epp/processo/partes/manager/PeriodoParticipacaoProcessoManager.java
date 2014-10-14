package br.com.infox.epp.processo.partes.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.partes.dao.PeriodoParticipacaoProcessoDAO;
import br.com.infox.epp.processo.partes.entity.PeriodoParticipacaoProcesso;

@AutoCreate
@Name(PeriodoParticipacaoProcessoManager.NAME)
public class PeriodoParticipacaoProcessoManager extends Manager<PeriodoParticipacaoProcessoDAO, PeriodoParticipacaoProcesso>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "periodoParticipacaoProcessoManager";

}
