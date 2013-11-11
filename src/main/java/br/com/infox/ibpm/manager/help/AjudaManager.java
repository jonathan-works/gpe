package br.com.infox.ibpm.manager.help;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.ajuda.dao.AjudaDAO;
import br.com.infox.epp.ajuda.entity.Ajuda;

@Name(AjudaManager.NAME)
@AutoCreate
public class AjudaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "ajudaManager";
	
	@In private AjudaDAO ajudaDAO;
	
	public Ajuda getAjudaByPaginaUrl(String url){
		return ajudaDAO.getAjudaByPaginaUrl(url);
	}
	
}
