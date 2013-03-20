/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.AssertionFailure;
import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.epa.service.ProcessoService;
import br.com.infox.ibpm.component.ControleFiltros;
import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.TaskInstanceHome;
import br.com.infox.ibpm.jbpm.actions.JbpmEventsHandler;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.infox.ibpm.jbpm.assignment.LocalizacaoAssignment;
import br.com.infox.ibpm.service.AssinaturaDocumentoService;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.Crypto;
import br.com.itx.util.EntityUtil;


@Name("processoHome")
public class ProcessoHome extends AbstractProcessoHome<Processo> {
	
	public static final String NAME = "processoHome";

	public static final String EVENT_ATUALIZAR_PROCESSO_DOCUMENTO_FLUXO = "atualizarProcessoDocumentoFluxo";
	public static final String AFTER_UPDATE_PD_FLUXO_EVENT = "afterUpdatePdFluxoEvent";
	
	private static final LogProvider LOG = Logging.getLogProvider(ProcessoHome.class);

	private static final long serialVersionUID = 1L;
	
	@In private ProcessoService processoService;

	private ModeloDocumento modeloDocumento;
	private TipoProcessoDocumento tipoProcessoDocumento;
	private TipoProcessoDocumento tipoProcessoDocumentoRO;
	private ProcessoDocumentoBin processoDocumentoBin = new ProcessoDocumentoBin();
	private String modeloDocumentoRO;
	private String observacaoMovimentacao;
	private boolean iniciaExterno;
	private String signature;
	private String certChain;
	private String idAgrupamentos;
	private boolean renderEventsTree;
    private ProcessoDocumento pdFluxo;
	private Integer idProcessoDocumento;

	private Long taskId;

	private Boolean checkVisibilidade = true;

	public void iniciarNovoFluxo(){
		limpar();
		Redirect redirect = Redirect.instance();
		redirect.setViewId("/Processo/movimentar.xhtml");
		redirect.execute();
	}
	
	public void limpar(){
		modeloDocumento = null;
		tipoProcessoDocumento = null;
		newInstance();
	}
	
	public Processo criarProcesso() {
		Date dataCadastro = new Date();
		newInstance();
		UsuarioLogin usuario = Authenticator.getUsuarioLogado();
		instance.setUsuarioCadastroProcesso(usuario);
		instance.setDataInicio(dataCadastro);
		instance.setNumeroProcesso("");
		Processo processo = getInstance();
		persist();
		
		FacesMessages.instance().clear();
		FacesMessages.instance().add(StatusMessage.Severity.INFO,
				"Processo #{processoHome.instance.idProcesso} iniciado com sucesso");
		return processo;
	}
	
	public Processo iniciarProcesso(Fluxo fluxo) {
		BusinessProcess.instance().createProcess(fluxo.getFluxo().trim());
		Date dataCadastro = new Date();
		newInstance();
		UsuarioLogin usuario = Authenticator.getUsuarioLogado();
		instance.setUsuarioCadastroProcesso(usuario);
		instance.setDataInicio(dataCadastro);
		instance.setIdJbpm(BusinessProcess.instance().getProcessId());
		instance.setNumeroProcesso("");
		persist();
		// grava a variavel processo no jbpm com o numero do processo e-pa
		org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
		processInstance.getContextInstance().setVariable("processo", instance.getIdProcesso());
		instance.setNumeroProcesso(Integer.toString(instance.getIdProcesso()));
		update();
		// inicia a primeira tarefa do processo
		Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
		if (taskInstances != null && !taskInstances.isEmpty()) {
			BusinessProcess.instance().setTaskId(taskInstances.iterator().next().getId());
			BusinessProcess.instance().startTask();
		}
		FacesMessages.instance().clear();
		FacesMessages.instance().add(StatusMessage.Severity.INFO,
				"Processo #{processoHome.instance.idProcesso} iniciado com sucesso");

		SwimlaneInstance swimlaneInstance = TaskInstance.instance().getSwimlaneInstance();
		String actorsExpression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
		Set<String> pooledActors = LocalizacaoAssignment.instance().getPooledActors(actorsExpression);
		String[] actorIds = (String[]) pooledActors.toArray(new String[pooledActors.size()]);
		swimlaneInstance.setPooledActors(actorIds);
		return instance;
	}
 
	public void iniciarTarefaProcesso() {
		JbpmEventsHandler.instance().iniciarTask(instance);
	}
	
