package br.com.infox.epp.processo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.dao.RelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso;
import br.com.infox.epp.processo.entity.RelacionamentoProcesso.TipoProcesso;

@AutoCreate
@Name(RelacionamentoProcessoManager.NAME)
public class RelacionamentoProcessoManager extends Manager<RelacionamentoProcessoDAO, RelacionamentoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "relacionamentoProcessoManager";

	@In
	private ProcessoManager processoManager;

	public boolean existeRelacionamento(String processo1, TipoProcesso tipoProcesso1, String processo2, TipoProcesso tipoProcesso2) {
		return getDao().existeRelacionamento(processo1, tipoProcesso1, processo2, tipoProcesso2);
	}

	@Override
	public RelacionamentoProcesso persist(RelacionamentoProcesso instance) throws DAOException {
		if (instance.getProcesso() == null) {
			if (instance.getTipoProcesso() == TipoProcesso.ELE) {
				instance.setProcesso(processoManager.getProcessoEpaByNumeroProcesso(instance.getNumeroProcesso()));
			}
		}
		return super.persist(instance);
	}

}
