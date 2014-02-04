package br.com.infox.epp.system.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroManager.NAME)
@AutoCreate
public class ParametroManager extends Manager<ParametroDAO, Parametro> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parametroManager";
	
	public Parametro getParametro(String nome) {
		return getDao().getParametroByNomeVariavel(nome);
	}
	
    public List<Parametro> listParametrosAtivos() {
        return getDao().listParametrosAtivos();
    }

}
