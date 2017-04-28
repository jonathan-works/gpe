package br.com.infox.epp.processo.home;

import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos;
import br.com.infox.epp.processo.consulta.action.ConsultaController;
import br.com.infox.epp.processo.documento.action.DocumentoProcessoAction;
import br.com.infox.epp.processo.documento.action.PastaAction;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.list.PastaList;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoAction;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.sigilo.service.SigiloProcessoService;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.processo.variavel.action.VariavelProcessoAction;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.context.ContextFacade;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.util.ComponentUtil;
import br.com.itx.component.AbstractHome;

/**
 * Deprecated : A superclasse AbstractHome está em processo de remoção, assim as
 * funções de ProcessoHome estão sendo repassadas a novos componentes
 */
@Deprecated
@AutoCreate
@Name(ProcessoEpaHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class ProcessoEpaHome extends AbstractHome<Processo> {

	private static final LogProvider LOG = Logging.getLogProvider(Processo.class);
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaHome";

	
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private DocumentoManager documentoManager;
	@In
	private SigiloProcessoService sigiloProcessoService;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private Authenticator authenticator;
	@In
	private InfoxMessages infoxMessages;
	@In
	private ConsultaController consultaController;
	@In
	private DocumentoList documentoList;
	
	@Inject
    private PastaRestricaoAction pastaRestricaoAction;
	@Inject
	private PastaList pastaList;
	@Inject
    private ProcessoManager processoManager;
	@Inject
    private VariavelProcessoAction variavelProcessoAction;
	@Inject
	private DocumentoProcessoAction documentoProcessoAction;
	@Inject
	private PastaAction pastaAction;
	@Inject
	private SituacaoProcessoDAO situacaoProcessoDAO;
	@Inject
	private VariavelProcessoService variavelProcessoService;
	@Inject
	private PapelManager papelManager;

	private DocumentoBin documentoBin = new DocumentoBin();
	private String observacaoMovimentacao;
	private boolean iniciaExterno;
	private List<VariavelProcesso> variaveisDetalhe;
	private Boolean inTabExpedidas;

	private Long idTaskInstance;

	public void iniciarTarefaProcesso() {
		try {
			processoManager.iniciarTask(getInstance(), getIdTaskInstance(), Authenticator.getUsuarioPerfilAtual());
			documentoProcessoAction.setProcesso(getInstance().getProcessoRoot());
			carregarVariaveisDetalhe();
		} catch (java.lang.NullPointerException e) {
			LOG.error("ProcessoEpaHome.iniciarTarefaProcesso()", e);
		} catch (DAOException e) {
			LOG.error("Erro ao vincular Usuario", e);
		} catch (BusinessRollbackException e) {
		    FacesMessages.instance().add(e.getMessage());
		    throw new OptimisticLockException();
		} catch (EJBException e) {
			if (e.getCause() instanceof BusinessRollbackException) {
			    FacesMessages.instance().add(e.getMessage());
			    throw new OptimisticLockException();
			}
		}
	}
	
	private void carregarVariaveisDetalhe() {
	    variaveisDetalhe = variavelProcessoService.getVariaveis(getInstance(), 
                DefinicaoVariavelProcessoRecursos.DETALHE_PROCESSO.getIdentificador(), papelManager.isUsuarioExterno(Authenticator.getPapelAtual().getIdentificador()));
	}

	public List<VariavelProcesso> getVariaveisDetalhe() {
		return variaveisDetalhe;
    }
	
	public void visualizarTarefaProcesso() {
		processoManager.visualizarTask(getInstance(), getIdTaskInstance(), Authenticator.getUsuarioPerfilAtual());
	}

	public static ProcessoEpaHome instance() {
		return ComponentUtil.getComponent(NAME);
	}

	public boolean checarVisibilidade() {
		boolean visivel = checarVisibilidadeSemException();
		if (!visivel) {
			avisarNaoHaPermissaoParaAcessarProcesso();
		}
		return visivel;
	}

	public boolean checarVisibilidadeSemException() {
		MetadadoProcesso metadadoProcesso = getInstance().getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
		TipoProcesso tipoProcesso = metadadoProcesso != null ? metadadoProcesso.<TipoProcesso> getValue() : null;
        boolean visivel = situacaoProcessoDAO.canAccessProcesso(getInstance().getIdProcesso(), tipoProcesso, getInTabExpedidas());
		if (!visivel) {
			ContextFacade.setToEventContext("canClosePanel", true);
			FacesMessages.instance().clear();
		}
		return visivel;
	}

	private void avisarNaoHaPermissaoParaAcessarProcesso() {
		ContextFacade.setToEventContext("canClosePanel", true);
		FacesMessages.instance().clear();
		throw new ApplicationException("Sem permissão para acessar o processo: " + getInstance().getNumeroProcesso());
	}

	@Override
	protected Processo createInstance() {
		Processo processo = super.createInstance();
		processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
		return processo;
	}

	@Override
	public String remove() {
		Authenticator.getUsuarioLogado().getProcessoListForIdUsuarioCadastroProcesso().remove(instance);
		return super.remove();
	}

	@Override
	public String remove(Processo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	public boolean hasPartes() {
		return getInstance() != null && getInstance().hasPartes();
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// -------------------------------------------- Getters e Setters
	// --------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------

	public void setProcessoIdProcesso(Integer id) {
		setId(id);
	}

	public Integer getProcessoIdProcesso() {
		return (Integer) getId();
	}

	@Observer("processoHomeSetId")
	@Override
	public void setId(Object id) {
		super.setId(id);
		// Colocado para poder ser chamado antes do iniciarTask
		variavelProcessoAction.init(getInstance());
	}

	public String getObservacaoMovimentacao() {
		return observacaoMovimentacao;
	}

	public void setObservacaoMovimentacao(String observacaoMovimentacao) {
		this.observacaoMovimentacao = observacaoMovimentacao;
	}

	public Long getIdTaskInstance() {
        return idTaskInstance;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    public boolean isIniciaExterno() {
		return iniciaExterno;
	}

	public void setIniciaExterno(boolean iniciaExterno) {
		this.iniciaExterno = iniciaExterno;
	}

	public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}

	public String getNumeroProcesso(int idProcesso) {
		Processo processo = processoManager.find(idProcesso);
		if (processo != null) {
			return processo.getNumeroProcesso();
		}
		return String.valueOf(idProcesso);
	}

	@Override
    public void setTab(String tab) {
		if((tab == null && getTab() != null) || (tab != null && getTab() == null) || !tab.equals(getTab())){
			super.setTab(tab);
			variavelProcessoAction.init(getInstance());
	        if (tab.equals("tabMovimentacoes")){
	        	consultaController.setProcesso(this.getInstance());
	        }
	        if (tab.equals("tabAnexos")){
	        	pastaAction.setProcesso(this.getInstance());
	        	documentoList.setProcesso(this.getInstance().getProcessoRoot());
	        	documentoProcessoAction.setProcesso(getInstance().getProcessoRoot());
	        	documentoProcessoAction.setListClassificacaoDocumento(null);
	        }
	        if(tab.equals("tabAnexar")){
	        	pastaAction.setProcesso(this.getInstance().getProcessoRoot());
	        }
	        if (tab.equals("tabPastaRestricao")) {
	        	pastaRestricaoAction.setProcesso(this.getInstance());
	        	pastaList.setProcesso(this.getInstance().getProcessoRoot());
			}
		}
    }

    public Boolean getInTabExpedidas() {
        return inTabExpedidas != null ? inTabExpedidas : false;
    }

    public void setInTabExpedidas(Boolean inTabExpedidas) {
        this.inTabExpedidas = inTabExpedidas;
    }

}