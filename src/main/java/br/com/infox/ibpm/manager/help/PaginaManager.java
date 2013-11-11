package br.com.infox.ibpm.manager.help;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.ajuda.dao.PaginaDAO;
import br.com.infox.epp.ajuda.entity.Pagina;

@Name(PaginaManager.NAME)
@AutoCreate
public class PaginaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "paginaManager";
	
	@In private PaginaDAO paginaDAO;
	
	public Pagina getPaginaByUrl(String url){
		return paginaDAO.getPaginaByUrl(url);
	}

}