	public void visualizarTarefaProcesso(){
		JbpmEventsHandler.instance().visualizarTask(instance);
	}
	
	public static ProcessoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
		
	public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded, UsuarioLogin usuarioLogado) throws Exception {
		if (Strings.isEmpty(usuarioLogado.getCertChain())) {
			limparAssinatura();
			throw new Exception("O cadastro do usuário não está assinado.");
		}
		if (!usuarioLogado.checkCertChain(certChainBase64Encoded)) {
			limparAssinatura();
			throw new Exception("O certificado não é o mesmo do cadastro do usuario");
		}
		//TODO usar o VerificaCertificado que hoje sim está no PJE2, tem de migrar o que nao é do PJE2 pro core.
	}	
	
	private void limparAssinatura() {
		certChain = null;
		signature = null;
	}
	
	public Boolean checarVisibilidade()	{
		 if (!checkVisibilidade ) {
				return true;
			}
			Integer id = null;
			String numeroProcesso = null;
			if (!isManaged()) {
				id = ProcessoHome.instance().getInstance().getIdProcesso();
				numeroProcesso = ProcessoHome.instance().getInstance().getNumeroProcesso();
			} else {
				id = getInstance().getIdProcesso();
				numeroProcesso = getInstance().getNumeroProcesso();
			}
			
			ControleFiltros.instance().iniciarFiltro();
			Query query = getEntityManager().createQuery("select o from ProcessoLocalizacaoIbpm o" +
															" where o.processo.idProcesso = :id" +
																" and o.localizacao = :localizacao" +
																" and o.papel = :papel");
			query.setParameter("id", id);
			query.setParameter("localizacao", Authenticator.getLocalizacaoAtual());
			query.setParameter("papel", Authenticator.getPapelAtual());
			Object result = EntityUtil.getSingleResult(query);
			boolean check = result != null;
			if(!check){
				Util.setToEventContext("canClosePanel", true);
				FacesMessages.instance().clear();
				FacesMessages.instance().add(Severity.ERROR, "Sem permissão para acessar o processo: " + numeroProcesso);
			}
			return check;
	}
		
	/**
	 * Verifica se existe algum agrupamento vinculado ao tipo de documento
	 * selecionado.
	 */
	public void onSelectProcessoDocumento() {
		AutomaticEventsTreeHandler.instance().clearList();
		AutomaticEventsTreeHandler.instance().clearTree();
		renderEventsTree = false;
	
			if(tipoProcessoDocumento != null && tipoProcessoDocumento.getAgrupamento() != null) {
				idAgrupamentos = Integer.toString(tipoProcessoDocumento.getAgrupamento().getIdAgrupamento());
				if(!Strings.isEmpty(idAgrupamentos)) {
					renderEventsTree = true;
					AutomaticEventsTreeHandler.instance().setRootsSelectedMap(new HashMap<Evento, List<Evento>>());
					AutomaticEventsTreeHandler.instance().getRoots(idAgrupamentos);
				}
			}	
		}
	
