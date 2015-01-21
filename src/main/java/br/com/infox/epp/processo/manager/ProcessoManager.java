package br.com.infox.epp.processo.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.util.Strings;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.util.time.DateRange;

@Name(ProcessoManager.NAME)
@AutoCreate
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

    public DocumentoBin createDocumentoBin(final Object value) throws DAOException {
        final DocumentoBin bin = new DocumentoBin();
        bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
        bin.setMd5Documento(MD5Encoder.encode(String.valueOf(value)));
        this.documentoBinManager.persist(bin);
        return bin;
    }

    public Documento createDocumento(final Processo processo, final String label, final DocumentoBin bin,
            final ClassificacaoDocumento classificacaoDocumento) throws DAOException {
        return this.documentoManager.createDocumento(processo, label, bin, classificacaoDocumento);
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

    public void visualizarTask(final Processo processo, final Long idTarefa, final UsuarioPerfil usuarioPerfil) {
        final BusinessProcess bp = BusinessProcess.instance();
        if (!processo.getIdJbpm().equals(bp.getProcessId())) {
            final Long taskInstanceId = this.processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo,
                    idTarefa);
            bp.setProcessId(processo.getIdJbpm());
            bp.setTaskId(taskInstanceId);
        }
    }

    private void iniciaTask(Processo processo, Long taskInstanceId) {
        final BusinessProcess bp = BusinessProcess.instance();
        TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstance(taskInstanceId);
        bp.setProcessId(processo.getIdJbpm());
        bp.setTaskId(taskInstanceId);
        if (!processo.getIdJbpm().equals(bp.getProcessId()) && !taskInstance.isOpen()) {
            bp.startTask();
        }
    }

    public void iniciarTask(Processo processo, Long idTarefa, UsuarioPerfil usuarioPerfil) throws DAOException {
        Long taskInstanceId = getTaskInstanceId(usuarioPerfil, processo, idTarefa);
        if (taskInstanceId != null) {
            iniciaTask(processo, taskInstanceId);
            storeUsuario(taskInstanceId, usuarioPerfil.getUsuarioLogin(), usuarioPerfil.getPerfilTemplate()
                    .getLocalizacao(), usuarioPerfil.getPerfilTemplate().getPapel());
        }
    }

    private Long getTaskInstanceId(UsuarioPerfil usuarioPerfil, Processo processo, Long idTarefa) {
        MetadadoProcesso metadado = processo.getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
        if ( metadado != null && idTarefa != null) {
        	Map<String, Object> map = processoTarefaManager.findProcessoTarefaByIdProcessoAndIdTarefa(processo.getIdProcesso(), idTarefa.intValue());
        	return (Long) map.get("idTaskInstance");
        } else {
        	 if (idTarefa != null) {
                 return processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo, idTarefa);
             } else {
                 return processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo);
             }
        }
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
    private void storeUsuario(final Long idTaskInstance, final UsuarioLogin user, final Localizacao localizacao,
            final Papel papel) throws DAOException {
        if (this.genericDAO.find(UsuarioTaskInstance.class, idTaskInstance) == null) {
            this.genericDAO.persist(new UsuarioTaskInstance(idTaskInstance, user, localizacao, papel));
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

    public Processo persistProcessoComNumero(final Processo processo) throws DAOException {
        return getDao().persistProcessoComNumero(processo);
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
	
	public List<Processo> getProcessosByIdCaixa(Integer idCaixa) {
		return getDao().getProcessosByIdCaixa(idCaixa);
	}
	
	public List<Processo> listProcessosComunicacaoAguardandoCiencia() {
		return getDao().listProcessosComunicacaoAguardandoCiencia();
	}
	
	public List<Processo> listProcessosComunicacaoAguardandoCumprimento() {
		return getDao().listProcessosComunicacaoAguardandoCumprimento();
	}

}
