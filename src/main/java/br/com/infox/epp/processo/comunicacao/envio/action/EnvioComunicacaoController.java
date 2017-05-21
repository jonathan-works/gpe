package br.com.infox.epp.processo.comunicacao.envio.action;

import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.CODIGO_LOCALIZACAO_ASSINATURA;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.CODIGO_PERFIL_ASSINATURA;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.CODIGO_TIPO_COMUNICACAO;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.EM_ELABORACAO;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.EXIBIR_RESPONSAVEIS_ASSINATURA;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.EXIBIR_TRANSICOES;
import static br.com.infox.epp.processo.comunicacao.envio.action.EnvioComunicacaoController.PRAZO_PADRAO_RESPOSTA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;

import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.Token;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.exception.EppConfigurationException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.localizacao.LocalizacaoSearch;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoSearch;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoUsoComunicacaoEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.ibpm.variable.components.AbstractTaskPageController;
import br.com.infox.ibpm.variable.components.ParameterDefinition.ParameterType;
import br.com.infox.ibpm.variable.components.ParameterVariable;
import br.com.infox.ibpm.variable.components.Taskpage;
import br.com.infox.jsf.util.JsfProducer.ParamValue;
import br.com.infox.jsf.util.JsfProducer.RequestParam;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@Taskpage(
		id="enviarComunicacao",
		xhtmlPath="/WEB-INF/taskpages/enviarComunicacao.xhtml",
		name="Enviar Comunicação",
		description="enviarComunicacao.description",
		parameters={
				@ParameterVariable(id=PRAZO_PADRAO_RESPOSTA, type=ParameterType.INTEGER, description="enviarComunicacao.parameter.prazo"),
				@ParameterVariable(id=CODIGO_LOCALIZACAO_ASSINATURA, type=ParameterType.STRING, description="enviarComunicacao.parameter.codLocalizacaoAssinatura"),
				@ParameterVariable(id=CODIGO_PERFIL_ASSINATURA, type=ParameterType.STRING, description="enviarComunicacao.parameter.codPerfilAssinatura"),
				@ParameterVariable(id=CODIGO_TIPO_COMUNICACAO, type=ParameterType.STRING, description="enviarComunicacao.parameter.tipoComunicacao"),
				@ParameterVariable(id=EM_ELABORACAO, type=ParameterType.BOOLEAN, description="enviarComunicacao.parameter.emElaboracao"),
				@ParameterVariable(id=EXIBIR_TRANSICOES, type=ParameterType.BOOLEAN, description="enviarComunicacao.parameter.exibirTransicoes"),
				@ParameterVariable(id=EXIBIR_RESPONSAVEIS_ASSINATURA, type=ParameterType.BOOLEAN, description="enviarComunicacao.parameter.exibirResponsavelAssinatura")
		}
)
@ViewScoped
public class EnvioComunicacaoController  extends AbstractTaskPageController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(EnvioComunicacaoController.class);
	public static final int MAX_RESULTS = 10;
	private static final TipoUsoComunicacaoEnum TIPO = TipoUsoComunicacaoEnum.E;
	//Parametros disponíveis para configuração da página de tarefa
	public static final String CODIGO_TIPO_COMUNICACAO = "tipoComunicacao";
	public static final String PRAZO_PADRAO_RESPOSTA = "prazoPadraoResposta";
	public static final String CODIGO_LOCALIZACAO_ASSINATURA = "localizacaoAssinaturaComunicacao";
	public static final String CODIGO_PERFIL_ASSINATURA = "perfilAssinatura";
	public static final String EM_ELABORACAO = "emElaboracao";
	public static final String EXIBIR_TRANSICOES = "exibirTransicoes";
	public static final String EXIBIR_RESPONSAVEIS_ASSINATURA = "exibirResponsavelAssinatura";
	
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private AssinadorService assinadorService;
	@Inject
	private DocumentoComunicacaoAction documentoComunicacaoAction;
	@Inject
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@Inject
	private DestinatarioComunicacaoAction destinatarioComunicacaoAction;
	@Inject
	private ProcessoManager processoManager;
	@Inject
	private ComunicacaoService comunicacaoService;
	@Inject
	protected LocalizacaoManager localizacaoManager;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private LocalizacaoSearch localizacaoSearch;
	@Inject
	private UsuarioLoginSearch usuarioLoginSearch;
	@Inject
	private TipoComunicacaoSearch tipoComunicacaoSearch;
	@Inject
	private PerfilTemplateManager perfilTemplateManager;
	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
	
	@Inject @RequestParam
	protected ParamValue<Long> jbpmProcessId;
	@Inject @RequestParam
	protected ParamValue<Long> idModeloComunicacao;
	
	private String raizLocalizacoesComunicacao = Parametros.RAIZ_LOCALIZACOES_COMUNICACAO.getValue();
	private Localizacao localizacaoRaizComunicacao;
	private Localizacao localizacaoRaizAssinaturaComunicacao;
	protected Long processInstanceId;
	private Integer prazoDefaultComunicacao = null;
    private Localizacao localizacaoAssinatura;
    private PerfilTemplate perfilAssinatura;
	private List<TipoComunicacao> tiposComunicacao;
	private ModeloComunicacao modeloComunicacao;
	private boolean exibirTransicoes = false;
	private boolean exibirResponsaveisAssinatura = true;
	
	private boolean finalizada;
	private String token;
	private Boolean expedida;
	private Boolean comunicacaoSuficientementeAssinada;
	private DestinatarioModeloComunicacao destinatario;
	protected boolean inTask = false;
	private boolean minuta;
	protected String idModeloComunicacaoVariableName;
	private boolean isNew = true;
	private boolean existeUsuarioLocalizacaoAssinatura = true;
	private boolean existeParametroTipoComunicacao = false;
	
	@PostConstruct
	public void init() {
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = getTaskInstance();
        if ( !jbpmProcessId.isNull() ) { // Nova comunicação fora da aba de saída
            processInstanceId = jbpmProcessId.getValue();
        } else if ( idModeloComunicacao.isNull() ) { // Nova comunicação dentro da aba de saída
            processInstanceId = Long.valueOf(getProcesso().getIdJbpm());
            inTask = taskInstance != null;
        }
        if (taskInstance != null) {
            idModeloComunicacaoVariableName = "idModeloComunicacao-" + taskInstance.getId();
        }
        initModelo(idModeloComunicacao.getValue());
        initParametros();
        clear();
	}
	
	protected org.jbpm.taskmgmt.exe.TaskInstance getTaskInstance() {
	    return super.getTaskInstance() == null ? TaskInstance.instance() : super.getTaskInstance();
	}
	
	protected Processo getProcesso() {
        return super.getProcesso() == null ? JbpmUtil.getProcesso() : super.getProcesso();
    }

    protected void initParametros() {
        if (inTask) {
            String tipoComunicacaoCodigo = getVariable(CODIGO_TIPO_COMUNICACAO, String.class);
            if (!Strings.isNullOrEmpty(tipoComunicacaoCodigo)) {
                TipoComunicacao tipoComunicacao = tipoComunicacaoSearch.getTiposComunicacaoAtivosByCodigo(tipoComunicacaoCodigo, TIPO);
                if (tipoComunicacao == null) {
                    throw new EppConfigurationException("O Tipo de Comunicação não foi definido com um valor válido.");
                } else {
                    tiposComunicacao = new ArrayList<>(1);
                    tiposComunicacao.add(tipoComunicacao);
                    modeloComunicacao.setTipoComunicacao(tipoComunicacao);
                    existeParametroTipoComunicacao = true;
                }
            }

            String prazo = (String) getTaskInstance().getVariable(PRAZO_PADRAO_RESPOSTA);
            if (!Strings.isNullOrEmpty(prazo)) {
                try {
                    prazoDefaultComunicacao = new Integer(prazo);
                } catch (NumberFormatException e) {
                    throw new EppConfigurationException("O prazo de resposta padrão sugerido não foi definido com um valor válido.");
                }
            }

            String codigoLocalizacaoAssinatura = (String) getTaskInstance().getVariable(CODIGO_LOCALIZACAO_ASSINATURA);
            if (!Strings.isNullOrEmpty(codigoLocalizacaoAssinatura)) {
                localizacaoAssinatura = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacaoAssinatura);
                if (localizacaoAssinatura == null) {
                    throw new EppConfigurationException("A localização para assinatura não foi definida com um valor válido");
                }
                getModeloComunicacao().setLocalizacaoResponsavelAssinatura(localizacaoAssinatura);
            }

            String codigoPerfilTemplateAssinatura = (String) getTaskInstance().getVariable(CODIGO_PERFIL_ASSINATURA);
            if (!Strings.isNullOrEmpty(codigoPerfilTemplateAssinatura)) {
                perfilAssinatura = perfilTemplateManager.getPerfilTemplateByCodigo(codigoPerfilTemplateAssinatura);
                if (perfilAssinatura == null) {
                    throw new EppConfigurationException("O perfil para assinatura não foi definida com um valor válido");
                }
                getModeloComunicacao().setPerfilResponsavelAssinatura(perfilAssinatura);
            }
            
            Boolean exibirTransicoes = (Boolean) getTaskInstance().getVariable(EXIBIR_TRANSICOES);
            if (exibirTransicoes != null && exibirTransicoes) {
                this.exibirTransicoes = true;
            }
            
            Boolean exibirResponsavelAssinatura = (Boolean) getTaskInstance().getVariable(EXIBIR_RESPONSAVEIS_ASSINATURA);
            if (exibirResponsavelAssinatura != null && !exibirResponsavelAssinatura) {
                this.exibirResponsaveisAssinatura = false;
            }
        }
    }

	private void initDocumentoComunicacaoAction() {
		documentoComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		documentoComunicacaoAction.init();		
	}
	
	private void initDestinatarioComunicacaoAction() {
		destinatarioComunicacaoAction.setModeloComunicacao(modeloComunicacao);
		destinatarioComunicacaoAction.init(getLocalizacaoRaizComunicacao(), prazoDefaultComunicacao, perfilAssinatura);		
	}
	
	private void initLocalizacaoRaiz() {
		try {
			localizacaoRaizComunicacao = localizacaoManager.getLocalizacaoByNome(raizLocalizacoesComunicacao);
			if (localizacaoRaizComunicacao == null) {
			    throw new EppConfigurationException("O parâmetro raizLocalizacoesComunicacao não foi definido.");
			}
		} catch (DAOException e) {
			LOG.error("", e);
			if (e.getCause() instanceof NonUniqueResultException) {
			    throw new EppConfigurationException("Existe mais de uma localização com o nome definido no parâmetro raizLocalizacoesComunicacao: " + raizLocalizacoesComunicacao);
			} else {
				actionMessagesService.handleDAOException(e);
			}
		}
        String codigoLocalizacaoRaizAssinaturaComunicacao = Parametros.RAIZ_LOCALIZACOES_ASSINATURA_COMUNICACAO.getValue();
        if (codigoLocalizacaoRaizAssinaturaComunicacao != null && !codigoLocalizacaoRaizAssinaturaComunicacao.isEmpty()) {
            localizacaoRaizAssinaturaComunicacao = localizacaoSearch.getLocalizacaoByCodigo(codigoLocalizacaoRaizAssinaturaComunicacao);
            if (localizacaoRaizAssinaturaComunicacao == null) {
                throw new EppConfigurationException("O parâmetro codigoRaizResponsavelAssinaturaLocalizacao não foi definido corretamente");
            }
        } else {
            localizacaoRaizAssinaturaComunicacao = Authenticator.getLocalizacaoAtual();
        }
	}

	protected void initModelo(Long idModelo) {
	    org.jbpm.taskmgmt.exe.TaskInstance taskInstance = getTaskInstance();
		if (idModelo == null && taskInstance != null) { //Comunicação na aba de saída 
			ContextInstance context = taskInstance.getContextInstance();
			Token taskToken = taskInstance.getToken();
			idModelo = (Long) context.getVariable(idModeloComunicacaoVariableName, taskToken);
			if (idModelo == null) {
	            Boolean emElaboracao = (Boolean) getTaskInstance().getVariable(EM_ELABORACAO);
	            if (emElaboracao != null && emElaboracao.equals(Boolean.TRUE)) {
	                ModeloComunicacao modeloComunicacaoEmElaboracao = getModeloEmElaboracao();
	                if (modeloComunicacaoEmElaboracao != null) {
	                    idModelo = modeloComunicacaoEmElaboracao.getId();
	                    context.setVariable(idModeloComunicacaoVariableName, idModelo, taskToken);
	                }
	            }
			}
		}
		if (idModelo == null) { // Nova comunicação
			this.modeloComunicacao = new ModeloComunicacao();
			this.modeloComunicacao.setProcesso(processoManager.getProcessoByIdJbpm(processInstanceId));
			if (taskInstance != null && inTask) {
			    this.modeloComunicacao.setTaskKey(taskInstance.getTask().getKey());
			}
		} else { // Comunicação existente
			this.modeloComunicacao = modeloComunicacaoManager.find(idModelo);
			setFinalizada(modeloComunicacao.getFinalizada() != null ? modeloComunicacao.getFinalizada() : false);
			this.processInstanceId = this.modeloComunicacao.getProcesso().getIdJbpm();
			BusinessProcess.instance().setProcessId(processInstanceId);
			isNew = false;
		}
		minuta = modeloComunicacao.isMinuta();
	}
	
	@Transactional
	public void gravar() {
		try {
			validarGravacao();
			
			if (modeloComunicacao.getId() == null) {
				modeloComunicacaoManager.persist(modeloComunicacao);
			}

			destinatarioComunicacaoAction.persistDestinatarios();
			documentoComunicacaoAction.persistDocumentos();
			modeloComunicacao = modeloComunicacaoManager.update(modeloComunicacao);
			isNew = false;
			if (isFinalizada()) {
				comunicacaoService.finalizarComunicacao(modeloComunicacao);
				if ((!modeloComunicacao.isDocumentoBinario() && !modeloComunicacao.isClassificacaoAssinavel()) 
					|| (modeloComunicacao.isDocumentoBinario() && documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno())) {
					expedirComunicacao();
				}
			}
			clear();
			FacesMessages.instance().add("Registro gravado com sucesso");
			minuta = modeloComunicacao.isMinuta();
			setIdModeloVariable(modeloComunicacao.getId());
		} catch (Exception e) {
			LOG.error("Erro ao gravar comunicação ", e);
			if (e instanceof DAOException) {
				if (e.getCause() instanceof OptimisticLockException) {
					actionMessagesService.handleGenericException(e, "Erro ao gravar: A comunicação foi alterada por outro usuário");
				} else {
					actionMessagesService.handleDAOException((DAOException) e);
				}
			} else {
				FacesMessages.instance().add(e.getMessage());
			}
			resetEntityState();
		}
	}

	protected void clear() {
		destinatario = null;
		initLocalizacaoRaiz();
		initDestinatarioComunicacaoAction();
		initDocumentoComunicacaoAction();
	}

	private void setIdModeloVariable(Long id) {
		if (getTaskInstance() != null) {
			setVariable(idModeloComunicacaoVariableName, id, getTaskInstance().getToken());
			if (id != null) {
			    setVariable(ComunicacaoService.COMUNICACAO_EM_ELABORACAO, getModeloComunicacao());
			}
		}
	}

	private void validarGravacao() {
		StringBuilder msg = criarMensagensValidacao();
		if (msg.length() > 0) {
			throw new BusinessException(msg.toString());
		}
	}

	protected StringBuilder criarMensagensValidacao() {
		StringBuilder msg = new StringBuilder();
		if (modeloComunicacao.getTipoComunicacao() == null) {
			msg.append("Escolha o tipo de comunicação.\n");
		}
		if (modeloComunicacao.getDestinatarios().isEmpty()) {
			msg.append("Nenhum destinatário foi selecionado.\n");
		}
		if (finalizada && !modeloComunicacao.isDocumentoBinario() && modeloComunicacao.isMinuta()) {
		    msg.append("Não é possível finalizar pois o texto no editor da comunicação é minuta.\n");
		}
		if (!modeloComunicacao.isMinuta() && modeloComunicacao.getClassificacaoComunicacao() == null){
			msg.append("Escolha a classificação de documento.\n");
		}
		if (!modeloComunicacao.isMinuta() && Strings.isNullOrEmpty(modeloComunicacao.getTextoComunicacao())){
			msg.append("O documento do editor não é minuta mas não existe texto no editor.\n");
		}
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getMeioExpedicao() == null) {
				msg.append("Existe destinatário sem meio de expedição selecionado.\n");
				break;
			}
			if (isPrazoComunicacaoRequired() && (destinatario.getPrazo() == null || destinatario.getPrazo() < 0)){
				msg.append("Não foi informado o prazo para o destinatário ");
				msg.append(destinatario.getNome());
				msg.append(" ou esse prazo é inválido.\n");
				break;
			}
		}
		return msg;
	}

	private void resetEntityState() {
		this.finalizada = false;
		modeloComunicacao.setFinalizada(false);
		if (isNew) {
			minuta = true;
			modeloComunicacao.setId(null);
			setIdModeloVariable(null);
			documentoComunicacaoAction.resetEntityState();
			destinatarioComunicacaoAction.resetEntityState();
			destinatarioComunicacaoAction.setLocalizacao(null);
			destinatarioComunicacaoAction.setPerfilDestino(null);
		}
		modeloComunicacao.setMinuta(minuta);
	}

	@Transactional
	public void expedirComunicacao() {
		try {
			if (destinatario != null) {
				if (!isComunicacaoSuficientementeAssinada()) {
					assinadorService.assinarToken(token, Authenticator.getUsuarioPerfilAtual());
					clearAssinaturas();
				}
				if (isComunicacaoSuficientementeAssinada()) {
					comunicacaoService.expedirComunicacao(destinatario);
				}
			} else if ((!modeloComunicacao.isDocumentoBinario() && !modeloComunicacao.isClassificacaoAssinavel()) 
					|| documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno()) {
				comunicacaoService.expedirComunicacao(modeloComunicacao);
			}
			clearAssinaturas();
			clear();
			expedida = null;
			if ((destinatario!= null && destinatario.getExpedido()) || (destinatario == null && isExpedida())) {
				FacesMessages.instance().add("Comunicação expedida com sucesso");
			} 
		} catch (DAOException e) {
			LOG.error("Erro ao expedir comunicação", e);
			actionMessagesService.handleDAOException(e);
		} catch (AssinaturaException e) {
			LOG.error("Erro ao expedir comunicação", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}

    public void reabrirComunicacao() {
		try {
			modeloComunicacao = comunicacaoService.reabrirComunicacao(getModeloComunicacao());
			isNew = false;
			minuta = true;
			resetEntityState();
			clear();
			destinatarioComunicacaoAction.init(getLocalizacaoRaizComunicacao(), prazoDefaultComunicacao, perfilAssinatura);
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.reabertura"));
		} catch (DAOException | CloneNotSupportedException e) {
			LOG.error("Erro ao rebarir comunicação", e);
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.reabertura"));
		}
	}
	
	@Transactional
	public void excluirDestinatarioComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
		try {
			destinatarioComunicacaoAction.excluirDestinatario(destinatarioModeloComunicacao);
			if (modeloComunicacao.getDestinatarios().isEmpty()) {
				modeloComunicacao = comunicacaoService.reabrirComunicacao(getModeloComunicacao());
				isNew = true;
				resetEntityState();
			}
			clear();
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.sucesso.exclusaoDestinatario"));
		} catch (DAOException e) {
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.exclusaoDestinatario"));
		} catch (CloneNotSupportedException e) {
			FacesMessages.instance().add(InfoxMessages.getInstance().get("comunicacao.msg.erro.recuperaModelo"));
		}
	}
	
    private ModeloComunicacao getModeloEmElaboracao() {
        org.jbpm.taskmgmt.exe.TaskInstance taskInstance = getTaskInstance();
        if (taskInstance != null) {
            ContextInstance context = taskInstance.getContextInstance();
            return (ModeloComunicacao) context.getVariable(ComunicacaoService.COMUNICACAO_EM_ELABORACAO);
        }
        return null;
    }

	public List<Localizacao> getLocalizacoesDisponiveisAssinatura(String query) {
		return localizacaoSearch.getLocalizacoesByRaizWithDescricaoLike(localizacaoRaizAssinaturaComunicacao, query, MAX_RESULTS);
	}

	public List<TipoComunicacao> getTiposComunicacao() {
		if (tiposComunicacao == null) {
			tiposComunicacao = tipoComunicacaoSearch.getTiposComunicacaoAtivosByUso(TipoUsoComunicacaoEnum.E);
		}
		return tiposComunicacao;
	}
	
	protected Localizacao getLocalizacaoRaizComunicacao() {
		return localizacaoRaizComunicacao;
	}
	
	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFinalizada() {
		return finalizada;
	}
	
	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
		if (!this.finalizada) {
			modeloComunicacao.setLocalizacaoResponsavelAssinatura(localizacaoAssinatura);
			modeloComunicacao.setPerfilResponsavelAssinatura(perfilAssinatura);
		}
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public boolean isExpedida() {
		if (expedida == null && modeloComunicacao.getFinalizada()) {
			expedida = modeloComunicacaoManager.isExpedida(modeloComunicacao);
		}
		return modeloComunicacao.getFinalizada() && expedida;
	}
	
	public boolean isComunicacaoSuficientementeAssinada() {
		if (destinatario != null && comunicacaoSuficientementeAssinada == null) {
			comunicacaoSuficientementeAssinada = assinaturaDocumentoService.isDocumentoTotalmenteAssinado(destinatario.getDocumentoComunicacao());
		}
		return comunicacaoSuficientementeAssinada;
	}
	
	public boolean podeRenderizarApplet() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
		UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
		DocumentoBin documento = null; 
		ClassificacaoDocumento classificacao = null;
		if (modeloComunicacao.isDocumentoBinario()) {
			Documento documentoComunicacao = modeloComunicacao.getDestinatarios().get(0).getDocumentoComunicacao();
			documento = documentoComunicacao.getDocumentoBin();
			classificacao = documentoComunicacao.getClassificacaoDocumento();
		} else {
			documento = destinatario.getDocumentoComunicacao().getDocumentoBin();
			classificacao = modeloComunicacao.getClassificacaoComunicacao();
		}
		return documento != null && assinaturaDocumentoService.podeRenderizarApplet(papel, classificacao, documento, usuario);
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
		clearAssinaturas();
	}

	private void clearAssinaturas() {
		this.comunicacaoSuficientementeAssinada = null;
	}

	public boolean isInTask() {
		return inTask;
	}
	
	public TipoComunicacao getTipoComunicacao() {
		return modeloComunicacao.getTipoComunicacao();
	}
	
	public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
		modeloComunicacao.setTipoComunicacao(tipoComunicacao);
		documentoComunicacaoAction.initClassificacoes();
		documentoComunicacaoAction.setModelosDocumento(null);
		modeloComunicacao.setClassificacaoComunicacao(null);
		modeloComunicacao.setModeloDocumento(null);
	}
	
	public boolean podeExibirBotaoVisualizarComunicacoes() {
		return modeloComunicacao.getFinalizada() && isExpedida() && modeloComunicacao.isDocumentoBinario();
	}
	
	public boolean podeVisualizarComunicacaoNaoFinalizada(){
		return modeloComunicacao.isDocumentoBinario() && documentoComunicacaoAction.isPossuiDocumentoInclusoPorUsuarioInterno() && !modeloComunicacao.getFinalizada();
	}
	
	public boolean isUsuarioLogadoNaLocalizacaoPerfilResponsavel() {
		modeloComunicacaoManager.refresh(modeloComunicacao);
		boolean usuarioLogadoNaLocalizacaoResponsavel = Authenticator.getLocalizacaoAtual().equals(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
		PerfilTemplate perfilUsuarioLogado = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate();
		PerfilTemplate perfilResponsavelAssinatura = modeloComunicacao.getPerfilResponsavelAssinatura();
		boolean usuarioLogadoNoPerfilResponsavel = perfilResponsavelAssinatura == null || perfilUsuarioLogado.equals(perfilResponsavelAssinatura);
		return usuarioLogadoNaLocalizacaoResponsavel && usuarioLogadoNoPerfilResponsavel;
	}
	
	public boolean isPrazoComunicacaoRequired(){
		return false;
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public boolean existeUsuarioLocalizacaoAssinatura() {
		return existeUsuarioLocalizacaoAssinatura;
	}
	
	public void verificaExistenciaUsuario() {
		if (getModeloComunicacao().getLocalizacaoResponsavelAssinatura() != null) {
			existeUsuarioLocalizacaoAssinatura = usuarioLoginSearch.existsUsuarioWithLocalizacaoPerfil(getModeloComunicacao().getLocalizacaoResponsavelAssinatura(),
					getModeloComunicacao().getPerfilResponsavelAssinatura());
		}
	}
	
	public boolean canChooseTipoComunicacao() {
		return !existeParametroTipoComunicacao && !getModeloComunicacao().getFinalizada();
	}
	
	public boolean canChooseResponsavelAssinatura() {
        return localizacaoAssinatura == null;
    }
    
    public boolean isExibirTransicoes() {
        return exibirTransicoes && getModeloComunicacao().getFinalizada();
    }

    protected boolean podeAssinar() {
        return !getModeloComunicacao().isDocumentoBinario() && isUsuarioLogadoNaLocalizacaoPerfilResponsavel() && 
                classificacaoDocumentoPapelManager.papelPodeAssinarClassificacao(Authenticator.getPapelAtual(), modeloComunicacao.getClassificacaoComunicacao());
    }
    
    protected boolean assinouComunicacao() {
        for (DestinatarioModeloComunicacao destinatario : getModeloComunicacao().getDestinatarios()) {
            if (!assinaturaDocumentoService.isDocumentoAssinado(destinatario.getDocumentoComunicacao().getDocumentoBin(),Authenticator.getPapelAtual(), 
                    Authenticator.getUsuarioLogado())) {
                return false;
            }
        }
        return true; 
    }

    public boolean isExibirResponsaveisAssinatura() {
        return exibirResponsaveisAssinatura;
    }
    
    public void endTask(String transition) {
        if (!TaskInstanceHome.instance().isTransitionValidateForm(transition) || !podeAssinar() || assinouComunicacao()) {
            TaskInstanceHome.instance().end(transition);
        } else {
            FacesMessages.instance().add("É necessário assinar a comunicação para continuar");
        }
    }
    
    public boolean isShowFormButtonsEnd() {
        return true;
    }
}
