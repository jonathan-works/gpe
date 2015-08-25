package br.com.infox.epp.fluxo.manager;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.dao.ModeloPastaDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

@Name(ModeloPastaManager.NAME)
@AutoCreate
public class ModeloPastaManager extends Manager<ModeloPastaDAO, ModeloPasta>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloPastaManager";

	@In
	private ModeloPastaRestricaoManager modeloPastaRestricaoManager;
		
	public List<ModeloPasta> getByFluxo(Fluxo fluxo){
		List<ModeloPasta> modeloPastaList = getDao().getByFluxo(fluxo);
		if (modeloPastaList == null) {
			modeloPastaList = new ArrayList<ModeloPasta>();
		}
		return modeloPastaList;
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
			o.setRemovivel(Boolean.TRUE);
		}
	}

	@Transactional
	public ModeloPasta persistWithDefault(ModeloPasta o) throws DAOException {
		ModeloPasta modelo = persist(o);
		ModeloPastaRestricao restricao = new ModeloPastaRestricao();
		restricao.setModeloPasta(modelo);
		restricao.setTipoPastaRestricao(PastaRestricaoEnum.D);
		restricao.setAlvo(null);
    	restricao.setRead(Boolean.TRUE);
    	restricao.setWrite(Boolean.TRUE);
    	restricao.setDelete(Boolean.TRUE);
    	restricao.setLogicDelete(Boolean.FALSE);
    	modeloPastaRestricaoManager.persist(restricao);
    	return modelo;
	}
	
	public void deleteComRestricoes(ModeloPasta modelo) throws DAOException{
		modeloPastaRestricaoManager.deleteByModeloPasta(modelo);
		remove(modelo);
	}
}