// -----------------------------------------------------------------------------------------------------------------------
// ----------------------------------- Métodos ligados a Processo Documento ----------------------------------------------
// -----------------------------------------------------------------------------------------------------------------------	
	
	public Integer salvarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado, String label){
		Integer result = 0;
		
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
		ProcessoHome.instance().setIdProcessoDocumento(idDoc);
		if (processoDocumento == null) {
			result = inserirProcessoDocumentoFluxo(value, label, assinado);
			if(result == 0) {
				//Erro ao inserir o documento
				return result;
			}
		} else {
			AssinaturaDocumentoService documentoService = new AssinaturaDocumentoService();
			if(documentoService.isDocumentoAssinado(idDoc)){
				result = inserirProcessoDocumentoFluxo(value, label, assinado);
			}
			if(result == 0) {
				atualizarProcessoDocumentoFluxo(value, idDoc, assinado);
				result = idDoc;
			}
		}
		FacesMessages.instance().add(StatusMessage.Severity.INFO, "Registro gravado com sucesso!");
		Events.instance().raiseEvent(AFTER_UPDATE_PD_FLUXO_EVENT, idProcessoDocumento);
		return result;
	}
	
	//Método para Atualizar o documento do fluxo
	public void atualizarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado){
		if (assinado){
			try {
				verificaCertificadoUsuarioLogado(certChain, Authenticator.getUsuarioLogado());
			} catch (Exception e1) {
				FacesMessages.instance().add(Severity.ERROR, 
						"Erro ao verificar certificado: " + e1.getMessage());
				return;
			}
		}
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
		ProcessoDocumentoBin processoDocumentoBin = processoDocumento.getProcessoDocumentoBin();
		String modeloDocumentoFluxo = processoDocumentoBin.getModeloDocumento();
		if(value == null) {
			value = modeloDocumentoFluxo;
			if(value == null) {
				value = "";
			}
		}
		String modeloDocumento = String.valueOf(value);
		
		if (!Strings.isEmpty(modeloDocumentoFluxo) && modeloDocumento != modeloDocumentoFluxo){
			modeloDocumento = modeloDocumentoFluxo;
		}
		
		if (Strings.isEmpty(modeloDocumento)){
			modeloDocumento = " ";
		}
		processoDocumentoBin.setModeloDocumento(modeloDocumento);
		processoDocumentoBin.setCertChain(certChain);
		processoDocumentoBin.setSignature(signature);
		
		//TODO verificar se a regra abaixo será mantida, se será criado um parametro ou se o componente textEditor será extinto.
		/*
		 *Se o tipoProcessoDocumento for nulo (caso o componente utilizado seja
		 *o editor sem assinatura digital, o tipoProcessoDOcumento será setado
		 *automaticamente com um valor aleatorio 
		 */
		if (tipoProcessoDocumento == null){
			tipoProcessoDocumento = getTipoProcessoDocumentoFluxo();
		}
		processoDocumento.setTipoProcessoDocumento(tipoProcessoDocumento);
		
		try {
			getEntityManager().merge(processoDocumento);
			getEntityManager().flush();
            setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
		} catch (AssertionFailure e) {
			// TODO: handle exception
		}
		try {
			getEntityManager().merge(processoDocumentoBin);
			getEntityManager().flush();
			try{
				Events.instance().raiseEvent(EVENT_ATUALIZAR_PROCESSO_DOCUMENTO_FLUXO, processoDocumentoBin);
			}catch(Exception e){
				//Ignora de assinatura erro por enquanto
			}
		} catch (AssertionFailure e) {
			// TODO: handle exception
		}
	}
	
	//Método para Inserir o documento do fluxo
	public Integer inserirProcessoDocumentoFluxo(Object value, String label, Boolean assinado){
		UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
		if (assinado){
			try {
				verificaCertificadoUsuarioLogado(certChain, usuarioLogado);
				
			} catch (Exception e1) {
				FacesMessages.instance().add(Severity.ERROR, 
						"Erro ao verificar certificado: " + e1.getMessage());
				return 0;
			}
		}
		
		ProcessoDocumentoBin bin = new ProcessoDocumentoBin();
		ProcessoDocumento doc = new ProcessoDocumento();
		if(processoDocumentoBin.getModeloDocumento() != null) {
			value = processoDocumentoBin.getModeloDocumento();
		}
		if(value == null) {
			value = new String();
		}
		String modeloDocumento = String.valueOf(value);
		if (Strings.isEmpty(modeloDocumento)){
			modeloDocumento = " ";
		}
		bin.setModeloDocumento(modeloDocumento);
		bin.setDataInclusao(new Date());
		bin.setMd5Documento(Crypto.encodeMD5(String.valueOf(value)));
		bin.setUsuario(usuarioLogado);
		bin.setCertChain(certChain);
		bin.setSignature(signature);
		  
		doc.setProcessoDocumentoBin(bin);
		doc.setAtivo(Boolean.TRUE);
		doc.setDataInclusao(new Date());
		doc.setUsuarioInclusao(usuarioLogado);
		doc.setProcesso(getInstance());
		if (label == null) {
			doc.setProcessoDocumento("null");
		} else {
			doc.setProcessoDocumento(label);
		}

		//TODO verificar se a regra abaixo será mantida, se será criado um parametro ou se o componente textEditor será extinto.
		/*
		 *Se o tipoProcessoDocumento for nulo (caso o componente utilizado seja
		 *o editor sem assinatura digital, o tipoProcessoDOcumento será setado
		 *automaticamente com um valor aleatorio 
		 */
		if (tipoProcessoDocumento == null){
			tipoProcessoDocumento = getTipoProcessoDocumentoFluxo();
		}
		doc.setTipoProcessoDocumento(tipoProcessoDocumento);
	  
		EntityManager em = EntityUtil.getEntityManager();
		em.persist(bin);
		em.persist(doc);
		
		try {
			em.flush();
            setIdProcessoDocumento(doc.getIdProcessoDocumento());
			try{
				Events.instance().raiseEvent(EVENT_ATUALIZAR_PROCESSO_DOCUMENTO_FLUXO, bin);
			}catch(Exception e){
				//Ignora de assinatura erro por enquanto
			}
		} catch (AssertionFailure e) { 
		//Ignorar
		}
		
		return doc.getIdProcessoDocumento();
	}
	
	public void carregarDadosFluxo(Integer idProcessoDocumento){
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idProcessoDocumento);
		if(processoDocumento != null){
			setPdFluxo(processoDocumento);
			processoDocumentoBin = processoDocumento.getProcessoDocumentoBin();
			ProcessoDocumentoHome.instance().setInstance(processoDocumento);
			ProcessoHome.instance().setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
			setTipoProcessoDocumento(processoDocumento.getTipoProcessoDocumento());
			onSelectProcessoDocumento();
		}
	}
	
	public void updateProcessoDocumentoBin() {
		ModeloDocumento modeloDocumento = TaskInstanceHome.instance().getModeloDocumento();
		String modelo = "";
		if(modeloDocumento != null) {
			modelo = ModeloDocumentoAction.instance().getConteudo(modeloDocumento);
		}
		processoDocumentoBin.setModeloDocumento(modelo);
	}
		
