package br.com.infox.epp.fluxo.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.ModeloPastaRestricaoDAO;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;

@Name(ModeloPastaRestricaoManager.NAME)
@AutoCreate
@Stateless
public class ModeloPastaRestricaoManager extends Manager<ModeloPastaRestricaoDAO, ModeloPastaRestricao>{

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaRestricaoManager";
	
	public List<ModeloPastaRestricao> getByModeloPasta(ModeloPasta modeloPasta) {
		return getDao().getByModeloPasta(modeloPasta);
	}
	
	public void deleteByModeloPasta(ModeloPasta modelo) throws DAOException{
		getDao().deleteByModeloPasta(modelo);
	}

}
