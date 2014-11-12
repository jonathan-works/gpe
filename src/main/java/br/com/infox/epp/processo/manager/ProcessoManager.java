package br.com.infox.epp.processo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.Actor;
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
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

@Name(ProcessoManager.NAME)
@AutoCreate
public class ProcessoManager extends Manager<ProcessoDAO, Processo> {

    private static final long serialVersionUID = 8095772422429350875L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoManager.class);
    public static final String NAME = "processoManager";

    @In
    private ProcessoEpaDAO processoEpaDAO;
    @In
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
    @In
    private DocumentoManager documentoManager;
    @In
    private GenericDAO genericDAO;
    @In
    private DocumentoBinManager documentoBinManager;

    public DocumentoBin createDocumentoBin(Object value) throws DAOException {
        DocumentoBin bin = new DocumentoBin();
        bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
        bin.setMd5Documento(MD5Encoder.encode(String.valueOf(value)));
        documentoBinManager.persist(bin);
        return bin;
    }

    public Documento createDocumento(Processo processo,
            String label, DocumentoBin bin,
            ClassificacaoDocumento classificacaoDocumento) throws DAOException {
        return documentoManager.createDocumento(processo, label, bin, classificacaoDocumento);
    }

    private String getDescricaoModeloDocumentoByValue(Object value) {
        String modeloDocumento = String.valueOf(value);
        if (Strings.isEmpty(modeloDocumento)) {
            modeloDocumento = " ";
        }
        return modeloDocumento;
    }

    public Object getAlteracaoModeloDocumento(DocumentoBin documentoBinAtual, Object value) {
        if (documentoBinAtual.getModeloDocumento() != null) {
            return documentoBinAtual.getModeloDocumento();
        } else {
            return value;
        }
    }

    public boolean hasPartes(Processo processo) {
        return processoEpaDAO.hasPartes(processo);
    }

    public void visualizarTask(final Processo processo, final Long idTarefa, final UsuarioPerfil usuarioPerfil) {
        final BusinessProcess bp = BusinessProcess.instance();
        if (!processo.getIdJbpm().equals(bp.getProcessId())) {
            final Long taskInstanceId = processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo, idTarefa);
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
            } catch (IllegalStateException e) {
                // Caso já exista deve-se ignorar este trecho, outras
                // illegalstate devem ser averiguadas
                // TODO Ideal para processos já iniciados seria chamar o método
                // resumeTask
                LOG.warn(".iniciaTask()", e);
            }
        }
        return result;
    }

    public void iniciarTask(final Processo processo, final Long idTarefa,
            final UsuarioPerfil usuarioPerfil) throws DAOException {
        final Long taskInstanceId = getTaskInstanceId(usuarioPerfil, processo, idTarefa);
        final String actorId = Actor.instance().getId();
        if (taskInstanceId != null) {
            iniciaTask(processo, taskInstanceId);
            storeUsuario(taskInstanceId, usuarioPerfil.getUsuarioLogin(), usuarioPerfil.getPerfilTemplate().getLocalizacao(), usuarioPerfil.getPerfilTemplate().getPapel());
            vinculaUsuario(processo, actorId);
        }
    }

    @Deprecated
    private void vinculaUsuario(Processo processo, String actorId) throws DAOException {
//        processo.setActorId(actorId);
        processo = merge(processo);
        flush();
    }

    private Long getTaskInstanceId(final UsuarioPerfil usuarioPerfil,
            final Processo processo, final Long idTarefa) {
        Long result;
        if (idTarefa != null) {
            result = processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo, idTarefa);
        } else {
            result = processoLocalizacaoIbpmDAO.getTaskInstanceId(usuarioPerfil, processo);
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
    private void storeUsuario(final Long idTaskInstance,
            final UsuarioLogin user, final Localizacao localizacao,
            final Papel papel) throws DAOException {
        if (genericDAO.find(UsuarioTaskInstance.class, idTaskInstance) == null) {
            genericDAO.persist(new UsuarioTaskInstance(idTaskInstance, user, localizacao, papel));
        }
    }

    public void moverProcessosParaCaixa(List<Integer> idList, Caixa caixa) throws DAOException {
        getDao().moverProcessosParaCaixa(idList, caixa);
    }

    public void moverProcessoParaCaixa(Caixa caixa, Processo processo) throws DAOException {
        getDao().moverProcessoParaCaixa(caixa, processo);
    }

    public void moverProcessoParaCaixa(List<Caixa> caixaList, Processo processo) throws DAOException {
        Caixa caixaEscolhida = escolherCaixaParaAlocarProcesso(caixaList);
        getDao().moverProcessoParaCaixa(caixaEscolhida, processo);
    }

    /*
     * Atualmente a regra para escolher a caixa é simplesmente pegar a primeira
     */
    private Caixa escolherCaixaParaAlocarProcesso(List<Caixa> caixaList) {
        return caixaList.get(0);
    }

    public void removerProcessoDaCaixaAtual(Processo processo) throws DAOException {
        getDao().removerProcessoDaCaixaAtual(processo);
    }

    public void apagarActorIdDoProcesso(Processo processo) throws DAOException {
        getDao().apagarActorIdDoProcesso(processo);
    }

    public void atualizarProcessos() {
        getDao().atualizarProcessos();
    }

    public boolean checkAccess(int idProcesso, String login) {
        return !getDao().findProcessosByIdProcessoAndActorId(idProcesso, login).isEmpty();
    }

    public String getNumeroProcesso(int idProcesso) {
        Processo processo = find(idProcesso);
        if (processo != null) {
            return processo.getNumeroProcesso();
        }
        return String.valueOf(idProcesso);
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
    
    public String getNumeroProcessoByIdJbpm(Long processInstanceId) {
        return getDao().getNumeroProcessoByIdJbpm(processInstanceId);
    }
    
    public Processo getProcessoByNumero(String numeroProcesso) {
    	return getDao().getProcessoByNumero(numeroProcesso);
    }
}
