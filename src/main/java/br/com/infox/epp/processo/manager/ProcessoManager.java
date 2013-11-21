package br.com.infox.epp.processo.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.dao.ProcessoEpaDAO;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.tarefa.caixa.Caixa;
import br.com.infox.ibpm.jbpm.UsuarioTaskInstance;
import br.com.itx.util.Crypto;
import br.com.itx.util.EntityUtil;

@Name(ProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoManager extends GenericManager {
	
	private static final long serialVersionUID = 8095772422429350875L;
	private static final LogProvider LOG = Logging.getLogProvider(ProcessoManager.class);
	public static final String NAME = "processoManager";
	
	@In private ProcessoDAO processoDAO;
	@In private ProcessoEpaDAO processoEpaDAO;
	@In private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
	@In private ProcessoDocumentoManager processoDocumentoManager;
	@In private ProcessoDocumentoBinManager processoDocumentoBinManager;
	
	public ProcessoDocumentoBin createProcessoDocumentoBin(Object value, String certChain, String signature) {
		ProcessoDocumentoBin bin = new ProcessoDocumentoBin();
		bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
		bin.setDataInclusao(new Date());
		bin.setMd5Documento(Crypto.encodeMD5(String.valueOf(value)));
		bin.setUsuario(Authenticator.getUsuarioLogado());
		bin.setCertChain(certChain);
		bin.setSignature(signature);
		EntityUtil.getEntityManager().persist(bin);
		return bin;
	}
	
	public ProcessoDocumento createProcessoDocumento(Processo processo, String label, ProcessoDocumentoBin bin, TipoProcessoDocumento tipoProcessoDocumento) throws DAOException {
		return processoDocumentoManager.createProcessoDocumento(processo, label, bin, tipoProcessoDocumento);
	}

    private String getDescricaoModeloDocumentoByValue(Object value) {
		String modeloDocumento = String.valueOf(value);
		if (Strings.isEmpty(modeloDocumento)){
			modeloDocumento = " ";
		}
		return modeloDocumento;
	}
	
	/**
	 * Retorna, se houver, o novo valor do ModeloDocumento. Se nao houver, retorna o valor o valor
	 * inicial inalterado
	 * @param value - valor da variável modeloDocumento no contexto jBPM
	 * */
	public Object getAlteracaoModeloDocumento(ProcessoDocumentoBin processoDocumentoBinAtual, Object value) {
		if(processoDocumentoBinAtual.getModeloDocumento() != null) {
			return processoDocumentoBinAtual.getModeloDocumento();
		} else {
		    return value;
		}
	}
	
	public void addProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().add(processoConexo);
	}
	
	public void removeProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().remove(processoConexo);
	}
	
	public void addProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo) throws DAOException{
		processoAtual.getProcessoConexoListForIdProcessoConexo().add(processoConexo);
		update(processoAtual);
	}
	
	public void removeProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo) throws DAOException{
		processoAtual.getProcessoConexoListForIdProcessoConexo().remove(processoConexo);
		update(processoAtual);
	}
	
	public boolean hasPartes(Processo processo){
		return processoEpaDAO.hasPartes(processo);
	}
	
    public void visualizarTask(final Processo processo, final Long idTarefa, final UsuarioLocalizacao usrLoc){
        final BusinessProcess bp = BusinessProcess.instance();
		if (!processo.getIdJbpm().equals(bp.getProcessId())) {            
            final Long taskInstanceId = processoLocalizacaoIbpmDAO.getTaskInstanceId(usrLoc, processo, idTarefa);
            
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
                // Caso já exista deve-se ignorar este trecho, outras illegalstate devem ser averiguadas
                // Ideal para processos já iniciados seria chamar o método resumeTask
                LOG.warn(".iniciaTask()", e);
            }
        }
    	return result;
    }
    
    public void iniciarTask(final Processo processo, final Long idTarefa, final UsuarioLocalizacao usrLoc) {
        final Long taskInstanceId = getTaskInstanceId(usrLoc, processo, idTarefa);
    	final String actorId = Actor.instance().getId();
    	iniciaTask(processo, taskInstanceId);
    	if (taskInstanceId != null) {
        	storeUsuario(taskInstanceId, usrLoc.getUsuario());
        	vinculaUsuario(processo, actorId);
    	}
    }

	private void vinculaUsuario(Processo processo, String actorId) {
		processo.setActorId(actorId);
		processo = EntityUtil.getEntityManager().merge(processo);
		EntityUtil.flush();
	}
    
	private Long getTaskInstanceId(final UsuarioLocalizacao usrLoc, final Processo processo, final Long idTarefa) {
        Long result;
		if (idTarefa != null) {
        	result = processoLocalizacaoIbpmDAO.getTaskInstanceId(usrLoc, processo, idTarefa);
        } else {
        	result = processoLocalizacaoIbpmDAO.getTaskInstanceId(usrLoc, processo);
        }
		return result;
    }
	
	/**
	 * Armazena o usuário que executou a tarefa. O jBPM mantem apenas os usuários das tarefas em execução, 
	 * apagando o usuário sempre que a tarefa é finalizada (ver tabela jbpm_taskinstance, campo actorid_)
	 * Porém surgiu a necessidade de armazenar os usuários das tarefas já finalizas para exibir no 
	 * histórico de Movimentação do Processo
	 * @param idTaskInstance
	 * @param actorId				 
	 * */
	private void storeUsuario(final Long idTaskInstance, final UsuarioLogin user){
        if (EntityUtil.getEntityManager().find(UsuarioTaskInstance.class, idTaskInstance) == null){
            EntityUtil.getEntityManager().persist(new UsuarioTaskInstance(idTaskInstance, user));
        }
	}
	
	public void moverProcessosParaCaixa(List<Integer> idList, Caixa caixa) {
		processoDAO.moverProcessosParaCaixa(idList, caixa);
	}
	
	public void moverProcessoParaCaixa(Caixa caixa, Processo processo){
		processoDAO.moverProcessoParaCaixa(caixa, processo);
	}
	
	public void moverProcessoParaCaixa(List<Caixa> caixaList, Processo processo){
		Caixa caixaEscolhida = escolherCaixaParaAlocarProcesso(caixaList);
		processoDAO.moverProcessoParaCaixa(caixaEscolhida, processo);
	}
	
	/*
	 * Atualmente a regra para escolher a caixa é simplesmente pegar a primeira
	 * */
	private Caixa escolherCaixaParaAlocarProcesso(List<Caixa> caixaList){
		return caixaList.get(0);		
	}
	
	public void removerProcessoDaCaixaAtual(Processo processo){
		processoDAO.removerProcessoDaCaixaAtual(processo);
	}
	
	public void apagarActorIdDoProcesso(Processo processo){
		processoDAO.apagarActorIdDoProcesso(processo);
	}
	
	public void atualizarProcessos(){
		processoDAO.atualizarProcessos();
	}
	
	public boolean checkAccess(int idProcesso, String login){
	    return !processoDAO.findProcessosByIdProcessoAndActorId(idProcesso, login).isEmpty();
	}
}
