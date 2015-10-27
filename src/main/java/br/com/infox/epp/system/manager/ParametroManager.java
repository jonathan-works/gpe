package br.com.infox.epp.system.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.system.dao.ParametroDAO;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroManager.NAME)
@AutoCreate
@Stateless
public class ParametroManager extends Manager<ParametroDAO, Parametro> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parametroManager";

    public Parametro getParametro(String nome) {
        return getDao().getParametroByNomeVariavel(nome);
    }

    public Parametro removeParametroByValue(String value) throws DAOException {
        return getDao().removeByValue(value);
    }
    
    public List<Parametro> listParametrosAtivos() {
        return getDao().listParametrosAtivos();
    }

    public boolean existeParametro(String nome) {
        return getDao().existeParametro(nome);
    }

	public String getValorParametro(String nome) {
		final Parametro parametro = getParametro(nome);
		String result = null;
		if (parametro != null) {
			result = parametro.getValorVariavel();
		} else {
			result = "";
		}
		return result;
	}
}
