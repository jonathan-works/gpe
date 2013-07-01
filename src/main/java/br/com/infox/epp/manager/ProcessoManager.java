package br.com.infox.epp.manager;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.util.Strings;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoEpaDAO;
import br.com.infox.ibpm.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.dao.UsuarioLoginDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.home.Authenticator;
import br.com.infox.ibpm.jbpm.UsuarioTaskInstance;
import br.com.itx.util.Crypto;
import br.com.itx.util.EntityUtil;

@Name(ProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoManager extends GenericManager {
	
	private static final long serialVersionUID = 8095772422429350875L;
	public static final String NAME = "processoManager";
	
	@In private ProcessoEpaDAO processoEpaDAO;
	@In private ProcessoLocalizacaoIbpmDAO processoLocIbpmDAO;
	@In private UsuarioLoginDAO usuarioLoginDAO;
	
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
	
	public ProcessoDocumento createProcessoDocumento(Processo processo, String label, ProcessoDocumentoBin bin, TipoProcessoDocumento tipoProcessoDocumento) {
		ProcessoDocumento doc = new ProcessoDocumento();
		doc.setProcessoDocumentoBin(bin);
		doc.setAtivo(Boolean.TRUE);
		doc.setDataInclusao(new Date());
		doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
		doc.setProcesso(processo);
		if (label == null) {
			doc.setProcessoDocumento("null");
		} else {
			doc.setProcessoDocumento(label);
		}
		doc.setTipoProcessoDocumento(tipoProcessoDocumento);
		EntityUtil.getEntityManager().persist(doc);
		return doc;
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
			value = processoDocumentoBinAtual.getModeloDocumento();
		}
		return value;
	}
	
	public void addProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().add(processoConexo);
	}
	
	public void removeProcessoConexoForIdProcesso(Processo processoAtual, Processo processoConexo) {
		processoAtual.getProcessoConexoListForIdProcesso().remove(processoConexo);
	}
	
	public void addProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo){
		processoAtual.getProcessoConexoListForIdProcessoConexo().add(processoConexo);
		update(processoAtual);
	}
	
	public void removeProcessoConexoForIdProcessoConexo(Processo processoAtual, Processo processoConexo){
		processoAtual.getProcessoConexoListForIdProcessoConexo().remove(processoConexo);
		update(processoAtual);
	}
	
	public boolean hasPartes(Processo processo){
		return processoEpaDAO.hasPartes(processo);
	}
	
    public void visualizarTask(final Processo processo, final Long idTarefa, final UsuarioLocalizacao usrLoc){
        final BusinessProcess bp = BusinessProcess.instance();
		if (!processo.getIdJbpm().equals(bp.getProcessId())) {            
            final Long taskInstanceId = processoLocIbpmDAO.getTaskInstanceId(usrLoc, processo, idTarefa);
            
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
            	e.printStackTrace();
            }
        }
    	return result;
    }
    
    public void iniciarTask(final Processo processo, final Long idTarefa, final UsuarioLocalizacao usrLoc) {
        final Long taskInstanceId = getTaskInstanceId(usrLoc, processo, idTarefa);
    	final String actorId = Actor.instance().getId();
    	if (iniciaTask(processo, taskInstanceId)) {
	    	storeUsuario(taskInstanceId, usrLoc.getUsuario());
	    	vinculaUsuario(processo, actorId);
    	}
    }

	private void vinculaUsuario(Processo processo, String actorId) {
		processo.setActorId(actorId);
		EntityUtil.getEntityManager().merge(processo);
		EntityUtil.flush();
	}
    
	private Long getTaskInstanceId(final UsuarioLocalizacao usrLoc, final Processo processo, final Long idTarefa) {
        Long result;
		if (idTarefa != null) {
        	result = processoLocIbpmDAO.getTaskInstanceId(usrLoc, processo, idTarefa);
        } else {
        	result = processoLocIbpmDAO.getTaskInstanceId(usrLoc, processo);
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
        EntityUtil.getEntityManager().persist(new UsuarioTaskInstance(idTaskInstance, user));
	}
}
