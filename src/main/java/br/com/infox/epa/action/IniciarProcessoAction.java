package br.com.infox.epa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.TypeMismatchException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.access.entity.Papel;
import br.com.infox.epa.bean.ItemBean;
import br.com.infox.epa.entity.CategoriaItem;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epa.manager.ProcessoEpaManager;
import br.com.infox.epa.service.IniciarProcessoService;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.Authenticator;

/**
 * 
 * @author Daniel
 *
 */
/**
 * @author jonas
 *
 */
@Name(IniciarProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class IniciarProcessoAction {

	public static final String NAME = "iniciarProcessoAction";

	@In
	private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	@In
	private ProcessoEpaManager processoEpaManager;
	
	private boolean renderedByItem;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Localizacao localizacao;
	private Item itemDoProcesso;
	private ProcessoEpa processoEpa;
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;
	private List<ItemBean> itemList;
	
	private Papel papel;
	
	@Create
	public void init() {
		localizacao = Authenticator.getLocalizacaoAtual();
		papel = Authenticator.getPapelAtual();
		naturezaCategoriaFluxoList = natCatFluxoLocalizacaoManager.
									 listByLocalizacaoAndPapel(localizacao, papel);
	}
	
	public void iniciarProcesso() {
		try {
			Usuario usuarioLogado = Authenticator.getUsuarioLogado();
			processoEpa = new ProcessoEpa();
			processoEpa.setDataInicio(new Date());
			processoEpa.setNumeroProcesso("");
			processoEpa.setUsuarioCadastroProcesso(usuarioLogado);
			processoEpa.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			processoEpa.setLocalizacao(localizacao);
			processoEpa.setItemDoProcesso(itemDoProcesso);
			processoEpaManager.persist(processoEpa);
			
			iniciarProcessoService.iniciarProcesso(processoEpa, naturezaCategoriaFluxo.getFluxo());
			
			FacesMessages.instance().add(Severity.INFO, "Processo inserido com sucesso!");
		} catch(TypeMismatchException tme) {
			tme.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, IniciarProcessoService.
										 TYPE_MISMATCH_EXCEPTION);
		} catch(NullPointerException npe) {
			npe.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR,"Nenhum processo informado.");
		} catch(Exception e) {
			e.printStackTrace();
			FacesMessages.instance().add(Severity.ERROR, "Não foi possível iniciar o processo");
		}
	}

	public void onSelectNatCatFluxo(NaturezaCategoriaFluxo ncf) {
		naturezaCategoriaFluxo = ncf;
		setItemList(new ArrayList<ItemBean>());
		for(CategoriaItem ca : naturezaCategoriaFluxo.getCategoria()
													    .getCategoriaItemList()) {
			getItemList().add(new ItemBean(ca.getItem()));
		}
		if (itemList.size() > 0) 
			setRenderedByItem(true);
	}
	
	public void onSelectItem(ItemBean bean) {
		itemDoProcesso = bean.getItem();
		renderedByItem = hasSelectedItem();
		iniciarProcesso();
	}

	private boolean hasSelectedItem() {
		for (ItemBean ib : itemList) {
			if(ib.isChecked()) {
				return true;
			}
		}
		return false;
	}
	
	public void setNaturezaCategoriaFluxoList(
			List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList) {
		this.naturezaCategoriaFluxoList = naturezaCategoriaFluxoList;
	}

	public List<NaturezaCategoriaFluxo> getNaturezaCategoriaFluxoList() {
		return naturezaCategoriaFluxoList;
	}
	
	public boolean isRenderedByItem() {
		return renderedByItem;
	}
	
	public void setRenderedByItem(boolean renderedByItem) {
		this.renderedByItem = renderedByItem;
	}

	public void setProcessoEpa(ProcessoEpa processoEpa) {
		this.processoEpa = processoEpa;
	}

	public ProcessoEpa getProcessoEpa() {
		return processoEpa;
	}

	public List<ItemBean> getItemList() {
		return itemList;
	}

	public void setItemList(List<ItemBean> itemList) {
		this.itemList = itemList;
	}
	
}