package br.com.infox.epp.processo.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.dao.RelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcessoInterno;

@AutoCreate
@Name(RelacionamentoProcessoManager.NAME)
@Stateless
public class RelacionamentoProcessoManager extends Manager<RelacionamentoProcessoDAO, RelacionamentoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "relacionamentoProcessoManager";

	public boolean existeRelacionamento(RelacionamentoProcessoInterno rp1 , RelacionamentoProcesso rp2) {
		return getDao().existeRelacionamento(rp1, rp2);
	}

}
