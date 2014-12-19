package br.com.infox.epp.processo.metadado.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.dao.MetadadoProcessoDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

@AutoCreate
@Name(MetadadoProcessoManager.NAME)
public class MetadadoProcessoManager extends Manager<MetadadoProcessoDAO, MetadadoProcesso>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "metadadoProcessoManager";
	
	public List<MetadadoProcesso> getListMetadadoVisivelByProcesso(Processo processo) {
		return getDao().getListMetadadoVisivelByProcesso(processo);
	}
	
	
}
