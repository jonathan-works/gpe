/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.epp.dao.ProcessoEpaDAO;
import br.com.infox.epp.manager.ProcessoEpaManager;
import br.com.infox.epp.manager.ProcessoManager;
import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.dao.TipoProcessoDocumentoDAO;
import br.com.infox.ibpm.entity.ModeloDocumento;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.TipoProcessoDocumento;
import br.com.infox.ibpm.jbpm.TaskInstanceHome;
import br.com.infox.ibpm.jbpm.actions.ModeloDocumentoAction;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoHome extends AbstractHome<Processo> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoHome";

	private static final int ERRO_AO_VERIFICAR_CERTIFICADO = 0;
		
	@In private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
	@In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;
	
	@In private ProcessoManager processoManager;
	@In private ProcessoEpaDAO processoEpaDAO;
	@In private ProcessoEpaManager processoEpaManager;

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
	private boolean checkVisibilidade=true;
	private boolean possuiPermissaoVisibilidade=false;

	private Long tarefaId;

	private Boolean podeInativarParteProcesso;

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
		processoManager.iniciarTask(instance, tarefaId, Authenticator.getUsuarioLocalizacaoAtual());
	}
	
	public void visualizarTarefaProcesso(){
		processoManager.visualizarTask(instance, tarefaId,Authenticator. getUsuarioLocalizacaoAtual());
	}
	
	public static ProcessoHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
		
	private void limparAssinatura() {
		certChain = null;
		signature = null;
	}
	
	public Boolean checarVisibilidade()	{
		if (checkVisibilidade) {
			possuiPermissaoVisibilidade = processoLocalizacaoIbpmDAO.possuiPermissao();
			checkVisibilidade = false;
		}
		if(!possuiPermissaoVisibilidade) {
			avisarNaoHaPermissaoParaAcessarProcesso();
		}
		return possuiPermissaoVisibilidade;
	}

	private void avisarNaoHaPermissaoParaAcessarProcesso() {
		Util.setToEventContext("canClosePanel", true);
		FacesMessages.instance().clear();
		FacesMessages.instance().add(Severity.ERROR, "Sem permiss�o para acessar o processo: " + getInstance().getNumeroProcesso());
	}
		
	
	public void onSelectProcessoDocumento() {
		limparEventsTreeHandler();
	}
	
	private void limparEventsTreeHandler() {
		AutomaticEventsTreeHandler.instance().clearList();
		AutomaticEventsTreeHandler.instance().clearTree();
		renderEventsTree = false;
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
	
	//M�todo para Atualizar o documento do fluxo
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
	 *o editor sem assinatura digital, o tipoProcessoDOcumento ser� setado
	 *automaticamente com um valor aleatorio 
	 */
	private void inicializarTipoProcessoDocumento() {
		if (tipoProcessoDocumento == null){
			tipoProcessoDocumento = tipoProcessoDocumentoDAO.getTipoProcessoDocumentoFluxo();
		}
	}
	
	//M�todo para Inserir o documento do fluxo
	private Integer inserirProcessoDocumentoFluxo(Object value, String label, Boolean assinado){
		if (validacaoCertificadoBemSucedida(assinado)) {
			value = processoManager.getAlteracaoModeloDocumento(processoDocumentoBin, value);
			ProcessoDocumentoBin processoDocumentoBin = processoManager.createProcessoDocumentoBin(value, certChain, signature);
			ProcessoDocumento doc = processoManager.createProcessoDocumento(getInstance(), label, processoDocumentoBin, getTipoProcessoDocumento());
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

	private void avisarErroAoVerificarCertificado(Exception e1) {
		FacesMessages.instance().add(Severity.ERROR, "Erro ao verificar certificado: " + e1.getMessage());
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
		Authenticator.getUsuarioLogado().getProcessoListForIdUsuarioCadastroProcesso().remove(instance);
		return super.remove();
	}

	@Override
	public String remove(Processo obj) {
		setInstance(obj);
		String ret = super.remove();
		newInstance();
		return ret;
	}

	public List<ProcessoDocumento> getProcessoDocumentoList() {
		return getInstance() == null ? null : getInstance()
				.getProcessoDocumentoList();
	}

	/**
	 * Metodo que adiciona o processo passado como par�metro a lista dos processos
	 * que s�o conexos ao processo da inst�ncia.
	 * @param processoConexo
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcesso(Processo processoConexo, String gridId) {
		if (getInstance() != null){
			processoManager.addProcessoConexoForIdProcesso(getInstance(), processoConexo);
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcesso(Processo processoConexo, String gridId) {
		if (getInstance() != null){
			processoManager.removeProcessoConexoForIdProcesso(getInstance(), processoConexo);
			refreshGrid(gridId);
		}
	}

	/**
	 * Metodo que adiciona o processo passado como par�metro � lista dos processos
	 * que o processo da inst�ncia � conexo.
	 * @param processo
	 * @param gridId
	 */
	public void addProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null){
			processoManager.addProcessoConexoForIdProcessoConexo(getInstance(), processo);
			refreshGrid(gridId);
		}
	}

	public void removeProcessoConexoForIdProcessoConexo(Processo processo, String gridId) {
		if (getInstance() != null){
			processoManager.removeProcessoConexoForIdProcessoConexo(getInstance(), processo);
			refreshGrid(gridId);
		}
	}
	
	public boolean hasPartes(){
		return processoManager.hasPartes(getInstance());
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
		if (tipoProcessoDocumento == null){
			tipoProcessoDocumento = tipoProcessoDocumentoDAO.getTipoProcessoDocumentoFluxo();
		}
		return tipoProcessoDocumento;
	}
	
	public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {	
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
			throw new Exception("O cadastro do usu�rio n�o est� assinado.");
		}
		if (!usuarioLogado.checkCertChain(certChainBase64Encoded)) {
			limparAssinatura();
			throw new Exception("O certificado n�o � o mesmo do cadastro do usuario");
		}
		//TODO usar o VerificaCertificado que hoje sim est� no PJE2, tem de migrar o que nao � do PJE2 pro core.
		//TODO esperando T�ssio verificar (21 de mar�o de 2013)
	}

	public Boolean getPodeInativarParteProcesso() {
		if (podeInativarParteProcesso == null){
			podeInativarParteProcesso = processoEpaManager.podeInativarPartesDoProcesso(instance);
		}
		return podeInativarParteProcesso;
	}
	
	public void setPodeInativarParteProcesso(Boolean podeInativarParteProcesso) {
		return;
	}

	@Observer(ParteProcesso.ALTERACAO_ATIVIDADE_PARTE_PROCESSO)
	public void setPodeInativarParteProcesso() {
		this.podeInativarParteProcesso = processoEpaManager.podeInativarPartesDoProcesso(instance);
	}
	
}