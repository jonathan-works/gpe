package br.com.infox.epp.action.crud;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.Natureza;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

/**
 * 
 * @author Erik Liberal
 *
 */
@Name(NaturezaCategoriaFluxoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoHome extends AbstractHome<NaturezaCategoriaFluxo> {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "naturezaCategoriaFluxoHome";

	private List<Natureza> naturezaList;
	private List<Categoria> categoriaList;
	private List<Fluxo> fluxoList;
	
	@PostConstruct
	public void init() {
		naturezaList = EntityUtil.getEntityList(Natureza.class);
		categoriaList = EntityUtil.getEntityList(Categoria.class);
		fluxoList = EntityUtil.getEntityList(Fluxo.class);
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
	
	public List<Natureza> getNaturezaList() {
		return naturezaList;
	}

	public void setNaturezaList(List<Natureza> naturezaList) {
		this.naturezaList = naturezaList;
	}
}