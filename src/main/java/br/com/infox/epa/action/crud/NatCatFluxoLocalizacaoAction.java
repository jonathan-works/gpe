package br.com.infox.epa.action.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.NatCatFluxoLocalizacao;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.infox.epa.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.EntityUtil;

/**
 * 
 * @author Daniel
 *
 */
@Name(NatCatFluxoLocalizacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NatCatFluxoLocalizacaoAction extends AbstractHome<NatCatFluxoLocalizacao> {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "natCatFluxoLocalizacaoAction";

	@In
	private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
	
	private LocalizacaoTreeHandler localizacaoTreeHandler = new LocalizacaoTreeHandler();
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;
	private List<Natureza> naturezaList;
	private List<Categoria> categoriaList;
	private List<Fluxo> fluxoList;
	private NatCatFluxoLocalizacao oldInstance;
	
	@Override
	public void setId(Object id) {
		super.setId(id);
		if(oldInstance == null || oldInstance.getIdNatCatFluxoLocalizacao() != 
								  getInstance().getIdNatCatFluxoLocalizacao()) {
			oldInstance = new NatCatFluxoLocalizacao();
			oldInstance.setIdNatCatFluxoLocalizacao(getInstance().getIdNatCatFluxoLocalizacao());
			oldInstance.setHeranca(getInstance().getHeranca());
			oldInstance.setNaturezaCategoriaFluxo(getInstance().getNaturezaCategoriaFluxo());
			oldInstance.setLocalizacao(getInstance().getLocalizacao());
		}
	}

	@Override
	public String update() {
		return save();
	}
	
	@Override
	public String persist() {
		return save();
	}
	
	public String save() {
		boolean heranca = getInstance().getHeranca();
		String save = null;
		try {
			if(isManaged()) {
				natCatFluxoLocalizacaoManager.saveWithChidren(getInstance(), oldInstance);
				save = UPDATED;
				FacesMessages.instance().add("Registro alterado com sucesso!");
			} else {
				if(heranca) {
					natCatFluxoLocalizacaoManager.persistWithChildren(getInstance());
					save = PERSISTED;
					FacesMessages.instance().add("Registro incluido com sucesso!");
				} else {
					save = super.persist();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, "Registro j� Cadastrado!");
		}
		return save;
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

	public void setNaturezaCategoriaFluxoList(
			List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
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