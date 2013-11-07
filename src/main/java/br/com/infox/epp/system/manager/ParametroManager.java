package br.com.infox.epp.system.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.ibpm.dao.ParametroDAO;

@Name(ParametroManager.NAME)
@AutoCreate
public class ParametroManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroManager";
	
	@In private ParametroDAO parametroDAO;
	
	public Parametro getParametro(String nome) {
		return parametroDAO.getParametroByNomeVariavel(nome);
	}

}
