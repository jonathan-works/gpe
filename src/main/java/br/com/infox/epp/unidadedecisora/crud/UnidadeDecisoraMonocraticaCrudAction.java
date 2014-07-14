package br.com.infox.epp.unidadedecisora.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(UnidadeDecisoraMonocraticaCrudAction.NAME)
public class UnidadeDecisoraMonocraticaCrudAction extends AbstractCrudAction<UnidadeDecisoraMonocratica, UnidadeDecisoraMonocraticaManager>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaCrudAction";
	
	
	
}