// -----------------------------------------------------------------------------------------------------------------------
// -------------------------------------------- Getters e Setters --------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------------
	
	@Observer("processoHomeSetId")
	@Override
	public void setId(Object id) {
		super.setId(id);
	}
	
	public void setModeloDocumentoCombo(ModeloDocumento modeloDocumentoCombo) {
		this.modeloDocumento = modeloDocumentoCombo;
	}
	
	public ModeloDocumento getModeloDocumentoCombo() {
		return modeloDocumento;
	}

	public TipoProcessoDocumento getTipoProcessoDocumento() {
		return tipoProcessoDocumento;
	}
	
	public void setTipoProcessoDocumento(
			TipoProcessoDocumento tipoProcessoDocumento) {	
			this.tipoProcessoDocumento = tipoProcessoDocumento;		
	}
	
	public String getObservacaoMovimentacao() {
		return observacaoMovimentacao;
	}
	
	public void setObservacaoMovimentacao(String observacaoMovimentacao) {
		this.observacaoMovimentacao = observacaoMovimentacao;
	}

	public void setModeloDocumentoRO(String modeloDocumentoRO) {
		this.modeloDocumentoRO = modeloDocumentoRO;
	}

	public String getModeloDocumentoRO() {
		return modeloDocumentoRO;
	}

	public void setTipoProcessoDocumentoRO(TipoProcessoDocumento tipoProcessoDocumentoRO) {
		this.tipoProcessoDocumentoRO = tipoProcessoDocumentoRO;
	}

	public TipoProcessoDocumento getTipoProcessoDocumentoRO() {
		return tipoProcessoDocumentoRO;
	}
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getTaskId() {
		return taskId; 
	}
	
	public boolean isIniciaExterno() {
		return iniciaExterno;
	}

	public void setIniciaExterno(boolean iniciaExterno) {
		this.iniciaExterno = iniciaExterno;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignature() {
		return signature;
	}

	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}

	public String getCertChain() {
		return certChain;
	}
	
	public String getIdAgrupamentos() {
		return idAgrupamentos;
	}

	public boolean getRenderEventsTree() {
		return renderEventsTree;
	}

	public void setProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin) {
		this.processoDocumentoBin = processoDocumentoBin;
	}

	public ProcessoDocumentoBin getProcessoDocumentoBin() {
		return processoDocumentoBin;
	}

	public void setPdFluxo(ProcessoDocumento pdFluxo) {
		this.pdFluxo = pdFluxo;
	}

	public ProcessoDocumento getPdFluxo() {
		return pdFluxo;
	}

	public void setIdProcessoDocumento(Integer idProcessoDocumento) {
		this.idProcessoDocumento = idProcessoDocumento;
	}

	public Integer getIdProcessoDocumento() {
		return idProcessoDocumento;
	}

	public Integer getFirst(){
		List<ProcessoDocumento> processoDocList = getInstance().getProcessoDocumentoList();
		return processoDocList.size();
	}	
		
	public String getNumeroProcesso(int idProcesso) {
		String ret = idProcesso + "";
		Processo processo = EntityUtil.find(Processo.class, idProcesso);
		if (processo != null) {
			ret = processo.getNumeroProcesso();
		}
		return ret;
	}
	
	public TipoProcessoDocumento getTipoProcessoDocumentoFluxo(){
		String sql = "select o from TipoProcessoDocumento o ";
		Query q = EntityUtil.getEntityManager().createQuery(sql);
		return (TipoProcessoDocumento) q.getResultList().get(0);
	}
	
	/*
	 * Varre toda a entidade variableInstance, e caso o registro seja do tipo textEditor 
	 * e tenha valor na stringInstance, pega esse valor, cria um documento, seta como null
	 * a string e seta o longInstance do o id do ProcessoDocumento criado.
	 */
	@SuppressWarnings("unchecked")
	public void getListaVariable(){
		//Lista de todos os objetos que sao textEditor
		String sqlListTextEditor = "select o.id from org.jbpm.context.exe.VariableInstance o " +
		 						   "where o.name like 'textEdit%'";
		org.hibernate.Query q0 = JbpmUtil.getJbpmSession().createQuery(sqlListTextEditor);
		List<Long> lte = q0.list();
		
		//Verificando quais items dessa lista que estao com o valor da string != null
		List<Long> les = new ArrayList<Long>(0);
		for (Long longId : lte) {
			String sqlListEditorString = "select o.id from org.jbpm.context.exe.variableinstance.StringInstance o " +
										 "where o.id = :id and o.value != ''";
			org.hibernate.Query q = JbpmUtil.getJbpmSession().createQuery(sqlListEditorString);
			q.setParameter("id", longId);
			if (q.list().size() > 0){
				les.add((Long) q.list().get(0));
			}
		}
		
		/*
		 * Pega cada registro da variableInstance, verifica o processInstance e
		 * procura o registro com o name processo em q o processInstance seja igual,
		 * para pegar o numero do processo.
		 */
		for (Long longId : les) {
			//Pega o objeto do registro
			String sqlVar = "select o from org.jbpm.context.exe.VariableInstance o " +
						    "where o.id = :id";
			org.hibernate.Query q = JbpmUtil.getJbpmSession().createQuery(sqlVar);
			q.setParameter("id", longId);
			
			//Pega o variable instance do registro TextEditor q tenha valor na string.
			String pi = "select o.processInstance.id from org.jbpm.context.exe.VariableInstance o " +
						"where o.id = :id";
			org.hibernate.Query q1 = JbpmUtil.getJbpmSession().createQuery(pi);
			q1.setParameter("id", longId);
			Long resultado = (Long) q1.list().get(0);
			
			//Pega o id do registro que guarda o numero do processo.
			String np = "select o.id from org.jbpm.context.exe.VariableInstance o " +
						"where o.processInstance.id = :processInstanceId and o.name like 'processo'";
			org.hibernate.Query q2 = JbpmUtil.getJbpmSession().createQuery(np);
			q2.setParameter("processInstanceId", resultado);
			Long process = (Long) q2.list().get(0);
			
			//Pega o valor da coluna Long do registro achado anteriormente.
			String processo = "select o.value from org.jbpm.context.exe.variableinstance.LongInstance o " +
							  "where o.id = :id";
			org.hibernate.Query q3 = JbpmUtil.getJbpmSession().createQuery(processo);
			q3.setParameter("id", process);
			long numeroProcesso = (Long) q3.list().get(0);
			
			int numeroProcessoInt = (int) numeroProcesso; 
			//Seta a instancia com o processo achado.
			Processo instanceProcesso = EntityUtil.find(Processo.class, numeroProcessoInt);
			setInstance(instanceProcesso);
			
			//Pegando o valor do registro para criar um processo documento.
			String doc = "select o.value from org.jbpm.context.exe.variableinstance.StringInstance o " +
						 "where o.id = :id";
			org.hibernate.Query q4 = JbpmUtil.getJbpmSession().createQuery(doc);
			q4.setParameter("id", longId);
			
			Object value = q4.list().get(0); 
			int id = inserirProcessoDocumentoFluxo(value, "Certidão", false);
			
			StringBuilder sba = new StringBuilder();
			sba.append("update public.jbpm_variableinstance ");
			sba.append("set stringvalue_ = null , longvalue_ = ").append((long) id);
			sba.append(" where id_ = ").append(longId);
			SQLQuery updateString = JbpmUtil.getJbpmSession().createSQLQuery(sba.toString());
			updateString.executeUpdate();
		}
	}
}