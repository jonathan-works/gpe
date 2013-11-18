package br.com.infox.epp.fluxo.home;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

/**
 * 
 * @author Daniel
 *
 */
@Name(NatCatFluxoLocalizacaoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class NatCatFluxoLocalizacaoHome extends AbstractHome<NatCatFluxoLocalizacao> {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "natCatFluxoLocalizacaoHome";
	private static final Log LOG = Logging.getLog(NatCatFluxoLocalizacaoHome.class);

	@In
	private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
	
	@In(value = LocalizacaoTreeHandler.NAME, create = true)
	private LocalizacaoTreeHandler localizacaoTreeHandler;
	
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;
	private List<NatCatFluxoLocalizacao> natCatFluxolocalizacaoList;
	private List<Natureza> naturezaList;
	private List<Categoria> categoriaList;
	private List<Fluxo> fluxoList;
	private boolean updateList = true;
	private NatCatFluxoLocalizacao oldInstance;
	
	@Override
	public void newInstance() {
		super.newInstance();
		updateList = true;
		naturezaCategoriaFluxo = null;
	}
	
	@Override
	public String persist() {
	    String result = save();
	    newInstance();
		return result;
	}
	
	public String save() {
		NatCatFluxoLocalizacao instanceToSave = getInstance();
		instanceToSave.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
		String save = null;
		if (instanceToSave.getHeranca()) {
			try {
				natCatFluxoLocalizacaoManager.persistWithChildren(instanceToSave);
				save = PERSISTED;
				FacesMessages.instance().addFromResourceBundleOrDefault( StatusMessage.Severity.INFO, getCreatedMessageKey(), getCreatedMessage().getExpressionString() );
			} catch (DAOException e) {
				LOG.error(null, e);
			}
		} else {
			save = super.persist();
		}
		updateList=true;
		return save;
	}
	
	@Override
	public void setId(Object id) {
		super.setId(id);
		NatCatFluxoLocalizacao tempInstance = getInstance();
		if(oldInstance == null || oldInstance.getIdNatCatFluxoLocalizacao() != 
									tempInstance.getIdNatCatFluxoLocalizacao()) {
			oldInstance = new NatCatFluxoLocalizacao();
			oldInstance.setIdNatCatFluxoLocalizacao(tempInstance.getIdNatCatFluxoLocalizacao());
			oldInstance.setHeranca(tempInstance.getHeranca());
			oldInstance.setNaturezaCategoriaFluxo(tempInstance.getNaturezaCategoriaFluxo());
			oldInstance.setLocalizacao(tempInstance.getLocalizacao());
		}
	}
	
	@Override
	public String remove(NatCatFluxoLocalizacao obj) {
		final String result = super.remove(obj);
		newInstance();
		return result;
	}
	
	public List<NaturezaCategoriaFluxo> getActivedNaturezaCategoriaFluxoList() {
		List <NaturezaCategoriaFluxo> ativos = new ArrayList<NaturezaCategoriaFluxo>();
		for (NaturezaCategoriaFluxo ncf : naturezaCategoriaFluxoList){
			if (ncf.isAtivo()){
				ativos.add(ncf);
			}
		}
		return ativos;
	}
	
	public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
	    String hql = "select ncf from NaturezaCategoriaFluxo ncf " +
	    		"inner join ncf.natureza n " +
	    		"inner join ncf.categoria c " +
	    		"where n.ativo=true " +
	    		"and c.ativo=true " +
	    		"and ncf.fluxo=:fluxo";
	    return EntityUtil.getEntityManager().createQuery(hql, NaturezaCategoriaFluxo.class)
	            .setParameter("fluxo", fluxo)
	            .getResultList();
	}
	
	@Override
	public void create() {
		super.create();
		naturezaCategoriaFluxoList = EntityUtil.getEntityList(NaturezaCategoriaFluxo.class);
		naturezaList = EntityUtil.getEntityList(Natureza.class);
		categoriaList = EntityUtil.getEntityList(Categoria.class);
		fluxoList = EntityUtil.getEntityList(Fluxo.class);
	}
	
	public void setLocalizacaoTreeHandler(LocalizacaoTreeHandler localizacaoTreeHandler) {
		this.localizacaoTreeHandler = localizacaoTreeHandler;
	}

	public LocalizacaoTreeHandler getLocalizacaoTreeHandler() {
		return localizacaoTreeHandler;
	}

	
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}

	
	public void setNaturezaCategoriaFluxo(
			NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
		newInstance();
	}

	
	public List<NatCatFluxoLocalizacao> getNatCatFluxolocalizacaoList() {
		if (updateList) {
			this.natCatFluxolocalizacaoList = natCatFluxoLocalizacaoManager.listByNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			updateList = false;
		}
		return natCatFluxolocalizacaoList;
	}

	
	public void setNatCatFluxolocalizacaoList(
			List<NatCatFluxoLocalizacao> natCatFluxolocalizacaoList) {
		this.natCatFluxolocalizacaoList = natCatFluxolocalizacaoList;
	}

	public void setNaturezaCategoriaFluxoList(List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
		this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
	}

	public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
		return naturezaCategoriaFluxoList;
	}
	
	public void setNaturezaList(List<Natureza> naturezaList) {
		this.naturezaList = naturezaList;
	}

	public List<Natureza> getNaturezaList() {
		return naturezaList;
	}

	public void setCategoriaList(List<Categoria> categoriaList) {
		this.categoriaList = categoriaList;
	}

	public List<Categoria> getCategoriaList() {
		return categoriaList;
	}

	public void setFluxoList(List<Fluxo> fluxoList) {
		this.fluxoList = fluxoList;
	}

	public List<Fluxo> getFluxoList() {
		return fluxoList;
	}
}