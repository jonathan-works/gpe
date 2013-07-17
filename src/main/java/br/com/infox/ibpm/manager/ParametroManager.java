package br.com.infox.ibpm.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.ParametroDAO;
import br.com.infox.ibpm.entity.Parametro;

@Name(ParametroManager.NAME)
@AutoCreate
public class ParametroManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroManager";
	
	@In private ParametroDAO parametroDAO;
	
	public Parametro getParametro(String nome) throws IllegalArgumentException {
		return parametroDAO.getParametrosByNomeVariavel(nome);
	}

}
