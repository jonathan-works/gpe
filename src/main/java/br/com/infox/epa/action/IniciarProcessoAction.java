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
import br.com.infox.epa.bean.AssuntoBean;
import br.com.infox.epa.entity.CategoriaAssunto;
import br.com.infox.epa.entity.NaturezaCategoriaFluxo;
import br.com.infox.epa.entity.NaturezaLocalizacao;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epa.service.IniciarProcessoService;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.Usuario;
import br.com.infox.ibpm.home.Authenticator;

/**
 * 
 * @author Daniel
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
	
	private boolean renderedByAssunto;
	private boolean renderedByLocalizacao;
	private boolean renderedAssuntoLocalizacao;
	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Localizacao localizacao;
	private ProcessoEpa	processoEpa;
	
	private List<NaturezaCategoriaFluxo> naturezaCategoriaFluxoList;
	private List<AssuntoBean> assuntoList;
	private List<Localizacao> localizacaoList;
	
	@Create
	public void init() {
		Localizacao l = Authenticator.getLocalizacaoAtual();
		Papel p = Authenticator.getPapelAtual();
		naturezaCategoriaFluxoList = natCatFluxoLocalizacaoManager.
									 listByLocalizacaoAndPapel(l, p);
	}
	
	public void iniciarProcesso() {
		try{
			Usuario usuarioLogado = Authenticator.getUsuarioLogado();
			processoEpa = new ProcessoEpa();
			processoEpa.setUsuarioCadastroProcesso(usuarioLogado);
			processoEpa.setDataInicio(new Date());
			processoEpa.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
			processoEpa.setLocalizacao(localizacao);
			processoEpa.setNumeroProcesso("");
			processoEpa.setPorcentagem(0);
			processoEpa.setTempoGasto(0);
			Long idJbpm = iniciarProcessoService.iniciarProcesso(processoEpa, 
											     naturezaCategoriaFluxo.getFluxo());
			processoEpa.setIdJbpm(idJbpm);
			processoEpa.setNumeroProcesso(String.valueOf(processoEpa.getIdProcesso()));
			iniciarProcessoService.update(processoEpa);
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
		setRenderedAssuntoLocalizacao(true);
		setAssuntoList(new ArrayList<AssuntoBean>());
		for(CategoriaAssunto ca : naturezaCategoriaFluxo.getCategoria()
													    .getCategoriaAssuntolist()) {
			getAssuntoList().add(new AssuntoBean(ca.getAssunto()));
		}
		localizacaoList = new ArrayList<Localizacao>();
		for (NaturezaLocalizacao nl : naturezaCategoriaFluxo.getNatureza()
														   .getNaturezaLocalizacaoList()) {
			localizacaoList.add(nl.getLocalizacao());
		}
		
	}
	
	public void onSelectAssunto() {
		renderedByAssunto = hasSelectedAssunto();
	}
	
	public void onSelectLocalizacao() {
		if(localizacao != null) {
			renderedByLocalizacao = true;
		} else {
			renderedByLocalizacao = false;
		}
	}
	
	private boolean hasSelectedAssunto() {
		for (AssuntoBean ab : assuntoList) {
			if(ab.isChecked()) {
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

	public void setAssuntoList(List<AssuntoBean> assuntoList) {
		this.assuntoList = assuntoList;
	}

	public List<AssuntoBean> getAssuntoList() {
		return assuntoList;
	}

	public void setLocalizacaoList(List<Localizacao> localizacaoList) {
		this.localizacaoList = localizacaoList;
	}

	public List<Localizacao> getLocalizacaoList() {
		return localizacaoList;
	}

	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setRenderedAssuntoLocalizacao(boolean renderedAssuntoLocalizacao) {
		this.renderedAssuntoLocalizacao = renderedAssuntoLocalizacao;
	}

	public boolean getRenderedAssuntoLocalizacao() {
		return renderedAssuntoLocalizacao;
	}

	public void setRenderedByAssunto(boolean renderedByAssunto) {
		this.renderedByAssunto = renderedByAssunto;
	}

	public boolean isRenderedByAssunto() {
		return renderedByAssunto;
	}

	public void setRenderedByLocalizacao(boolean renderedByLocalizacao) {
		this.renderedByLocalizacao = renderedByLocalizacao;
	}

	public boolean isRenderedByLocalizacao() {
		return renderedByLocalizacao;
	}

	public void setProcessoEpa(ProcessoEpa processoEpa) {
		this.processoEpa = processoEpa;
	}

	public ProcessoEpa getProcessoEpa() {
		return processoEpa;
	}

	
}