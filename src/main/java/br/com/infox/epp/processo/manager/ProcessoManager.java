package br.com.infox.epp.processo.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.painel.caixa.Caixa;
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
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoDefinition;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.util.time.DateRange;

@Name(ProcessoManager.NAME)
@AutoCreate
public class ProcessoManager extends Manager<ProcessoDAO, Processo> {

    private static final long serialVersionUID = 8095772422429350875L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoManager.class);
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

    public boolean iniciaTask(final Processo processo, final Long taskInstanceId) {
        boolean result = false;
        final BusinessProcess bp = BusinessProcess.instance();
        if (!processo.getIdJbpm().equals(bp.getProcessId())) {
            bp.setProcessId(processo.getIdJbpm());
            bp.setTaskId(taskInstanceId);
            try {
                bp.startTask();
                result = true;
            } catch (final IllegalStateException e) {
                // Caso já exista deve-se ignorar este trecho, outras
                // illegalstate devem ser averiguadas
                // TODO Ideal para processos já iniciados seria chamar o método
                // resumeTask
                ProcessoManager.LOG.warn(".iniciaTask()", e);
            }
        }
        return result;
    }

    public void iniciarTask(final Processo processo, final Long idTarefa, final UsuarioPerfil usuarioPerfil)
            throws DAOException {
        final Long taskInstanceId = getTaskInstanceId(usuarioPerfil, processo, idTarefa);
        if (taskInstanceId != null) {
            iniciaTask(processo, taskInstanceId);
            storeUsuario(taskInstanceId, usuarioPerfil.getUsuarioLogin(), usuarioPerfil.getPerfilTemplate()
                    .getLocalizacao(), usuarioPerfil.getPerfilTemplate().getPapel());
        }
    }

    private Long getTaskInstanceId(final UsuarioPerfil usuarioPerfil, final Processo processo, final Long idTarefa) {
        Long result;
        if (idTarefa != null) {
            result = this.processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo, idTarefa);
        } else {
            result = this.processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo);
        }
        return result;
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

    public void moverProcessosParaCaixa(final List<Integer> idList, final Caixa caixa) throws DAOException {
        getDao().moverProcessosParaCaixa(idList, caixa);
    }

    public void moverProcessoParaCaixa(final Caixa caixa, final Processo processo) throws DAOException {
        getDao().moverProcessoParaCaixa(caixa, processo);
    }

    public void moverProcessoParaCaixa(final List<Caixa> caixaList, final Processo processo) throws DAOException {
        final Caixa caixaEscolhida = escolherCaixaParaAlocarProcesso(caixaList);
        getDao().moverProcessoParaCaixa(caixaEscolhida, processo);
    }

    /*
     * Atualmente a regra para escolher a caixa é simplesmente pegar a primeira
     */
    private Caixa escolherCaixaParaAlocarProcesso(final List<Caixa> caixaList) {
        return caixaList.get(0);
    }

    public void removerProcessoDaCaixaAtual(final Processo processo) throws DAOException {
        getDao().removerProcessoDaCaixaAtual(processo);
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

    public void removerProcessoJbpm(final Processo processo) throws DAOException {
        final Long idJbpm = processo.getIdJbpm();
        if (idJbpm == null) {
            throw new DAOException("Processo sem tarefa no Jbpm");
        }
        final Object[] ids = getDao().getIdTaskMgmInstanceAndIdTokenByidJbpm(idJbpm);
        final Long idTaskMgmInstance = ((Number) ids[0]).longValue();
        final Long idToken = ((Number) ids[1]).longValue();
        getDao().removerProcessoJbpm(processo.getIdProcesso(), idJbpm, idTaskMgmInstance, idToken);
        processo.setIdJbpm(null);
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

    public Boolean podeInativarPartesDoProcesso(final Processo processo) {
        return getDao().podeInativarPartes(getDao().getProcessoEpaByProcesso(processo));
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

    public boolean hasPartes(final Processo processo) {
        return getDao().hasPartes(processo);
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

    public void distribuirProcesso(final Processo processo, final PessoaFisica relator,
            final UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        distribuirProcesso(processo, relator, unidadeDecisoraMonocratica, null);
    }

    public void distribuirProcesso(final Processo processo, final UnidadeDecisoraMonocratica unidadeDecisoraMonocratica)
            throws DAOException {
        distribuirProcesso(processo, null, unidadeDecisoraMonocratica, null);
    }

    public void distribuirProcesso(final Processo processo, final PessoaFisica relator,
            final UnidadeDecisoraMonocratica unidadeDecisoraMonocratica,
            final UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
        setRelator(processo, relator);
        setUnidadeDecisoraMonocratica(processo, unidadeDecisoraMonocratica);
        setUnidadeDecisoraColegiada(processo, unidadeDecisoraColegiada);
        getDao().update(processo);
    }

    private void setRelator(final Processo processo, final PessoaFisica relator) throws DAOException {
        if (relator != null) {
            addMetadadoProcesso(processo, EppMetadadoProvider.RELATOR, relator.getIdPessoa().toString());
        } else {
            removeMetadadoIfExists(processo, EppMetadadoProvider.RELATOR);
        }
    }

    private void removeMetadadoIfExists(final Processo processo, final MetadadoProcessoDefinition metadadoProcessoDefinition) throws DAOException {
        final MetadadoProcesso metadado = processo.getMetadado(metadadoProcessoDefinition);
        if (metadado != null) {
            this.metadadoProcessoManager.remove(metadado);
        }
    }

    private void setUnidadeDecisoraMonocratica(final Processo processo,
            final UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) throws DAOException {
        if (unidadeDecisoraMonocratica != null) {
            addMetadadoProcesso(processo, EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA, unidadeDecisoraMonocratica.getIdUnidadeDecisoraMonocratica()
                            .toString());
        } else {
            removeMetadadoIfExists(processo, EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA);
        }
    }

    private void setUnidadeDecisoraColegiada(final Processo processo,
            final UnidadeDecisoraColegiada unidadeDecisoraColegiada) throws DAOException {
        if (unidadeDecisoraColegiada != null) {
            addMetadadoProcesso(processo, EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA, unidadeDecisoraColegiada.getIdUnidadeDecisoraColegiada()
                            .toString());
        } else {
            removeMetadadoIfExists(processo, EppMetadadoProvider.UNIDADE_DECISORA_COLEGIADA);
        }
    }

    private void addMetadadoProcesso(final Processo processo, MetadadoProcessoDefinition metadadoProcessoDefinition,
            final String valor) {
        final MetadadoProcesso metadadoProcesso = new MetadadoProcesso();
        metadadoProcesso.setProcesso(processo);
        metadadoProcesso.setMetadadoType(metadadoProcessoDefinition.getMetadadoType());
        metadadoProcesso.setClassType(metadadoProcessoDefinition.getClassType());
        metadadoProcesso.setValor(valor);
        processo.getMetadadoProcessoList().add(metadadoProcesso);
    }

    public void distribuirProcesso(final Processo processo, final UnidadeDecisoraColegiada unidadeDecisoraColegiada)
            throws DAOException {
        distribuirProcesso(processo, null, null, unidadeDecisoraColegiada);
    }

    public void distribuirProcesso(final Processo processo) throws DAOException {
        distribuirProcesso(processo, null, null, null);
    }
}
