package br.com.infox.epp.processo.metadado.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

@AutoCreate
@Name(MetadadoProcessoDAO.NAME)
public class MetadadoProcessoDAO extends DAO<MetadadoProcesso> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "metadadoProcessoDAO";
	
//	public MetadadoProcesso getMetadadoProcessoByNome() {
//		
//	}
//	
//	public <E> E getMetadadoProcessoByNome(MetadadoProcessoType type) {
//		
//	}
	
}
