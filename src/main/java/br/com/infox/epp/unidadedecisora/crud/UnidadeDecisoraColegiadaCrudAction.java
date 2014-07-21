package br.com.infox.epp.unidadedecisora.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.unidadedecisora.entity.UniDecisoraColegiadaMono;
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
	private List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaAssociadasList;
	
	@Override
	public void onClickFormTab() {
		super.onClickFormTab();
		unidadeDecisoraMonocraticaItemList = null;
		unidadeDecisoraMonocraticaAssociadasList = null;
	}

	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaItemList() {
		if (unidadeDecisoraMonocraticaItemList == null){
			unidadeDecisoraMonocraticaItemList = unidadeDecisoraMonocraticaManager.getListUnidadeDecisoraMonocraticaAtivo();
			unidadeDecisoraMonocraticaItemList.removeAll(getUnidadeDecisoraMonocraticaAssociadasList());
		}
		return unidadeDecisoraMonocraticaItemList;
	}

	public void setUnidadeDecisoraMonocraticaItemList(List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaItemList) {
		this.unidadeDecisoraMonocraticaItemList = unidadeDecisoraMonocraticaItemList;
	}

	public List<UnidadeDecisoraMonocratica> getUnidadeDecisoraMonocraticaAssociadasList() {
		if (unidadeDecisoraMonocraticaAssociadasList == null){
			for (UniDecisoraColegiadaMono colegiadaMono : getInstance().getUniDecisoraColegiadaMonoList()){
				unidadeDecisoraMonocraticaAssociadasList.add(colegiadaMono.getUnidadeDecisoraMonocratica());
			}
		}
		return unidadeDecisoraMonocraticaAssociadasList;
	}

	public void setUnidadeDecisoraMonocraticaAssociadasList(List<UnidadeDecisoraMonocratica> unidadeDecisoraMonocraticaAssociadasList) {
		this.unidadeDecisoraMonocraticaAssociadasList = unidadeDecisoraMonocraticaAssociadasList;
	}
	
}
