package br.com.infox.ibpm.event;

import static br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais.PROCESSO;

import java.io.Serializable;
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
import br.com.infox.core.net.UrlBuilder;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cliente.dao.CalendarioEventosDAO;
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
import br.com.infox.ibpm.event.External.ExpressionType;
import br.com.infox.ibpm.sinal.SignalService;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.util.time.DateWrapper;

@Stateless
@Named(BpmExpressionService.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EppBpmExpressionService extends BpmExpressionService implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private MetadadoProcessoManager metadadoProcessoManager;

    @External(tooltip = "process.events.expression.atribuirCiencia.tooltip")
    public void atribuirCiencia() {
    	Integer idProcesso = (Integer) ExecutionContext.currentExecutionContext().getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
    	Processo comunicacao = processoManager.find(idProcesso);
        contabilizadorPrazo.atribuirCiencia(comunicacao);
    }

    @External(tooltip = "process.events.expression.atribuirCumprimento.tooltip")
    public void atribuirCumprimento() {
    	Integer idProcesso = (Integer) ExecutionContext.currentExecutionContext().getContextInstance().getVariable(VariaveisJbpmProcessosGerais.PROCESSO);
    	Processo comunicacao = processoManager.find(idProcesso);
        contabilizadorPrazo.atribuirCumprimento(comunicacao);
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.disponibilizarPastaParaParticipantesProcesso.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void disponibilizarPastaParaParticipantesProcesso(String descricaoPasta, Long idProcesso)
            throws DAOException {
        pastaManager.disponibilizarPastaParaParticipantesProcesso(descricaoPasta, idProcesso);
    }

    @External(value = {
            @Parameter(defaultValue = "'Nome da pasta'", label = "process.events.expression.param.nomePasta.label", tooltip = "process.events.expression.param.nomePasta.tooltip", selectable = true),
            @Parameter(defaultValue = PROCESSO, label = "process.events.expression.param.processo.label", tooltip = "process.events.expression.param.processo.tooltip") }, tooltip = "process.events.expression.tornarPastaPublica.tooltip")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void tornarPastaPublica(String nomePasta, Long processo) throws DAOException {
        pastaManager.tornarPastaPublica(nomePasta, processo);
    }
    
    @External(value = {
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
	
	@External(expressionType = ExpressionType.GERAL, value = {
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
	
	@External(expressionType = ExpressionType.RAIA_DINAMICA, 
        tooltip = "process.events.expression.toList.tooltip",
        example = "#{bpmExpressionService.toList(variavel).put(variavelLista).put(variavel2))}"
    )
    public Collection<Object> toList(Object object) {
	    return new ObjectCollection(object.getClass()).put(object);
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
            @Parameter(defaultValue = "'Nome da Tarefa'", selectable = true)
        }
    )
    public Date dataMaximaRespostaComunicacao(String taskName) throws DAOException {
        Integer idProcesso = getIdProcessoAtual();
        Date dataMaxima = prazoComunicacaoService.getDataMaximaRespostaComunicacao(idProcesso, taskName);
        return dataMaxima;
    }
	
	@External(expressionType = ExpressionType.GATEWAY,
	            tooltip = "process.events.expression.comunicacao.isPrazoProrrogadoENaoExpirado.tooltip",
	            example = "#{bpmExpressionService.isPrazoProrrogadoENaoExpirado(processo, 'Tarefa 1')}")
	public Boolean isPrazoProrrogadoENaoExpirado(Integer idProcesso, String taskName){
	    return prazoComunicacaoService.isPrazoProrrogadoENaoExpirado(idProcesso, taskName);
	}
	
	@External(expressionType = ExpressionType.GATEWAY,
                tooltip = "process.events.expression.comunicacao.possuiRespostaDiferenteProrrogacaoprazo.tooltip",
                example = "#{bpmExpressionService.possuiRespostaDiferenteProrrogacaoprazo(processo, 'Tarefa 1')}")
	public Boolean possuiRespostaDiferenteProrrogacaoprazo(Integer idProcesso, String taskName){
	    Boolean isProrrogacaoPrazo = Boolean.FALSE;
            return 0 < modeloComunicacaoSearch.countRespostasComunicacaoByProcessoAndTaskName(idProcesso, taskName, isProrrogacaoPrazo);
	}
	
	@External(expressionType = ExpressionType.GERAL,
        tooltip = "process.events.expression.getUsuarioComLogin.tooltip",
        value = {
            @Parameter(defaultValue = "'Login do usuário'", selectable = true)
        }
    )
    public UsuarioLogin getUsuarioComLogin(String login) throws DAOException {
	    UsuarioLogin usuarioLogin = null;
	    if (!StringUtil.isEmpty(login)) {
	        usuarioLogin = usuarioLoginManager.getUsuarioLoginByLogin(login);
	    }
        return usuarioLogin;
    }

   /**
     * Baseado no processo, procura a Entrega referente e verifica se o checklist respectivo tem
     * algum item marcado com {@link ChecklistSituacao} 'Não Conforme'.
     * @param Entrega entrega a ser considerada na EL
     * @return
     */
    @External(expressionType = ExpressionType.GATEWAY,
            tooltip = "process.events.expression.checklist.hasNaoConforme.tooltip",
            example = "#{bpmExpressionService.checklistHasItemNaoConforme('Documentos do Processo')}")
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
            @Parameter(defaultValue = "codigo", label = "process.events.expression.linkAplicacaoExterna.param.codigo.label", tooltip = "process.events.expression.param.linkAplicacaoExterna.codigo.tooltip"),
            @Parameter(defaultValue = "descricao", label = "process.events.expression.linkAplicacaoExterna.param.descricao.label", tooltip = "process.events.expression.param.linkAplicacaoExterna.codigo.tooltip"),
            @Parameter(defaultValue = "url", label = "process.events.expression.linkAplicacaoExterna.param.url.label", tooltip = "process.events.expression.param.linkAplicacaoExterna.codigo.tooltip") })
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
            @Parameter(defaultValue = "'nomeMetadado'", selectable = true)
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
            @Parameter(defaultValue = "'codigoClassificacaoDocumento'", selectable = true)
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

    @Override
    public List<ExternalMethod> getExternalMethods() {
    	return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GERAL);
    }

	@Override
	public List<ExternalMethod> getExternalRaiaDinamicaMethods() {
		return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.RAIA_DINAMICA);
	}

	@Override
	public List<ExternalMethod> getExternalGatewayMethods() {
	    return BpmExpressionServiceConsumer.instance().getExternalMethods(this, ExpressionType.GATEWAY);
	}
}
