package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;
import br.com.infox.epp.documento.type.TipoDocumentoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class TipoComunicacaoCrudAction extends AbstractCrudAction<TipoComunicacao, TipoComunicacaoManager> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(TipoComunicacaoCrudAction.class);
    
    @Inject
    private TipoComunicacaoManager tipoComunicacaoManager;
    @Inject
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @Inject
    private GenericManager genericManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;
    @Inject
    private ClassificacaoDocumentoDAO classificacaoDocumentoDao;
    
    private List<TipoModeloDocumento> tiposModeloDocumento;
    private List<ClassificacaoDocumento> classificacoesDocumento;
    private ClassificacaoDocumento classificacaoDocumentoProrrogacao;
	private List<ClassificacaoDocumento> classificacoesDisponiveis;
    
    public List<TipoModeloDocumento> getTiposModeloDocumento() {
    	if (tiposModeloDocumento == null) {
    		tiposModeloDocumento = tipoModeloDocumentoManager.getTiposModeloDocumentoAtivos();
    	}
		return tiposModeloDocumento;
	}
    
    public List<ClassificacaoDocumento> getClassificacoesDocumento() {
        if (classificacoesDocumento == null) {
            classificacoesDocumento = classificacaoDocumentoFacade
                    .getUseableClassificacaoDocumento(TipoDocumentoEnum.P);
        }
        return classificacoesDocumento;
    }
    
    public void addClassificacaoDocumentoResposta(ClassificacaoDocumento classificacaoDocumento){
    	TipoComunicacaoClassificacaoDocumento tipoComunicacaoClassificacaoDocumento = new TipoComunicacaoClassificacaoDocumento();
    	tipoComunicacaoClassificacaoDocumento.setClassificacaoDocumento(classificacaoDocumento);
    	tipoComunicacaoClassificacaoDocumento.setTipoComunicacao(getInstance());
    	try {
			getInstance().getTipoComunicacaoClassificacaoDocumentos().add((TipoComunicacaoClassificacaoDocumento) genericManager.persist(tipoComunicacaoClassificacaoDocumento));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
    }
    
    public void removeClassificacaoDocumentoResposta(TipoComunicacaoClassificacaoDocumento tipoComunicacaoClassificacaoDocumento) {
    	try {
			genericManager.remove(tipoComunicacaoClassificacaoDocumento);
			getInstance().getTipoComunicacaoClassificacaoDocumentos().remove(tipoComunicacaoClassificacaoDocumento);
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
    
    public TipoUsoComunicacaoEnum[] tipoUsoComunicacaoDisponiveis() {
		return TipoUsoComunicacaoEnum.values();
	}
    
    @Override
	public void setInstance(TipoComunicacao instance) {
		super.setInstance(instance);
		atualizaCamposAdicionais();
	}

	protected void atualizaCamposAdicionais() {
		setClassificacaoDocumentoProrrogacao(getInstance().getClassificacaoProrrogacao());
	}
    
    public void gravarClassificacaoProrrogacao() {
		getInstance().setClassificacaoProrrogacao(getClassificacaoDocumentoProrrogacao());
		try {
			update();
			FacesMessages.instance().add("Classificação da solicitação de prorrogação adicionada com sucesso");
		} catch (DAOException e) {
			FacesMessages.instance().add("Não foi possível relacionar a classificação da solicitação de prorrogação");
			setClassificacaoDocumentoProrrogacao(null);
		}
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoProrrogacao() {
		return classificacaoDocumentoProrrogacao;
	}

	public void setClassificacaoDocumentoProrrogacao(ClassificacaoDocumento classificacaoDocumentoProrrogacao) {
		this.classificacaoDocumentoProrrogacao = classificacaoDocumentoProrrogacao;
	}

	public List<ClassificacaoDocumento> getClassificacoesDisponiveis() {
		if (classificacoesDisponiveis == null) {
			classificacoesDisponiveis = classificacaoDocumentoDao.getClassificacoesDisponiveisPedidoProrrogacaoByTipoComunicacao(getInstance());
		}
		return classificacoesDisponiveis;
	}

	public void setClassificacoesDisponiveis(List<ClassificacaoDocumento> classificacoesDisponiveis) {
		this.classificacoesDisponiveis = classificacoesDisponiveis;
	}

	@Override
	protected TipoComunicacaoManager getManager() {
	    return tipoComunicacaoManager;
	}
}
