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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.AssertionFailure;
import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;

import br.com.infox.ibpm.component.ControleFiltros;
import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.ProcessoEvento;
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


@Name(ProcessoHome.NAME)
@BypassInterceptors
public class ProcessoHome extends AbstractProcessoHome<Processo> {
	
	public static final String NAME = "processoHome";

	public static final String EVENT_ATUALIZAR_PROCESSO_DOCUMENTO_FLUXO = "atualizarProcessoDocumentoFluxo";
	public static final String AFTER_UPDATE_PD_FLUXO_EVENT = "afterUpdatePdFluxoEvent";
	
	private static final LogProvider LOG = Logging.getLogProvider(ProcessoHome.class);

	private static final long serialVersionUID = 1L;

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
	
	public void adicionarFluxo(Fluxo fluxo, Map<String, Object> variaveis){
		iniciarProcessoJbpm(fluxo, variaveis);
	}

	public void iniciarProcessoJbpm(Fluxo fluxo, Map<String, Object> variaveis) {
		BusinessProcess.instance().createProcess(fluxo.getFluxo().trim());
		instance.setIdJbpm(BusinessProcess.instance().getProcessId());
		update();
		
		// grava a variavel processo no jbpm com o numero do processo e-pa
		org.jbpm.graph.exe.ProcessInstance processInstance = ProcessInstance.instance();
		processInstance.getContextInstance().setVariable("processo", instance.getIdProcesso());
		if (variaveis != null) {
			for (Entry<String, Object> entry : variaveis.entrySet()) {
				processInstance.getContextInstance().setVariable(entry.getKey(), entry.getValue());
			}
		}
		
		// inicia a primeira tarefa do processo
		Collection<org.jbpm.taskmgmt.exe.TaskInstance> taskInstances = processInstance
				.getTaskMgmtInstance().getTaskInstances();
		if (taskInstances != null && !taskInstances.isEmpty()) {
			BusinessProcess.instance().setTaskId(taskInstances.iterator().next().getId());
			BusinessProcess.instance().startTask();
		}
	}
	
