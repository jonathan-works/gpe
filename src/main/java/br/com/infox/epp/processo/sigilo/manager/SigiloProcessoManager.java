package br.com.infox.epp.processo.sigilo.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.sigilo.dao.SigiloProcessoDAO;
import br.com.infox.epp.processo.sigilo.entity.SigiloProcesso;

@Name(SigiloProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class SigiloProcessoManager extends Manager<SigiloProcessoDAO, SigiloProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sigiloProcessoManager";
	
	public SigiloProcesso getSigiloProcessoAtivo(ProcessoEpa processoEpa) {
		return getDao().getSigiloProcessoAtivo(processoEpa);
	}
	
	public boolean isSigiloso(ProcessoEpa processoEpa) {
		SigiloProcesso sigiloProcesso = getSigiloProcessoAtivo(processoEpa);
		if (sigiloProcesso != null) {
			return sigiloProcesso.getSigiloso();
		}
		return false;
	}
}
