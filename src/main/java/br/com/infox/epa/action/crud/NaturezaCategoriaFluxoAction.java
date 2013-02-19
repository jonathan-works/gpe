package br.com.infox.epa.action.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.infox.epa.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epa.query.NaturezaCategoriaFluxoQuery;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NaturezaCategoriaFluxoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoAction extends AbstractHome<NaturezaCategoriaFluxo> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "naturezaCategoriaFluxoAction";

	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;
	private List<Categoria> categoriaList;
	private List<Fluxo> fluxoList;
	private Natureza natureza;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setNatureza(natureza);
		return super.beforePersistOrUpdate();
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		listByNatureza();
		return super.afterPersistOrUpdate(ret);
	}
	
	@Override
	public String remove(NaturezaCategoriaFluxo obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			naturezaCategoriaFluxoList.remove(obj);
		}
		return remove;
	}

	public void removeAll() {
		try {
			for (Iterator<NaturezaCategoriaFluxo>  iterator = naturezaCategoriaFluxoList.iterator(); iterator.hasNext();) {
				NaturezaCategoriaFluxo nca = iterator.next();
					getEntityManager().remove(nca);
				iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	@SuppressWarnings("unchecked")
    public void init() {
		NaturezaAction naturezaAction = (NaturezaAction) Component.getInstance(NaturezaAction.NAME);
		natureza = naturezaAction.getInstance();
		listByNatureza();
		categoriaList = getEntityManager().createQuery(NaturezaCategoriaFluxoQuery.LIST_CATEGORIA_ATIVO_QUERY).getResultList();
		fluxoList = getEntityManager().createQuery(NaturezaCategoriaFluxoQuery.LIST_FLUXO_ATIVO_QUERY).getResultList();
		newInstance();
	}

	private void listByNatureza() {
		naturezaCategoriaFluxoList = naturezaCategoriaFluxoManager.
									   listByNatureza(natureza);
	}

	public void setNaturezaCategoriaFluxoList(
			List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
		this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
	}

	public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
		return naturezaCategoriaFluxoList;
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