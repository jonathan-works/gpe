package br.com.infox.epp.fluxo.manager;

import java.util.List;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.ModeloPastaDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

public class ModeloPastaManager extends Manager<ModeloPastaDAO, ModeloPasta>{

	public List<ModeloPasta> getByFluxo(Fluxo fluxo){
		List<ModeloPasta> modeloPastaList = getDao().getByFluxo(fluxo);
		//TODO verificar esse .isEmpty se não vai dar nullPointer caso modeloPastaList for null
		if (modeloPastaList == null || modeloPastaList.isEmpty()) {
			modeloPastaList = createDefaultFolders(fluxo);
		}
		return modeloPastaList;
	}
	
	private List<ModeloPasta> createDefaultFolders(Fluxo fluxo) {
		// TODO Ver se precisa implementar isso ou excluir o if, conferir na us pastas Default
		return null;
	}

	@Override
	public ModeloPasta persist(ModeloPasta o) throws DAOException {
		prePersist(o);
		return super.persist(o);
	}
	
	protected void prePersist(ModeloPasta o) {
		if (o.getEditavel() == null){
			o.setEditavel(Boolean.TRUE);
		}
		if(o.getRemovivel() == null){
			o.setEditavel(Boolean.TRUE);
		}
	}

	public ModeloPasta persistWithDefault(ModeloPasta o) throws DAOException {
		ModeloPastaRestricao restricao = new ModeloPastaRestricao();
		restricao.setModeloPasta(o);
		restricao.setTipoPastaRestricao(PastaRestricaoEnum.D);
		restricao.setAlvo(null);
    	restricao.setRead(Boolean.FALSE);
    	restricao.setWrite(Boolean.FALSE);
    	restricao.setDelete(Boolean.FALSE);
    	o.getRestricoes().add(restricao);
    	return persist(o);		
	}

}
