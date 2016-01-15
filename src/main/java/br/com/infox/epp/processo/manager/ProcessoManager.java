package br.com.infox.epp.processo.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.util.Strings;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.sinal.SignalParam;
import br.com.infox.ibpm.sinal.SignalParam.Type;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.util.ComponentUtil;
import br.com.infox.util.time.DateRange;

@AutoCreate
@Stateless
@ContextDependency
@Name(ProcessoManager.NAME)
public class ProcessoManager extends Manager<ProcessoDAO, Processo> {

    private static final long serialVersionUID = 8095772422429350875L;
    public static final String NAME = "processoManager";
    private static final int PORCENTAGEM = 100;

    @In
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
    @In
    private DocumentoManager documentoManager;
    @In
    private GenericDAO genericDAO;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private MetadadoProcessoManager metadadoProcessoManager;
    @In
    private ParametroManager parametroManager;
    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private LocalizacaoManager localizacaoManager; 
    
    public Processo buscarPrimeiroProcesso(Processo p, TipoProcesso tipo) {
        for (Processo filho : p.getFilhos()) {
            if (filho.getDataFim() != null) {
                continue;
            }
            for (MetadadoProcesso metadado : filho.getMetadadoProcessoList()) {
                final Object value = metadado.getValue();
                if (value != null && value instanceof TipoProcesso && value.equals(tipo)) {
                    return filho;
                }
            }
            Processo neto = buscarPrimeiroProcesso(filho, tipo);
            if (neto != null) {
                return neto;
            }
        }
        return null;
    }

    public DocumentoBin createDocumentoBin(final Object value) throws DAOException {
        final DocumentoBin bin = new DocumentoBin();
        bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
        bin.setMd5Documento(MD5Encoder.encode(String.valueOf(value)));
        this.documentoBinManager.persist(bin);
        return bin;
    }

    private String getDescricaoModeloDocumentoByValue(final Object value) {
        String modeloDocumento = String.valueOf(value);
        if (Strings.isEmpty(modeloDocumento)) {
            modeloDocumento = " ";
        }
        return modeloDocumento;
    }

    public Object getAlteracaoModeloDocumento(final DocumentoBin documentoBinAtual, final Object value) {
        if (documentoBinAtual.getModeloDocumento() != null) {
            return documentoBinAtual.getModeloDocumento();
        } else {
            return value;
        }
    }

    public void visualizarTask(Processo processo, Long idTaskInstance, UsuarioPerfil usuarioPerfil) {
        final BusinessProcess bp = BusinessProcess.instance();
        if (!processo.getIdJbpm().equals(bp.getProcessId())) {
            bp.setProcessId(processo.getIdJbpm());
            bp.setTaskId(idTaskInstance);
        }
    }

