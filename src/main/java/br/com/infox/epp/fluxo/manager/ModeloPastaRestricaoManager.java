package br.com.infox.epp.fluxo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.ModeloPastaDAO;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@Name(ModeloPastaRestricaoManager.NAME)
@AutoCreate
public class ModeloPastaRestricaoManager extends Manager<ModeloPastaDAO, ModeloPasta>{

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaRestricaoManager";

}
