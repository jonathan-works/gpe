package br.com.infox.epa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.TypeMismatchException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.epa.bean.ItemBean;
import br.com.infox.epa.entity.CategoriaItem;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epa.manager.ProcessoEpaManager;
import br.com.infox.epa.service.IniciarProcessoService;
import br.com.infox.epa.type.SituacaoPrazoEnum;
import br.com.infox.epa.type.TipoPessoaEnum;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Pessoa;
import br.com.infox.ibpm.entity.PessoaFisica;
import br.com.infox.ibpm.entity.PessoaJuridica;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.home.PessoaFisicaHome;
import br.com.infox.ibpm.home.PessoaJuridicaHome;
import br.com.itx.util.EntityUtil;
import javax.persistence.Query;

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
	private boolean renderizarCadastroPartes;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Item itemDoProcesso;
	private ProcessoEpa processoEpa;
	private List<ItemBean> itemList;
	private List<PessoaFisica> pessoaFisicaList = new ArrayList<PessoaFisica>();
	private List<PessoaJuridica> pessoaJuridicaList = new ArrayList<PessoaJuridica>();
	
	private void iniciarProcesso() {
		try {
			Usuario usuarioLogado = Authenticator.getUsuarioLogado();
			Localizacao localizacao = Authenticator.getLocalizacaoAtual();
			processoEpa = new ProcessoEpa();
			processoEpa.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
			processoEpa.setDataInicio(new Date());
			processoEpa.setNumeroProcesso("");
			processoEpa.setUsuarioCadastroProcesso(usuarioLogado);
			processoEpa.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			processoEpa.setLocalizacao(localizacao);
			processoEpa.setItemDoProcesso(itemDoProcesso);
			if (necessitaPartes()){
				for (Pessoa p : pessoaFisicaList) {
					processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
				}
				for (Pessoa p : pessoaJuridicaList) {
					processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
				}
			}
			processoEpaManager.persist(processoEpa);
			
			iniciarProcessoService.iniciarProcesso(processoEpa, naturezaCategoriaFluxo.getFluxo());
			System.out.println(processoEpa.getIdProcesso());
			
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
		itemList = new ArrayList<ItemBean>();
		for(CategoriaItem ca : naturezaCategoriaFluxo.getCategoria()
													    .getCategoriaItemList()) {
			itemList.add(new ItemBean(ca.getItem()));
		}
		if (itemList.isEmpty()){
			FacesMessages.instance().add(Severity.ERROR, "Não há itens cadastrados para a categoria escolhida");
		} else { 
			setRenderedByItem(true);
		}
	}
	
	public void onSelectItem(ItemBean bean, boolean hasPartes) {
		itemDoProcesso = bean.getItem();
		renderedByItem = hasSelectedItem();
		if (!hasPartes) 
			iniciarProcesso();
		else{
			renderizarCadastroPartes = true;
			renderedByItem = false;
		}
	}

	private boolean hasSelectedItem() {
		for (ItemBean ib : itemList) {
			if(ib.isChecked()) {
				return true;
			}
		}
		return false;
	}
	
	public void carregaPessoa(String tipoPessoa, String codigo){
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ");
		if (tipoPessoa.equals("F") || tipoPessoa.equals("f")) {
			sb.append("PessoaFisica o where o.cpf = :cpf");
			Query query = EntityUtil.createQuery(sb.toString()).setParameter("cpf", codigo);
			PessoaFisica pessoa = EntityUtil.getSingleResult(query);
			Events.instance().raiseEvent("evtCarregarPessoaFisica", pessoa);
		} else if (tipoPessoa.equals("J") || tipoPessoa.equals("j")){
			sb.append("PessoaJuridica o where o.cnpj = :cnpj");
			Query query = EntityUtil.createQuery(sb.toString()).setParameter("cnpj", codigo);
			PessoaJuridica pessoa = EntityUtil.getSingleResult(query);
			Events.instance().raiseEvent("evtCarregarPessoaJuridica", pessoa);
		} else return;
		
	}
	
	public void incluir(String tipoPessoa){
		if (tipoPessoa.equals("F") || tipoPessoa.equals("f")) {
			PessoaFisicaHome pf = (PessoaFisicaHome) Component.getInstance("pessoaFisicaHome");
			PessoaFisica p = pf.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.F);
				EntityUtil.getEntityManager().persist(p);
				EntityUtil.getEntityManager().flush();
			}
			if (pessoaFisicaList.contains(p))
				FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			else
				pessoaFisicaList.add(p);
		}
		else if (tipoPessoa.equals("J") || tipoPessoa.equals("j")) {
			PessoaJuridicaHome pj = (PessoaJuridicaHome) Component.getInstance("pessoaJuridicaHome");
			PessoaJuridica p = pj.getInstance();
			if (p.getAtivo() == null){
				p.setAtivo(true);
				p.setTipoPessoa(TipoPessoaEnum.J);
				EntityUtil.getEntityManager().persist(p);
				EntityUtil.getEntityManager().flush();
			}
			if (pessoaJuridicaList.contains(p))
				FacesMessages.instance().add(Severity.ERROR, "Parte já cadastrada no processo");
			else
				pessoaJuridicaList.add(p);
		} else return;
	}
	
	public void gravar(){
		iniciarProcesso();
		for (Pessoa p : pessoaFisicaList) {
			processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
		}
		for (Pessoa p : pessoaJuridicaList) {
			processoEpa.getPartes().add(new ParteProcesso(processoEpa, p));
		}
	}
	
	public void removePessoaFisica(PessoaFisica obj) {
		pessoaFisicaList.remove(obj);
	}
	
	public void removePessoaJuridica(PessoaJuridica obj) {
		pessoaJuridicaList.remove(obj);
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
	
	public boolean necessitaPartes(){
		if (naturezaCategoriaFluxo != null)
			return naturezaCategoriaFluxo.getNatureza().getHasPartes();
		else return false;
	}

	public boolean isRenderizarCadastroPartes() {
		return renderizarCadastroPartes;
	}

	public List<PessoaFisica> getPessoaFisicaList() {
		return pessoaFisicaList;
	}

	public void setPessoaFisicaList(List<PessoaFisica> pessoaFisicaList) {
		this.pessoaFisicaList = pessoaFisicaList;
	}

	public List<PessoaJuridica> getPessoaJuridicaList() {
		return pessoaJuridicaList;
	}

	public void setPessoaJuridicaList(List<PessoaJuridica> pessoaJuridicaList) {
		this.pessoaJuridicaList = pessoaJuridicaList;
	}
}