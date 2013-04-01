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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;
import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.ibpm.jbpm.TaskInstanceHome;
import br.com.infox.ibpm.jbpm.actions.JbpmEventsHandler;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.Crypto;
import br.com.itx.util.EntityUtil;

@Name(ProcessoHome.NAME)
public class ProcessoHome extends AbstractHome<Processo> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoHome";

	private static final int ERRO_AO_VERIFICAR_CERTIFICADO = 0;
		
	@In private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
	@In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;

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

	private Long tarefaId;
	
	public void iniciarNovoFluxo(){
		limpar();
		redirecionarPagina();
	}

	private void redirecionarPagina() {
		Redirect redirect = Redirect.instance();
		redirect.setViewId("/Processo/movimentar.xhtml");
		redirect.execute();
	}
	
	public void limpar(){
		modeloDocumento = null;
		tipoProcessoDocumento = null;
		newInstance();
	}
 
	public void iniciarTarefaProcesso() {
		JbpmEventsHandler.instance().iniciarTask(instance, tarefaId);
	}
	
	public void visualizarTarefaProcesso(){
		JbpmEventsHandler.instance().visualizarTask(instance, tarefaId);
	}
	
	public static ProcessoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
		
	private void limparAssinatura() {
		certChain = null;
		signature = null;
	}
	
	public Boolean checarVisibilidade()	{
		boolean possuiPermissao = processoLocalizacaoIbpmDAO.possuiPermissao();
		if(!possuiPermissao)
			avisarNaoHaPermissaoParaAcessarProcesso();
		return possuiPermissao;
	}

	private void avisarNaoHaPermissaoParaAcessarProcesso() {
		Util.setToEventContext("canClosePanel", true);
		FacesMessages.instance().clear();
		FacesMessages.instance().add(Severity.ERROR, "Sem permissão para acessar o processo: " + getInstance().getNumeroProcesso());
	}
		
	
	public void onSelectProcessoDocumento() {
		limparEventsTreeHandler();
		if(possuiAgrupamento()) {
			preencherEventsTreeHandler();
		}	
	}
	
	private void limparEventsTreeHandler() {
		AutomaticEventsTreeHandler.instance().clearList();
		AutomaticEventsTreeHandler.instance().clearTree();
		renderEventsTree = false;
	}

	private void preencherEventsTreeHandler() {
		renderEventsTree = true;
		AutomaticEventsTreeHandler.instance().setRootsSelectedMap(new HashMap<Evento, List<Evento>>());
		AutomaticEventsTreeHandler.instance().getRoots(getIdAgrupamentoAsString());
	}

	private String getIdAgrupamentoAsString() {
		return Integer.toString(tipoProcessoDocumento.getAgrupamento().getIdAgrupamento());
	}

	private boolean possuiAgrupamento() {
		return tipoProcessoDocumento != null && tipoProcessoDocumento.getAgrupamento() != null;
	}	
	
	public Integer salvarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado, String label){
		ProcessoDocumento processoDocumento = buscarProcessoDocumento(idDoc);
		setIdProcessoDocumento(idDoc);
		Integer result = inserirProcessoDocumentoFluxo(value, label, assinado);
		if (processoDocumento != null && result == ERRO_AO_VERIFICAR_CERTIFICADO) {
			atualizarProcessoDocumentoFluxo(value, idDoc, assinado);
			result = idDoc;
		}
		avisarRegistroFoiGravadoComSucesso();
		return result;
	}

	private ProcessoDocumento buscarProcessoDocumento(Integer idDoc) {
		return EntityUtil.find(ProcessoDocumento.class, idDoc);
	}

	private void avisarRegistroFoiGravadoComSucesso() {
		FacesMessages.instance().add(StatusMessage.Severity.INFO, "Registro gravado com sucesso!");
	}
	
	//Método para Atualizar o documento do fluxo
	private void atualizarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado){
		if (validacaoCertificadoBemSucedida(assinado)) {
			ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idDoc);
			ProcessoDocumentoBin processoDocumentoBin = processoDocumento.getProcessoDocumentoBin();
			String modeloDocumento = getDescricaoModeloDocumentoFluxoByValue(value, processoDocumentoBin.getModeloDocumento());
			atualizarProcessoDocumentoBin(processoDocumentoBin, modeloDocumento);
			inicializarTipoProcessoDocumento();
			gravarAlteracoes(processoDocumento, processoDocumentoBin);
		}
	}

	private String getDescricaoModeloDocumentoFluxoByValue(Object value, String modeloDocumentoFluxo) {
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
		return modeloDocumento;
	}

	private void gravarAlteracoes(ProcessoDocumento processoDocumento, ProcessoDocumentoBin processoDocumentoBin) {
		processoDocumento.setTipoProcessoDocumento(tipoProcessoDocumento);
		getEntityManager().merge(processoDocumento);
		setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
		getEntityManager().merge(processoDocumentoBin);
		getEntityManager().flush();
	}

	private void atualizarProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin, String modeloDocumento) {
		processoDocumentoBin.setModeloDocumento(modeloDocumento);
		processoDocumentoBin.setCertChain(certChain);
		processoDocumentoBin.setSignature(signature);
	}

	/**
	 *Se o tipoProcessoDocumento for nulo (caso o componente utilizado seja
	 *o editor sem assinatura digital, o tipoProcessoDOcumento será setado
	 *automaticamente com um valor aleatorio 
	 */
	private void inicializarTipoProcessoDocumento() {
		if (tipoProcessoDocumento == null){
			tipoProcessoDocumento = tipoProcessoDocumentoDAO.getTipoProcessoDocumentoFluxo();
		}
	}
	
	//Método para Inserir o documento do fluxo
	private Integer inserirProcessoDocumentoFluxo(Object value, String label, Boolean assinado){
		if (validacaoCertificadoBemSucedida(assinado)) {
			value = getAlteracaoModeloDocumento(value);
			ProcessoDocumento doc = createProcessoDocumento(label, createProcessoDocumentoBin(value));
			getEntityManager().flush();
	        setIdProcessoDocumento(doc.getIdProcessoDocumento());
			return doc.getIdProcessoDocumento();
		} else {
			return ERRO_AO_VERIFICAR_CERTIFICADO;
		}
	}
	
	private boolean validacaoCertificadoBemSucedida(boolean assinado){
		if (assinado){
			try {
				verificaCertificadoUsuarioLogado(certChain, Authenticator.getUsuarioLogado());
			} catch (Exception e1) {
				avisarErroAoVerificarCertificado(e1);
				return false;
			}
		}
		return true;
	}

	/**
	 * Retorna, se houver, o novo valor do ModeloDocumento. Se nao houver, retorna o valor o valor
	 * inicial inalterado
	 * @param value - valor da variável modeloDocumento no contexto jBPM
	 * */
	private Object getAlteracaoModeloDocumento(Object value) {
		if(processoDocumentoBin.getModeloDocumento() != null) {
			value = processoDocumentoBin.getModeloDocumento();
		}
		return value;
	}

	private String getDescricaoModeloDocumentoByValue(Object value) {
		String modeloDocumento = String.valueOf(value);
		if (Strings.isEmpty(modeloDocumento)){
			modeloDocumento = " ";
		}
		return modeloDocumento;
	}

	private void avisarErroAoVerificarCertificado(Exception e1) {
		FacesMessages.instance().add(Severity.ERROR, 
				"Erro ao verificar certificado: " + e1.getMessage());
	}

	private ProcessoDocumento createProcessoDocumento(String label, ProcessoDocumentoBin bin) {
		ProcessoDocumento doc = new ProcessoDocumento();
		doc.setProcessoDocumentoBin(bin);
		doc.setAtivo(Boolean.TRUE);
		doc.setDataInclusao(new Date());
		doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
		doc.setProcesso(getInstance());
		if (label == null) {
			doc.setProcessoDocumento("null");
		} else {
			doc.setProcessoDocumento(label);
		}
		inicializarTipoProcessoDocumento();
		doc.setTipoProcessoDocumento(tipoProcessoDocumento);
		getEntityManager().persist(doc);
		return doc;
	}

	/**
	 * Cria e configura um novo ProcessoDocumentoBin com base no {@value} passado
	 * @param value - valor da variável modeloDocumento no contexto jBPM
	 * */
	private ProcessoDocumentoBin createProcessoDocumentoBin(Object value) {
		ProcessoDocumentoBin bin = new ProcessoDocumentoBin();
		bin.setModeloDocumento(getDescricaoModeloDocumentoByValue(value));
		bin.setDataInclusao(new Date());
		bin.setMd5Documento(Crypto.encodeMD5(String.valueOf(value)));
		bin.setUsuario(Authenticator.getUsuarioLogado());
		bin.setCertChain(certChain);
		bin.setSignature(signature);
		getEntityManager().persist(bin);
		return bin;
	}
	
	public void carregarDadosFluxo(Integer idProcessoDocumento){
		ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, idProcessoDocumento);
		if(processoDocumento != null){
			setPdFluxo(processoDocumento);
			processoDocumentoBin = processoDocumento.getProcessoDocumentoBin();
			ProcessoDocumentoHome.instance().setInstance(processoDocumento);
			setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
			setTipoProcessoDocumento(processoDocumento.getTipoProcessoDocumento());
			onSelectProcessoDocumento();
		}
	}
	
	public void updateProcessoDocumentoBin() {
		String modelo = "";
		if(taskInstancePossuiModeloDocumento()) {
			modelo = ModeloDocumentoAction.instance().getConteudo(modeloDocumento);
		}
		processoDocumentoBin.setModeloDocumento(modelo);
	}
	
	private boolean taskInstancePossuiModeloDocumento(){
		return TaskInstanceHome.instance().getModeloDocumento() != null;
	}
	
	@Override
	protected Processo createInstance() {
		Processo processo = super.createInstance();
		processo.setUsuarioCadastroProcesso(Authenticator.getUsuarioLogado());
		return processo;
	}

	@Override
	public String remove() {
		Authenticator.getUsuarioLogado().getProcessoListForIdUsuarioCadastroProcesso()
					.remove(instance);
		return super.remove();
	}

	@Override
	public String remove(Processo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		refreshGrid("processoGrid");
		return ret;
	}

	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoList();
	}

	/**
	 * Metodo que adiciona o processo passado como parâmetro a lista dos processos
	 * que são conexos ao processo da instância.
	 * @param obj
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcesso(Processo obj, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcesso().add(obj);
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcesso(Processo obj, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcesso().remove(obj);
			refreshGrid(gridId);
		}
	}

	/**
	 * Metodo que adiciona o processo passado como parâmetro à lista dos processos
	 * que o processo da instância é conexo.
	 * @param processo
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcessoConexo().add(processo);
			getEntityManager().flush();
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null) {
			getInstance().getProcessoConexoListForIdProcessoConexo().remove(processo);
			getEntityManager().flush();
			refreshGrid(gridId);
		}
	}
		
// -----------------------------------------------------------------------------------------------------------------------
// -------------------------------------------- Getters e Setters --------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------------
	
	public void setProcessoIdProcesso(Integer id) {
		setId(id);
	}

	public Integer getProcessoIdProcesso() {
		return (Integer) getId();
	}
	
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
	
	public void setTarefaId(Long tarefaId) {
		this.tarefaId = tarefaId;
	}

	public Long getTarefaId() {
		return tarefaId;
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
		
	public String getNumeroProcesso(int idProcesso) {
		Processo processo = EntityUtil.find(Processo.class, idProcesso);
		if (processo != null) {
			return processo.getNumeroProcesso();
		}
		return String.valueOf(idProcesso);
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
		//TODO esperando Tássio verificar (21 de março de 2013)
	}
	
	
}