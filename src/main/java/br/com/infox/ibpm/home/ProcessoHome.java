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
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.cliente.home.ProcessoDocumentoHome;
import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.CertificadoException;
import br.com.infox.epp.documento.action.ModeloDocumentoAction;
import br.com.infox.epp.documento.dao.TipoProcessoDocumentoDAO;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.manager.ProcessoEpaManager;
import br.com.infox.epp.manager.ProcessoManager;
import br.com.infox.ibpm.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.ProcessoDocumento;
import br.com.infox.ibpm.entity.ProcessoDocumentoBin;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ibpm.jbpm.TaskInstanceHome;
import br.com.itx.component.AbstractHome;
import br.com.itx.component.Util;
import br.com.itx.exception.ApplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(ProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoHome extends AbstractHome<Processo> {
    private static final LogProvider LOG = Logging.getLogProvider(Processo.class);
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoHome";

	private static final int ERRO_AO_VERIFICAR_CERTIFICADO = 0;
		
	@In private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
	@In private TipoProcessoDocumentoDAO tipoProcessoDocumentoDAO;
	
	@In private ProcessoManager processoManager;
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
	    try {
	        processoManager.iniciarTask(instance, tarefaId, Authenticator.getUsuarioLocalizacaoAtual());
	    }catch(java.lang.NullPointerException e) {
	        LOG.error("ProcessoHome.iniciarTarefaProcesso()", e);
	    }
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
		FacesMessages.instance().add(Severity.ERROR, "Sem permissão para acessar o processo: " + getInstance().getNumeroProcesso());
	}
		
	public Integer salvarProcessoDocumentoFluxo(Object value, Integer idDoc, Boolean assinado, String label){
		ProcessoDocumento processoDocumento = buscarProcessoDocumento(idDoc);
		setIdProcessoDocumento(idDoc);
		Integer result = idDoc;
		if (processoDocumento != null) {
			atualizarProcessoDocumentoFluxo(value, idDoc, assinado);
		} else {
			result = inserirProcessoDocumentoFluxo(value, label, assinado);
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
			UsuarioLocalizacao usuarioLocalizacao = Authenticator.getUsuarioLocalizacaoAtual();
			processoDocumento.setPapel(usuarioLocalizacao.getPapel());
			processoDocumento.setLocalizacao(usuarioLocalizacao.getLocalizacao());
			atualizarProcessoDocumentoBin(processoDocumentoBin, modeloDocumento, usuarioLocalizacao.getUsuario());
			inicializarTipoProcessoDocumento();
			gravarAlteracoes(processoDocumento, processoDocumentoBin);
		}
	}

	private String getDescricaoModeloDocumentoFluxoByValue(Object value, String modeloDocumentoFluxo) {
		if (value == null) {
			value = modeloDocumentoFluxo != null ? modeloDocumentoFluxo : "";
		}
		return value.toString();
	}

	private void gravarAlteracoes(ProcessoDocumento processoDocumento, ProcessoDocumentoBin processoDocumentoBin) {
		processoDocumento.setTipoProcessoDocumento(tipoProcessoDocumento);
		getEntityManager().merge(processoDocumento);
		setIdProcessoDocumento(processoDocumento.getIdProcessoDocumento());
		getEntityManager().merge(processoDocumentoBin);
		getEntityManager().flush();
	}

	private void atualizarProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin, String modeloDocumento, UsuarioLogin assinante) {
		processoDocumentoBin.setModeloDocumento(modeloDocumento);
		processoDocumentoBin.setCertChain(certChain);
		processoDocumentoBin.setSignature(signature);
		processoDocumentoBin.setUsuarioUltimoAssinar(assinante.getNome());
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
			Object newValue = processoManager.getAlteracaoModeloDocumento(processoDocumentoBin, value);
			ProcessoDocumentoBin processoDocumentoBin = processoManager.createProcessoDocumentoBin(newValue, certChain, signature);
			label = label == null ? "-" : label;
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
	 * Metodo que adiciona o processo passado como parâmetro a lista dos processos
	 * que são conexos ao processo da instância.
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
	 * Metodo que adiciona o processo passado como parâmetro à lista dos processos
	 * que o processo da instância é conexo.
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
	
	public void verificaCertificadoUsuarioLogado(String certChainBase64Encoded, UsuarioLogin usuarioLogado) throws CertificadoException {
		if (Strings.isEmpty(usuarioLogado.getCertChain())) {
		    final Certificado certificado = new Certificado(certChainBase64Encoded);
		    final String cpfCertificado = certificado.getCn().split(":")[1];
		    if (usuarioLogado.getCpf().replace(".", "").equals(cpfCertificado)) {
		        usuarioLogado.setCertChain(certChainBase64Encoded);
		    } else {
    			limparAssinatura();
    			throw new ApplicationException("O cadastro do usuário não está assinado.");
		    }
		}
		if (!usuarioLogado.checkCertChain(certChainBase64Encoded)) {
			limparAssinatura();
			throw new ApplicationException("O certificado não é o mesmo do cadastro do usuario");
		}
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