package br.com.infox.epp.processo.comunicacao.impressao;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Scope(ScopeType.CONVERSATION)
@Name(ImpressaoComunicacaoAction.NAME)
public class ImpressaoComunicacaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "impressaoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ImpressaoComunicacaoAction.class);
	
	@In
	private ProcessoManager processoManager;
	@In
	private ImpressaoComunicacaoService impressaoComunicacaoService;
	
	private Boolean impressaoCompleta = Boolean.FALSE;
	private Boolean marcarImpresso = Boolean.FALSE;
	private Integer selected;
	
	public void newInstance() {
		impressaoCompleta = Boolean.FALSE;
		marcarImpresso = Boolean.FALSE;
		selected = null;
	}
	
	public MeioExpedicao getMeioExpedicao(Processo processo) {
		return impressaoComunicacaoService.getMeioExpedicao(processo);
	}
	
	public Date getDataAssinatura(Processo processo) {
		return impressaoComunicacaoService.getDataAssinatura(processo);
	}
	
	public Boolean getImpresso(Processo processo) {
		return impressaoComunicacaoService.getImpresso(processo);
	}
	
	public TipoComunicacao getTipoComunicacao(Processo processo) {
		return impressaoComunicacaoService.getTipoComunicacao(processo);
	}
	
	public void imprimirComunicacao() {
		try {
			Processo processo = processoManager.find(getSelected());
			if (getMarcarImpresso()) {
				impressaoComunicacaoService.marcarComunicacaoComoImpressa(processo);
			}
		} catch (DAOException e) {
			FacesMessages.instance().add("Erro ao Marcar Impresso " + e.getMessage());
			LOG.error("imprimirComunicacao()", e);
		}
	}
	
	public void downloadComunicacao() {
		Processo processo = processoManager.find(getSelected());
		try {
			impressaoComunicacaoService.downloadComunicacao(processo, getImpressaoCompleta());
		} catch (DAOException e) {
			FacesMessages.instance().add("Erro ao imprimir " + e.getMessage());
			LOG.error("downloadComunicacao()", e);
			if (getMarcarImpresso()) {
				try {
					impressaoComunicacaoService.desmarcarComunicacaoComoImpressa(processo);
				} catch (DAOException e1) {
					LOG.error("desmarcarComunicacaoComoImpressa()", e);
				}
			}
		} finally {
			newInstance();
		}
	}
	
	public Boolean getImpressaoCompleta() {
		return impressaoCompleta;
	}

	public void setImpressaoCompleta(Boolean impressaoCompleta) {
		this.impressaoCompleta = impressaoCompleta;
	}

	public Boolean getMarcarImpresso() {
		return marcarImpresso;
	}

	public void setMarcarImpresso(Boolean marcarImpresso) {
		this.marcarImpresso = marcarImpresso;
	}

	public Integer getSelected() {
		return selected;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

}