    private void iniciaTask(Processo processo, Long taskInstanceId) {
        BusinessProcess bp = BusinessProcess.instance();
        bp.setProcessId(processo.getIdJbpm());
        bp.setTaskId(taskInstanceId);
        if (bp.getProcessId() != null && bp.getTaskId() != null && bp.getProcessId().equals(processo.getIdJbpm())) {
        	TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
        	ManagedJbpmContext.instance().getSession().buildLockRequest(LockOptions.READ).setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).lock(taskInstance);
        	String currentActorId = Actor.instance().getId();
			if (taskInstance.getStart() == null) {
        		taskInstance.start(currentActorId);
        		taskInstance.setAssignee(currentActorId);
        	} else if (!StringUtil.isEmpty(taskInstance.getAssignee()) && !currentActorId.equals(taskInstance.getAssignee())) {
        		throw new BusinessRollbackException("Tarefa bloqueada por outro usuário");
        	} else {
        		taskInstance.setAssignee(currentActorId);
        	}
        	UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByLogin(currentActorId);
    		taskInstance.setVariableLocally(VariaveisJbpmProcessosGerais.OWNER, usuario.getNomeUsuario());
        }
    }

    public void iniciarTask(Processo processo, Long idTaskInstance, UsuarioPerfil usuarioPerfil) throws DAOException {
        iniciaTask(processo, idTaskInstance);
        storeUsuario(idTaskInstance, usuarioPerfil);
    }

    /**
     * Armazena o usuário que executou a tarefa. O jBPM mantem apenas os
     * usuários das tarefas em execução, apagando o usuário sempre que a tarefa
     * é finalizada (ver tabela jbpm_taskinstance, campo actorid_) Porém surgiu
     * a necessidade de armazenar os usuários das tarefas já finalizas para
     * exibir no histórico de Movimentação do Processo
     *
     * @param idTaskInstance
     * @param actorId
     * @throws DAOException
     * */
    private void storeUsuario(Long idTaskInstance, UsuarioPerfil usuarioPerfil) throws DAOException {
        if (this.genericDAO.find(UsuarioTaskInstance.class, idTaskInstance) == null) {
            this.genericDAO.persist(new UsuarioTaskInstance(idTaskInstance, usuarioPerfil));
        }
    }

    public void atualizarProcessos() {
        getDao().atualizarProcessos();
    }

    public boolean checkAccess(final int idProcesso, final Integer idUsuarioLogado, final Long idTask) {
        return getDao().findProcessosByIdProcessoAndIdUsuario(idProcesso, idUsuarioLogado, idTask) != null;
    }

    public String getNumeroProcesso(final int idProcesso) {
        final Processo processo = find(idProcesso);
        if (processo != null) {
            return processo.getNumeroProcesso();
        }
        return String.valueOf(idProcesso);
    }

    public Processo criarProcesso(NaturezaCategoriaFluxo natcf) throws DAOException {
		Processo processo = criarProcesso(natcf, Authenticator.getLocalizacaoAtual(), Authenticator.getUsuarioLogado());
		return persistProcessoComNumero(processo);
	}
    
    public Processo criarProcessoSistema(NaturezaCategoriaFluxo natcf, Processo processoPai) throws DAOException {
		Processo processo = criarProcesso(natcf, processoPai.getLocalizacao(), usuarioLoginManager.getUsuarioDeProcessosDoSistema());
		processo.setProcessoPai(processoPai);
		return persistProcessoComNumero(processo);
	}
    
    private Processo criarProcesso(NaturezaCategoriaFluxo natcf, Localizacao localizacao, UsuarioLogin usuario) throws DAOException {
    	Processo processo = new Processo();
    	processo.setDataInicio(Calendar.getInstance().getTime());
    	processo.setNaturezaCategoriaFluxo(natcf);
    	processo.setLocalizacao(localizacao);
    	processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
    	processo.setNumeroProcesso("");
    	processo.setUsuarioCadastro(usuario);
    	return processo;
    }
    
    public String getNumeroProcessoByIdJbpm(final Long processInstanceId) {
        return getDao().getNumeroProcessoByIdJbpm(processInstanceId);
    }

    public Processo getProcessoByNumero(final String numeroProcesso) {
        return getDao().getProcessoByNumero(numeroProcesso);
    }

    public List<Processo> listAllNotEnded() {
        return getDao().listAllNotEnded();
    }

    public List<Processo> listNotEnded(final Fluxo fluxo) {
        return getDao().listNotEnded(fluxo);
    }

    public void updateTempoGastoProcessoEpa() throws DAOException {
        final List<Processo> listAllNotEnded = listAllNotEnded();
        for (final Processo processo : listAllNotEnded) {
            final Map<String, Object> result = getDao().getTempoGasto(processo);
            if (result != null) {
                DateRange dateRange;
                final Date dataInicio = processo.getDataInicio();
                final Date dataFim = processo.getDataFim();
                if (dataFim != null) {
                    dateRange = new DateRange(dataInicio, dataFim);
                } else {
                    dateRange = new DateRange(dataInicio, new Date());
                }
                processo.setTempoGasto(new Long(dateRange.get(DateRange.DAYS)).intValue());
                if (processo.getPorcentagem() > ProcessoManager.PORCENTAGEM) {
                    processo.setSituacaoPrazo(SituacaoPrazoEnum.PAT);
                }
                getDao().update(processo);
            }
        }
    }

    public boolean hasPartes(final Long idJbpm) {
        return getDao().hasPartes(idJbpm);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        return getDao().getPessoaFisicaList();
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        return getDao().getPessoaJuridicaList();
    }

    public Double getMediaTempoGasto(final Fluxo fluxo, final SituacaoPrazoEnum prazoEnum) {
        return getDao().getMediaTempoGasto(fluxo, prazoEnum);
    }

    public Processo getProcessoEpaByNumeroProcesso(final String numeroProcesso) {
        Processo processo = null;
        if (numeroProcesso != null) {
            processo = getDao().getProcessoEpaByNumeroProcesso(numeroProcesso);
        }
        return processo;
    }

    private Processo persistProcessoComNumero(final Processo processo) throws DAOException {
        Processo p = getDao().persistProcessoComNumero(processo);
        return p;
    }

	public void removerProcessoJbpm(Processo processo) throws DAOException {
    	Long idJbpm = processo.getIdJbpm();
    	if (idJbpm == null) throw new DAOException("Processo sem tarefa no Jbpm");
    	Object[] ids = getDao().getIdTaskMgmInstanceAndIdTokenByidJbpm(idJbpm);
    	Long idTaskMgmInstance = ((Number) ids[0]).longValue();
    	Long idToken = ((Number) ids[1]).longValue();
    	getDao().removerProcessoJbpm(processo.getIdProcesso(), idJbpm, idTaskMgmInstance, idToken);
    	processo.setIdJbpm(null);
    }
	
	public List<Processo> getProcessosFilhoNotEndedByTipo(Processo processo, String tipoProcesso) {
		return getDao().getProcessosFilhoNotEndedByTipo(processo, tipoProcesso);
    }
	
	public List<Processo> getProcessosFilhoByTipo(Processo processo, String tipoProcesso) {
		return getDao().getProcessosFilhosByTipo(processo, tipoProcesso);
    }
	
	public List<Processo> getProcessosByIdCaixa(Integer idCaixa) {
		return getDao().getProcessosByIdCaixa(idCaixa);
	}
	
	public List<Processo> listProcessosComunicacaoAguardandoCiencia() {
		return getDao().listProcessosComunicacaoAguardandoCiencia();
	}
	
	public List<Processo> listProcessosComunicacaoAguardandoCumprimento() {
		return getDao().listProcessosComunicacaoAguardandoCumprimento();
	}
	
	public Processo getProcessoEpaByIdJbpm(Long idJbpm) {
		return getDao().getProcessoEpaByIdJbpm(idJbpm);
	}
	
	public void movimentarProcessoJBPM(Long taskInstanceId, String transicao) throws DAOException {
		Long processIdOriginal = BusinessProcess.instance().getProcessId();
		Long taskIdOriginal = BusinessProcess.instance().getTaskId();
		BusinessProcess.instance().setProcessId(null);
		BusinessProcess.instance().setTaskId(null);
		TaskInstance taskInstanceForUpdate = ManagedJbpmContext.instance().getTaskInstanceForUpdate(taskInstanceId);
		taskInstanceForUpdate.end(transicao);
		BusinessProcess.instance().setProcessId(processIdOriginal);
		BusinessProcess.instance().setTaskId(taskIdOriginal);
	}
	
	public void cancelJbpmSubprocess(Long subProcessInstanceId, String transicao) throws DAOException {
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        ProcessInstance processInstanceForUpdate = ManagedJbpmContext.instance().getProcessInstanceForUpdate(subProcessInstanceId);
        processInstanceForUpdate.end(transicao);
        while (processInstanceForUpdate != null) {
            processInstanceForUpdate.getTaskMgmtInstance().cancelAll();
            processInstanceForUpdate = processInstanceForUpdate.getRootToken().getSubProcessInstance();
        }
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);
    }
	
	public void startJbpmProcess(String fluxoName, String transitionName, List<SignalParam> params) throws DAOException {
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        Fluxo fluxo = fluxoManager.getFluxoByDescricao(fluxoName);
        List<NaturezaCategoriaFluxo> naturezaCategoriaFluxo = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
        Processo processo = new Processo();
        processo.setDataInicio(new Date());
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        if (localizacao == null) {
            localizacao = localizacaoManager.getLocalizacaoByNome("e-PP");
        }
        processo.setLocalizacao(localizacao);
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo.get(0));
        UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        if (usuarioLogin == null) {
            usuarioLogin = usuarioLoginManager.getReference(0);
        }
        processo.setUsuarioCadastro(usuarioLogin);
        MetadadoProcessoProvider provider = new MetadadoProcessoProvider(processo);
        Map<String, Object> variaveis = new HashMap<>();
        List<MetadadoProcesso> metadados = new ArrayList<>();
        for (SignalParam signalParam : params) {
            if (signalParam.getType() == Type.VARIABLE) {
                variaveis.put(signalParam.getName(), signalParam.getParamValue());
            } else if (signalParam.getType() == Type.METADADO) {
                metadados.add(provider.gerarMetadado(signalParam.getName(), signalParam.getParamValue()));
            }
        }
        ComponentUtil.<IniciarProcessoService>getComponent(IniciarProcessoService.NAME).iniciarProcesso(processo, variaveis, metadados, transitionName);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);
    }

	@Observer({Event.EVENTTYPE_TASK_END})
	public void atualizarProcessoTarefa(ExecutionContext executionContext) throws DAOException {
		TaskInstance taskInstance = executionContext.getTaskInstance();
		ProcessoTarefa processoTarefa = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		if (processoTarefa != null) {
			processoTarefa.setDataFim(taskInstance.getEnd());
			processoTarefaManager.update(processoTarefa);
		}
	}
	
}