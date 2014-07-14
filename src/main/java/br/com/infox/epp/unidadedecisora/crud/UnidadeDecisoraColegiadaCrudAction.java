package br.com.infox.epp.unidadedecisora.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(UnidadeDecisoraColegiadaCrudAction.NAME)
public class UnidadeDecisoraColegiadaCrudAction extends AbstractCrudAction<UnidadeDecisoraColegiada, UnidadeDecisoraColegiadaManager>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaCrudAction";
	
	@In
	private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
	
	private List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaItemList;
	
	@Override
	public void onClickFormTab() {
		super.onClickFormTab();
		unidadeDecisoraMonocraticaItemList = null;
	}

	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaItemList() {
		if (unidadeDecisoraMonocraticaItemList == null){
		}
		return unidadeDecisoraMonocraticaItemList;
	}

	public void setUnidadeDecisoraMonocraticaItemList(
			List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaItemList) {
		this.unidadeDecisoraMonocraticaItemList = unidadeDecisoraMonocraticaItemList;
	}
	
}
