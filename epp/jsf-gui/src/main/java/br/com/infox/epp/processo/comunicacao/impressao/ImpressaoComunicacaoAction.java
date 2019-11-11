package br.com.infox.epp.processo.comunicacao.impressao;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.comunicacao.meioexpedicao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(ImpressaoComunicacaoAction.NAME)
@Transactional
public class ImpressaoComunicacaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "impressaoComunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ImpressaoComunicacaoAction.class);
	
	@In
	private ProcessoManager processoManager;
	@In
	private ImpressaoComunicacaoService impressaoComunicacaoService;
	
	private Boolean impressaoCompleta = Boolean.FALSE;
	private Integer selected;
	private HashMap<Processo, Date> cacheDataAssinaturas = new HashMap<>();
	
	public void newInstance() {
		impressaoCompleta = Boolean.FALSE;
		selected = null;
	}
	
	public MeioExpedicao getMeioExpedicao(Processo processo) {
		return impressaoComunicacaoService.getMeioExpedicao(processo);
	}
	
	public Date getDataAssinatura(Processo processo) {
		if (cacheDataAssinaturas.containsKey(processo)) {
			return cacheDataAssinaturas.get(processo);
		}
		Date dataAssinatura = impressaoComunicacaoService.getDataAssinatura(processo); 
		cacheDataAssinaturas.put(processo, dataAssinatura);
		return dataAssinatura;
	}
	
	public Boolean getImpresso(Processo processo) {
		return impressaoComunicacaoService.getImpresso(processo);
	}
	
	public TipoComunicacao getTipoComunicacao(Processo processo) {
		return impressaoComunicacaoService.getTipoComunicacao(processo);
	}
	
	public void marcarComoImpresso() {
	    try {
	        Processo processo = processoManager.find(getSelected());
	        impressaoComunicacaoService.marcarComunicacaoComoImpressa(processo);
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

	public Integer getSelected() {
		return selected;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

}
