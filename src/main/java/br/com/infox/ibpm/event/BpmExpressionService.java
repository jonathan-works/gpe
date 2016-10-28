package br.com.infox.ibpm.event;

import static br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais.PROCESSO;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.componentes.reflection.Reflection;
import br.com.infox.core.net.UrlBuilder;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cliente.dao.CalendarioEventosDAO;
import br.com.infox.epp.documento.publicacao.LocalPublicacao;
import br.com.infox.epp.documento.publicacao.LocalPublicacaoSearch;
import br.com.infox.epp.documento.publicacao.PublicacaoDocumento;
import br.com.infox.epp.documento.publicacao.PublicacaoDocumentoService;
import br.com.infox.epp.entrega.EntregaResponsavelService;
import br.com.infox.epp.entrega.checklist.ChecklistSituacao;
import br.com.infox.epp.entrega.checklist.ChecklistVariableService;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacaoSearch;
import br.com.infox.epp.processo.comunicacao.prazo.ContabilizadorPrazo;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.processo.linkExterno.LinkAplicacaoExterna;
import br.com.infox.epp.processo.linkExterno.LinkAplicacaoExternaService;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.epp.relacionamentoprocessos.RelacionamentoProcessoManager;
import br.com.infox.epp.relacionamentoprocessos.TipoRelacionamentoProcessoManager;
import br.com.infox.epp.system.custom.variables.CustomVariableSearch;
import br.com.infox.ibpm.event.External.ExpressionType;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.util.time.DateWrapper;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BpmExpressionService {

    public static String NAME = "bpmExpressionService";

    private ContabilizadorPrazo contabilizadorPrazo = BeanManager.INSTANCE.getReference(ContabilizadorPrazo.class);
    private PastaManager pastaManager = BeanManager.INSTANCE.getReference(PastaManager.class);
    @Inject
    protected SignalService signalService;
    @Inject
    protected DocumentoManager documentoManager;
    @Inject
    protected PrazoComunicacaoService prazoComunicacaoService;
    @Inject
    private ModeloComunicacaoSearch modeloComunicacaoSearch;
    @Inject 
    protected UsuarioLoginManager usuarioLoginManager;
    @Inject
    protected ChecklistVariableService checklistVariableService;
    @Inject
    protected ProcessoManager processoManager;
    @Inject
    protected EntregaResponsavelService entregaResponsavelService;
    @Inject
    private TipoRelacionamentoProcessoManager tipoRelacionamentoProcessoManager;
    @Inject
    private RelacionamentoProcessoManager relacionamentoProcessoManager;
    @Inject
    private LinkAplicacaoExternaService linkAplicacaoExternaService;
    @Inject
    protected CalendarioEventosDAO calendarioEventosDAO;
    @Inject
    private CustomVariableSearch customVariableSearch;
    @Inject
    protected MetadadoProcessoManager metadadoProcessoManager;
    @Inject
    private LocalPublicacaoSearch localPublicacaoSearch;
    @Inject
    private PublicacaoDocumentoService publicacaoDocumentoService;

    @External(tooltip = "process.events.expression.atribuirCiencia.tooltip", expressionType = ExpressionType.EVENTOS)
    public void atribuirCiencia() {
    	Integer idProcesso = (Integer) ExecutionContext.currentExecutionContext().getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
    	Processo comunicacao = processoManager.find(idProcesso);
        contabilizadorPrazo.atribuirCiencia(comunicacao);
    }

    @External(tooltip = "process.events.expression.atribuirCumprimento.tooltip", expressionType = ExpressionType.EVENTOS)
    public void atribuirCumprimento() {
    	Integer idProcesso = (Integer) ExecutionContext.currentExecutionContext().getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
    	Processo comunicacao = processoManager.find(idProcesso);
        contabilizadorPrazo.atribuirCumprimento(comunicacao);
    }

    @External(expressionType = ExpressionType.EVENTOS, value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.disponibilizarPastaParaParticipantesProcesso.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso)
            throws DAOException {
        pastaManager.disponibilizarPastaParaParticipantesProcesso(descricaoPasta, idProcesso);
    }

    @External(expressionType = ExpressionType.EVENTOS, value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.tornarPastaPublica.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void tornarPastaPublica(String nomePasta, Long processo) throws DAOException {
        pastaManager.tornarPastaPublica(nomePasta, processo);
    }
    
    @External(expressionType = ExpressionType.EVENTOS, value = {
            @Parameter(defaultValue = "'Código do Sinal'", label = "process.events.expression.param.codigoSinal.label", tooltip = "process.events.expression.param.codigoSinal.tooltip", selectable = true)
            })
    public void dispatchSignal(String codigoSinal) throws DAOException {
        signalService.dispatch(codigoSinal);
    }
    
	@External(expressionType = ExpressionType.RAIA_DINAMICA, 
		tooltip = "process.events.expression.stringListBuilder.tooltip",
		example = "#{bpmExpressionService.stringListBuilder().add(variavelString).add('string').add(variavelListaString).build()}"
	)
    public StringListBuilder stringListBuilder() {
		return new StringListBuilder();
    }
	
	@External(expressionType = ExpressionType.GERAL, 
			tooltip = "process.events.expression.objectMapBuilder.tooltip",
			example = "#{bpmExpressionService.objectMapBuilder().add(chave,valor).add(chave,valor).build()}"
		)
    public ObjectMapBuilder objectMapBuilder() {
		return new ObjectMapBuilder();
    }
	
	@External(expressionType = ExpressionType.GATEWAY, value = {
        @Parameter(defaultValue = "'Nome variável editor/upload'", label = "process.events.expression.param.suficientementeAssinado.label", 
                tooltip = "process.events.expression.param.suficientementeAssinado.tooltip", selectable = true)
    })
    public boolean isDocumentoSuficientementeAssinado(Integer idDocumento) throws DAOException {
	    boolean suficientementeAssinado = false;
	    if (idDocumento != null) {
	        Documento documento = documentoManager.find(idDocumento);
	        if (documento != null) {
	            suficientementeAssinado = documento.getDocumentoBin().getSuficientementeAssinado();
	        }
	    }
	    return suficientementeAssinado;
    }
	
	@SuppressWarnings("unchecked")
    @External(expressionType = ExpressionType.GERAL,
        tooltip = "process.events.expression.toList.tooltip",
        example = "#{bpmExpressionService.toList(variavel).put(variavelLista).put(variavel2))}"
    )
    public Collection<Object> toList(Object object) {
		if(object == null)
			return null;
		if(object instanceof Collection<?>)
			return (Collection<Object>)object;
	    return new ObjectCollection(object.getClass()).put(object);
    }
	
	@External(expressionType = ExpressionType.GERAL, 
	        tooltip = "process.events.expression.toList.listExecuteMethod",
	        example = "#{bpmExpressionService.listExecuteMethod(colecao<Pasta>,'getNome')}"
	 )
    public String listExecuteMethod(Collection<?> colecao,String methodNameChain){
    	StringBuilder result = new StringBuilder();
    	for (Object obj : colecao) {
    		List<String> methodName = new ArrayList<String>();
    		if(methodNameChain == null || methodNameChain.trim().isEmpty())
    			methodName.add("toString") ;
    		else{
    			if(methodNameChain.contains("."))
    				methodName = Arrays.asList(methodNameChain.split("\\.")) ;
    			else
    				methodName.add(methodNameChain);
    		}
			try {
				for (int i = 0; i < methodName.size(); i++) {
					if(methodName.get(i).trim().isEmpty())
						continue;
					Reflection reflection = new Reflection(obj.getClass());
					Method declaredMethod = reflection.getMethod(methodName.get(i));
					obj = declaredMethod.invoke(obj);
					if(i != methodName.size() -1 )
						continue;
					if (obj != null) {
						if(!result.toString().isEmpty())
							result.append(", ");
						result.append(obj.toString());
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new BusinessException(
						"Método " + methodName + "() não encontrado em: " + obj.getClass().getSimpleName(), e);
			}
		}
    	return result.toString();
    }
    
	
	@External(expressionType = ExpressionType.GERAL,
        tooltip = "process.events.expression.dataMaximaRespostaComunicacao.tooltip"
	)
    public Date dataMaximaRespostaComunicacao() throws DAOException {
	    return dataMaximaRespostaComunicacao(null);
    }
	
	@External(expressionType = ExpressionType.GERAL,
        tooltip = "process.events.expression.dataMaximaRespostaComunicacao.tooltip",
        value = {
            @Parameter(selectable = true, defaultValue = "'Nome da Tarefa'", label = "Nome da Tarefa", tooltip = "Nome da tarefa originária da comunicação")
        }
    )
    public Date dataMaximaRespostaComunicacao(String taskName) throws DAOException {
        Integer idProcesso = getIdProcessoAtual();
        Date dataMaxima = prazoComunicacaoService.getDataMaximaRespostaComunicacao(idProcesso, taskName);
        return dataMaxima;
    }
	
	@External(expressionType = ExpressionType.GATEWAY, tooltip = "process.events.expression.comunicacao.isPrazoProrrogadoENaoExpirado.tooltip",
        example = "#{bpmExpressionService.isPrazoProrrogadoENaoExpirado(processo, 'Tarefa 1')}",
        value = {
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip"),
            @Parameter(defaultValue = "Nome da Tarefa", selectable = true, label = "Nome da Tarefa", tooltip = "Nome da tarefa originária da comunicação")
        }
	)
	public Boolean isPrazoProrrogadoENaoExpirado(Integer idProcesso, String taskName){
	    return prazoComunicacaoService.isPrazoProrrogadoENaoExpirado(idProcesso, taskName);
	}
	
	@External(expressionType = ExpressionType.GATEWAY,
        tooltip = "process.events.expression.comunicacao.possuiRespostaDiferenteProrrogacaoprazo.tooltip",
        example = "#{bpmExpressionService.possuiRespostaDiferenteProrrogacaoprazo(processo, 'Tarefa 1')}",
        value = {
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip"),
            @Parameter(defaultValue = "Nome da Tarefa", selectable = true, label = "Nome da Tarefa", tooltip = "Nome da tarefa originária da comunicação")
        }
	)
	public Boolean possuiRespostaDiferenteProrrogacaoprazo(Integer idProcesso, String taskName){
	    Boolean isProrrogacaoPrazo = Boolean.FALSE;
            return 0 < modeloComunicacaoSearch.countRespostasComunicacaoByProcessoAndTaskName(idProcesso, taskName, isProrrogacaoPrazo);
	}
	
	@External(expressionType = ExpressionType.GERAL,
        tooltip = "process.events.expression.getUsuarioComLogin.tooltip",
        value = {
            @Parameter(defaultValue = "'login'", selectable = true, label = "login", tooltip = "Login do usuário")
        }
    )
    public UsuarioLogin getUsuarioComLogin(String login) throws DAOException {
	    UsuarioLogin usuarioLogin = null;
	    if (!StringUtil.isEmpty(login)) {
	        usuarioLogin = usuarioLoginManager.getUsuarioLoginByLogin(login);
	    }
        return usuarioLogin;
    }

    protected Processo getProcessoAtual() {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        if (executionContext == null) {
            throw new BusinessRollbackException("O contexto de execução BPM não está disponível");
        }
        Integer idProcesso = (Integer) executionContext.getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
        if (idProcesso == null) {
            throw new BusinessRollbackException("Não foi encontrada variável 'processo'");
        }
        return processoManager.find(idProcesso);
    }

   /**
     * Baseado no processo, procura a Entrega referente e verifica se o checklist respectivo tem
     * algum item marcado com {@link ChecklistSituacao} 'Não Conforme'.
     * @param Entrega entrega a ser considerada na EL
     * @return
     */
    @External(expressionType = ExpressionType.GATEWAY,
        tooltip = "process.events.expression.checklist.hasNaoConforme.tooltip",
        example = "#{bpmExpressionService.checklistHasItemNaoConforme('Documentos do Processo')}",
        value = {
            @Parameter(selectable = true, defaultValue = "'Nome da Pasta'", label = "Nome da Pasta", tooltip = "Nome da pasta que o checklist está baseado")
        })
    public Boolean checklistHasItemNaoConforme(String nomePasta) {
        return checklistVariableService.existeItemNaoConforme(nomePasta);
    }

    @External(expressionType = ExpressionType.GERAL, tooltip = "teste",
            value = {
                    @Parameter(defaultValue = "entrega", selectable = false, label = "Entrega", tooltip = "Entrega de Documentos que irá prover os Responsáveis")
            })
    public void addParticipanteFromResponsavelEntrega(Entrega entrega) {
        Integer idProcesso = getIdProcessoAtual();
        Processo processo = EntityManagerProducer.getEntityManager().find(Processo.class, idProcesso);
        entregaResponsavelService.adicionaParticipantes(processo, entrega);
    }
    
    @External(expressionType = ExpressionType.GERAL,
    		tooltip = "process.events.expression.relacionarProcessosPorMetadados.tooltip", value = {
    		@Parameter(defaultValue = "tipoRelacionamento", label = "process.events.expression.param.relacionamento.tipoRelacionamento.label", tooltip = "process.events.expression.param.relacionamento.tipoRelacionamento.tooltip"),
    		@Parameter(defaultValue = "motivo", label = "process.events.expression.param.relacionamento.motivo.label", tooltip = "process.events.expression.param.relacionamento.motivo.tooltip"),
            @Parameter(defaultValue = "metadados", label = "process.events.expression.param.relacionamento.metadados.label", tooltip = "process.events.expression.param.relacionamento.metadados.tooltip") 
	})
    public void relacionarProcessosPorMetadados(String tipoRelacionamento, String motivo, Map<String, Object> metadados) {
    	Integer idProcesso = getIdProcessoAtual();
        
        Map<String, Object> parametrosMetadados = new HashMap<String, Object>();
        parametrosMetadados.putAll(metadados);

        TipoRelacionamentoProcesso tipoRelacionamentoProcesso = tipoRelacionamentoProcessoManager.findByCodigo(tipoRelacionamento);
        
        relacionamentoProcessoManager.relacionarProcessosPorMetadados(idProcesso, tipoRelacionamentoProcesso, motivo, parametrosMetadados);
    }

    @External(expressionType = ExpressionType.GERAL,
    		tooltip = "process.events.expression.relacionarProcessosPorNaturezaCategoriaMetadados.tooltip", value = {
    		@Parameter(defaultValue = "tipoRelacionamento", label = "process.events.expression.param.relacionamento.tipoRelacionamento.label", tooltip = "process.events.expression.param.relacionamento.tipoRelacionamento.tooltip"),
    		@Parameter(defaultValue = "motivo", label = "process.events.expression.param.relacionamento.motivo.label", tooltip = "process.events.expression.param.relacionamento.motivo.tooltip"),
    		@Parameter(defaultValue = "codigoNatureza", label = "process.events.expression.param.relacionamento.codigoNatureza.label", tooltip = "process.events.expression.param.relacionamento.codigoNatureza.tooltip"),
    		@Parameter(defaultValue = "codigoCategoria", label = "process.events.expression.param.relacionamento.codigoCategoria.label", tooltip = "process.events.expression.param.relacionamento.codigoCategoria.tooltip"),
            @Parameter(defaultValue = "metadados", label = "process.events.expression.param.relacionamento.metadados.label", tooltip = "process.events.expression.param.relacionamento.metadados.tooltip") 
	})
    public void relacionarProcessosPorNaturezaCategoriaMetadados(String tipoRelacionamento, String motivo, String codigoNatureza, String codigoCategoria, Map<String, Object> metadados) {
    	Integer idProcesso = getIdProcessoAtual();
        
        Map<String, Object> parametrosMetadados = new HashMap<String, Object>();
        parametrosMetadados.putAll(metadados);

        TipoRelacionamentoProcesso tipoRelacionamentoProcesso = tipoRelacionamentoProcessoManager.findByCodigo(tipoRelacionamento);
        
        if(codigoNatureza.isEmpty()) {
        	codigoNatureza = null;
        }
        if(codigoCategoria.isEmpty()) {
        	codigoCategoria = null;
        }
        
        relacionamentoProcessoManager.relacionarProcessosPorNaturezaCategoriaMetadados(idProcesso, tipoRelacionamentoProcesso, motivo, codigoNatureza, codigoCategoria, parametrosMetadados);
    }

	private Integer getIdProcessoAtual() {
		ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable("processo");
		return idProcesso;
	}
    
    @External(expressionType = ExpressionType.GERAL, tooltip = "process.events.expression.urlBuilder.tooltip", example = "#{bpmExpressionService.urlBuilder(baseUrl).path(variavelString).query('chave','valor').path('string').query('chave2',variavelListaString).build()}", value = {
            @Parameter(defaultValue = "urlBase", label = "process.events.expression.urlBuilder.param.urlBase.label", tooltip = "process.events.expression.urlBuilder.param.urlBase.tooltip", selectable = false),
            @Parameter(defaultValue = "urlBase", label = "process.events.expression.urlBuilder.param.path.label", tooltip = "process.events.expression.urlBuilder.param.path.tooltip", selectable = false),
            @Parameter(defaultValue = "urlBase", label = "process.events.expression.urlBuilder.param.query.label", tooltip = "process.events.expression.urlBuilder.param.query.tooltip", selectable = false)})
    public UrlBuilder urlBuilder(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    @External(expressionType = ExpressionType.GERAL
            , example ="#{bpmExpressionService.criarLink(variavelCodigo, 'Aplicacao externa X', bpmExpressionService.urlBuilder(baseUrl).path(variavelString).query('chave','valor').build())}", tooltip = "process.events.expression.linkAplicacaoExterna.tooltip", value = {
            @Parameter(defaultValue = "codigo", label = "process.events.expression.linkAplicacaoExterna.param.codigo.label", tooltip = "process.events.expression.linkAplicacaoExterna.param.codigo.tooltip"),
            @Parameter(defaultValue = "descricao", label = "process.events.expression.linkAplicacaoExterna.param.descricao.label", tooltip = "process.events.expression.linkAplicacaoExterna.param.descricao.tooltip"),
            @Parameter(defaultValue = "url", label = "process.events.expression.linkAplicacaoExterna.param.url.label", tooltip = "process.events.expression.linkAplicacaoExterna.param.url.tooltip") })
    public void criarLink(String codigo, String descricao, String url) {
        Integer idProcesso = getIdProcessoAtual();
        Processo processo = EntityManagerProducer.getEntityManager().find(Processo.class, idProcesso);

        LinkAplicacaoExterna link = new LinkAplicacaoExterna();
        link.setCodigo(codigo);
        link.setDescricao(descricao);
        link.setUrl(url);
        link.setProcesso(processo);
        try {
            linkAplicacaoExternaService.salvar(link);
        } catch (DAOException e){
            if (!GenericDatabaseErrorCode.UNIQUE_VIOLATION.equals(e.getDatabaseErrorCode())){
                throw e;
            }
        }
    }
    
    @External(expressionType = ExpressionType.GERAL
            , example ="#{bpmExpressionService.dateAdd(type, date, amount, util)}", tooltip = "process.events.expression.dateAdd.tooltip", value = {
            @Parameter(defaultValue = "type", label = "process.events.expression.dateAdd.param.type.label", tooltip = "process.events.expression.dateAdd.param.type.tooltip"),
            @Parameter(defaultValue = "date", label = "process.events.expression.dateAdd.param.date.label", tooltip = "process.events.expression.dateAdd.param.date.tooltip"),
            @Parameter(defaultValue = "amount", label = "process.events.expression.dateAdd.param.amount.label", tooltip = "process.events.expression.dateAdd.param.amount.tooltip"),
            @Parameter(defaultValue = "util", label = "process.events.expression.dateAdd.param.util.label", tooltip = "process.events.expression.dateAdd.param.util.tooltip")})
    public DateWrapper dateAdd(String type, Date date, int amount, boolean util) {
    	if (util) {
    		return new DateWrapper(calendarioEventosDAO.dataUtilAdd(type, date, amount));
    	} else {
    		DateWrapper dateWrapper = new DateWrapper(date);
    		if ("day".equals(type)) {
    			return dateWrapper.plusDays(amount);
    		} else if ("month".equals(type)) {
    			return dateWrapper.plusMonths(amount);
    		} else if ("year".equals(type)) {
    			return dateWrapper.plusYears(amount);
    		} else {
    			throw new IllegalArgumentException("Valor do atributo type '" + type + "' não suportado");
    		}
    	}
    }

    @External(expressionType = ExpressionType.GERAL, tooltip = "Remove metadados do processo", value = {
            @Parameter(selectable = true, defaultValue = "'nomeMetadado'", label = "Nome", tooltip = "Nome do Metadado a ser removido")
    })
    public void removerMetadado(String nomeMetadado) {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        if (executionContext == null) {
            throw new BusinessException("ExecutionContext está nulo.");
        }
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
        Processo processo = processoManager.find(idProcesso);
        List<MetadadoProcesso> metadados = processo.getMetadadoList(nomeMetadado);
        for (MetadadoProcesso metadado : metadados) {
            metadadoProcessoManager.removeWithoutFlush(metadado);
            processo.getMetadadoProcessoList().remove(metadado);
        }
        metadadoProcessoManager.flush();;
    }

    @External(expressionType = ExpressionType.GATEWAY, tooltip = "Verifica se algum dos documentos em análise possui a classificação de documento parametrizada", value = {
        @Parameter(defaultValue = "'codigoClassificacaoDocumento'", selectable = true, label = "codigoClassificacaoDocumento", tooltip = "Código da Classificação de Documento")
    })
    public boolean documentoEmAnaliseTemClassificacao(String codigoClassificacao) {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        if (executionContext == null) {
            throw new BusinessException("ExecutionContext está nulo.");
        }
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
        Processo processo = processoManager.find(idProcesso);
        List<MetadadoProcesso> metadadoList = processo.getMetadadoList(EppMetadadoProvider.DOCUMENTO_EM_ANALISE);
        for (MetadadoProcesso metadado : metadadoList) {
            Documento documento = metadado.getValue();
            if (documento.getClassificacaoDocumento().getCodigoDocumento().equals(codigoClassificacao)) {
                return true;
            }
        }
        return false;
    }
    
    @External(expressionType = ExpressionType.GATEWAY,
    		tooltip = "process.events.expression.customExpression.tooltip",
            value = {
                @Parameter(defaultValue = "'codigoVariavel'", label = "process.events.expression.customExpression.param.codigo.label",
                		tooltip = "process.events.expression.customExpression.param.codigo.tooltip")
            },
            example = "#{bpmExpressionService.getVariavel('codigoVariavel')}")
    public Object getVariavel(String codigo) {
		return customVariableSearch.getCustomVariableByCodigo(codigo);
	}

    @External(expressionType = ExpressionType.GERAL, tooltip = "Publica um documento", value = {
            @Parameter(selectable = true, defaultValue = "idDocumento", label = "ID documento", tooltip = "ID do documento a ser publicado"),
            @Parameter(selectable = true, defaultValue = "'DO'", label = "Código do LocalPublicacao", tooltip = "Código do Local onde será feita a publicação"),
            @Parameter(selectable = true, defaultValue = "numeroPublicacao", label = "Número da publicação", tooltip = "Número da publicação"),
            @Parameter(selectable = true, defaultValue = "dataPublicacao", label = "Data da publicação", tooltip = "Data da publicação"),
            @Parameter(selectable = true, defaultValue = "paginaPublicacao", label = "Página da publicação", tooltip = "Número da página onde foi feita a publicação"),
            @Parameter(selectable = true, defaultValue = "observacoesPublicacao", label = "Observações da publicação", tooltip = "Observações da publicação"),
            @Parameter(selectable = true, defaultValue = "idCertidaoPublicacao", label = "ID da certidão de publicação", tooltip = "ID do documento que representa a certidão de publicação")
            
    })
    public void publicarDocumento(Integer idDocumento, String codigoLocalPublicacao, String numero, Date data, Integer pagina, String observacoes, Integer idCertidao) {
    	Documento documento = documentoManager.find(idDocumento);
    	LocalPublicacao localPublicacao = localPublicacaoSearch.findByCodigo(codigoLocalPublicacao);
    	
    	Documento certidao = null;
    	if(idCertidao != null) {
    		certidao = documentoManager.find(idCertidao);
    	}
    	
    	PublicacaoDocumento publicacao = PublicacaoDocumento.builder()
    		.documento(documento)
    		.local(localPublicacao)
    		.numero(numero)
    		.data(data)
    		.pagina(pagina)
    		.observacoes(observacoes)
    		.certidao(certidao)
    		.build();
    	
    	publicacaoDocumentoService.publicarDocumento(publicacao);
    }
    
    public List<ExternalMethod> getExternalMethods() {
    	return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GERAL);
    }

    public List<ExternalMethod> getExternalEventosMethods() {
        return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GERAL, ExpressionType.EVENTOS);
    }

	public List<ExternalMethod> getExternalRaiaDinamicaMethods() {
		return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.RAIA_DINAMICA);
	}

	public List<ExternalMethod> getExternalGatewayMethods() {
	    return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GATEWAY);
	}
}
