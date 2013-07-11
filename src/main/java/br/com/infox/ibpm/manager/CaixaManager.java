package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.CaixaDAO;

@Name(CaixaManager.NAME)
@AutoCreate
public class CaixaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaManager";
	
	@In private CaixaDAO caixaDAO;
	
	public void removeCaixaByIdCaixa(int idCaixa){
		caixaDAO.removeCaixaByIdCaixa(idCaixa);
	}

}