	public Processo criarProcesso() {
		Date dataCadastro = new Date();
		newInstance();
		UsuarioLogin usuario = getUsuarioLogado();
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
		UsuarioLogin usuario = getUsuarioLogado();
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

	public Boolean acessarFluxo(){
		if(getInstance().getActorId() == null ||
				getInstance().getActorId().equals(Authenticator.getUsuarioLogado().getLogin())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public String mostraProcesso(int id, String destino) {
		setId(id);
		return destino;
	}
		
	public void iniciaExterno(String viewId) {
		iniciaExterno = true;
		Redirect r = Redirect.instance();
		r.setViewId(viewId);
		setTab("tabParticipante");
		r.execute();
		
	}
	
	public static ProcessoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
	public void atualizarProcessoEvento(ProcessoDocumento pd){
		String sql = "select max(o.idProcessoEvento) from ProcessoEvento o";
		Query q = EntityUtil.getEntityManager().createQuery(sql);
		
		Integer resultado = (Integer) q.getSingleResult();
		
		ProcessoEvento processoEvento = EntityUtil.find(ProcessoEvento.class, resultado);
		processoEvento.setProcessoDocumento(pd);
		getEntityManager().merge(processoEvento);
		EntityUtil.flush();
	}
	
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
	
	public Boolean verificarPessoaAssinatura(ProcessoDocumento pd){
		String sql = "select o from ProcessoDocumentoBinPessoaAssinatura o " +
					 "where o.processoDocumentoBin = :pdb";
		Query q = getEntityManager().createQuery(sql);
		q.setParameter("pdb", pd.getProcessoDocumentoBin());
		
		if (q.getResultList().size() > 0){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Boolean isDocumentoAssinado(Integer idDoc){
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
		if(processoDocumento != null){
			if (verificarPessoaAssinatura(processoDocumento)){
				return Boolean.TRUE;
			}
			else {
				if ( (Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getCertChain())) || 
				     (Strings.isEmpty(processoDocumento.getProcessoDocumentoBin().getSignature()))){
					return Boolean.FALSE;
				}
				else {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	//Método para Atualizar o documento do fluxo
	public void atualizarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado){
		if (assinado){
			try {
				verificaCertificadoUsuarioLogado(certChain, getUsuarioLogado());
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
	
	//Método para Inserir o documento do fluxo
	public Integer inserirProcessoDocumentoFluxo(Object value, String label, Boolean assinado){
		UsuarioLogin usuarioLogado = getUsuarioLogado();
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
	
	public void carregarDadosFluxoEntrada(Integer idProcessoDocumento) {
		//TODO mudar para um hql - gastar menos recurso.
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idProcessoDocumento);
		if(processoDocumento != null){
			setTipoProcessoDocumentoRO(processoDocumento.getTipoProcessoDocumento());
			setModeloDocumentoRO(processoDocumento.getProcessoDocumentoBin().getModeloDocumento());
		}
	}
	
	public Boolean isSigned(){
		Boolean faltaAssinatura = Boolean.FALSE;
		Boolean somenteMagistrado = Boolean.FALSE;
		List<ProcessoDocumento> lista = getInstance().getProcessoDocumentoList();
		for (ProcessoDocumento procDocList : lista){
			if(procDocList.getTipoProcessoDocumento().getTipoProcessoDocumento().equals("Sentença") ||
					procDocList.getTipoProcessoDocumento().getTipoProcessoDocumento().equals("Despacho") ||
					procDocList.getTipoProcessoDocumento().getTipoProcessoDocumento().equals("Decisão")){				
					if (Strings.isEmpty(procDocList.getProcessoDocumentoBin().getCertChain())){
						faltaAssinatura = Boolean.TRUE;
					}else{
						Boolean o = getPessoaAssinatura(procDocList.getProcessoDocumentoBin());
						if(!o){
							somenteMagistrado = Boolean.TRUE;
						}
					}
				}				
			}
		if (somenteMagistrado){
			FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Todos os Documentos devem estar assinados digitalmente por Magistrado.");
		}
		return faltaAssinatura;		
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
	
	public boolean verificaUltimoEvento(Processo processo, Evento evento) {
		ProcessoEvento processoEvento = getUltimoProcessoEvento(processo);
		
		if (processoEvento == null) {
			LOG.info("verificaUltimoEvento: Não existem evento no processo: " + processo);
			return false;
		}
		
		List<Evento> filhos = evento.getEventoListCompleto();
		return filhos.contains(processoEvento.getEvento());
	}
	
	public boolean verificaEvento(Processo processo, Evento evento) {
		String hql = "select o.idProcessoEvento from ProcessoEvento o where o.processo = :processo and o.evento in (:eventos)";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("processo", processo);
		q.setParameter("eventos", evento.getEventoListCompleto());
		
		return EntityUtil.getSingleResult(q) != null;
	}
	
	/**
	 * Método que retorna a data do envio do processo ao segundo grau
	 * @return Data de envio do processo ao segundo grau
	 * 
	 */
	public String dataEnvio2Grau() {
		String hql = "Select o from ProcessoEvento o where o.processo = :processo " +
				      "and o.evento = (Select o.idEvento from EventoProcessual o " +
				      					"where o.codEvento = '123A')";
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("processo", getInstance());
		if(!q.getResultList().isEmpty()){
			ProcessoEvento pe = (ProcessoEvento)q.getResultList().get(0);
			if(pe != null){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return sdf.format(pe.getDataAtualizacao());
			}
		}
		return null;
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
	
	// TODO: Verificar este método retornando uma constante
	public int getMovimentacaoInicial() {
		return 0;
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
	
	public UsuarioLogin getUsuarioLogado() {
		UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
		usuario = getEntityManager().find(usuario.getClass(), usuario.getIdPessoa());
		return usuario;
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
	
	public Boolean getPessoaAssinatura(ProcessoDocumentoBin processoDocumentoBin)	{
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT pa.pessoa as pessoa ");
		sb.append("FROM ProcessoDocumentoBinPessoaAssinatura pa ");
		sb.append("WHERE pa.processoDocumentoBin = :processoDocumentoBin ");
		sb.append("AND exists(select o.idUsuario from PessoaMagistrado o) ");		
		javax.persistence.Query query = EntityUtil.getEntityManager().createQuery(sb.toString());
		query.setParameter("processoDocumentoBin", processoDocumentoBin);
		if(query.getResultList().size() > 0)	{
			Boolean flag = Boolean.TRUE; 
		return flag;
		}	else	{
			return false;
		}
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
	
	public ProcessoEvento getUltimoProcessoEvento(Processo processo){
		String hql = "select o from ProcessoEvento o where o.processo = :processo " +
				"order by o.dataAtualizacao desc ";
		
		Query q = getEntityManager().createQuery(hql);
		q.setParameter("processo", processo);
		
		return EntityUtil.getSingleResult(q);
	}	
	
	@SuppressWarnings("unchecked")
	public List<Evento> getEventosAgrupamento(String nomeAgrupamento) {
		String hql = "	select e from Agrupamento o " +
			"	inner join o.eventoAgrupamentoList evtL " +
			"	inner join evtL.evento e " +
			"	where o.agrupamento = :nomeAgrupamento ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("nomeAgrupamento", nomeAgrupamento);
		return query.getResultList();
	}
	
	public ProcessoEvento getUltimoProcessoEvento(Processo processo, String nomeAgrupamento) {
		List<Evento> eventosAgrupamento = getEventosAgrupamento(nomeAgrupamento);
		if (eventosAgrupamento == null || eventosAgrupamento.size() == 0) {
			LOG.warn("Nenhum evento evento encontrado para o agrupamento: " + nomeAgrupamento);
			return null;
		}
		List<Evento> eventoListCompleto = new ArrayList<Evento>();
		for (Evento evento : eventosAgrupamento) {
			eventoListCompleto.addAll(evento.getEventoListCompleto());
		}
		
		String hql = "select o from ProcessoEvento o " +
			"where o.processo = :processo and o.evento in (" + 
			":eventos) " +
			"order by o.dataAtualizacao desc";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("processo", processo);
		query.setParameter("eventos", eventoListCompleto);
		return EntityUtil.getSingleResult(query);
	}

// -----------------------------------------------------------------------------------------------------------------------
// ------------------------------------------------- Excluir -------------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------------
	
	public void setProcessoDocumentoFaseAtual(ProcessoDocumento processoDocumentoFaseAtual) {
	}
	
	public void setProcessoDocumentoFaseAnterior(ProcessoDocumento processoDocumentoFaseAtual) {
	}
	
	//TODO: ver uso deste método
	public void teste(){
		System.out.println("paginator");
		processoDocumentoBin = pdFluxo.getProcessoDocumentoBin();
		System.out.println(processoDocumentoBin);
		System.out.println(tipoProcessoDocumento);
	}
	
}