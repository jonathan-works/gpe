package br.com.infox.epp.processo.metadado.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.dao.MetadadoProcessoDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;

@AutoCreate
@Name(MetadadoProcessoManager.NAME)
public class MetadadoProcessoManager extends Manager<MetadadoProcessoDAO, MetadadoProcesso> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "metadadoProcessoManager";
	
	public List<MetadadoProcesso> getListMetadadoVisivelByProcesso(Processo processo) {
		return getDao().getListMetadadoVisivelByProcesso(processo);
	}
	
	public MetadadoProcesso getMetadado(MetadadoProcessoDefinition definition, Processo processo) {
		return getDao().getMetadado(definition, processo);
	}
	public void addMetadadoProcesso(Processo processo, MetadadoProcessoDefinition definition, String valor) throws DAOException {
		MetadadoProcessoProvider provider = new MetadadoProcessoProvider(processo);        
		MetadadoProcesso metadadoProcesso = provider.gerarMetadado(definition, valor);
        processo.getMetadadoProcessoList().add(metadadoProcesso);
        persist(metadadoProcesso);
    }
	
	public List<MetadadoProcesso> getMetadadoProcessoByType(Processo processo, String metadadoType) {
		return getDao().getMetadadoProcessoByType(processo, metadadoType);
	}
	
	public void removerMetadado(MetadadoProcessoDefinition definition, Processo processo) throws DAOException {
		getDao().removerMetadado(definition, processo);
	}

	public void persistMetadados(MetadadoProcessoProvider metadadoProcessoProvider, List<MetadadoProcesso> metadados) throws DAOException {
		Processo processo = metadadoProcessoProvider.getProcesso();
	    for (MetadadoProcesso metadadoProcesso : metadados) {
			persist(metadadoProcesso);
			processo.getMetadadoProcessoList().add(metadadoProcesso);
		}
	}
}
