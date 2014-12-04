package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.Token;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.component.tree.LocalizacaoSubTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.list.ParticipanteProcessoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacaoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.exception.BusinessException;

@Name(ModeloComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ModeloComunicacaoAction implements Serializable {
	public static final String NAME = "modeloComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ModeloComunicacaoAction.class);
	
	@In
	private TipoComunicacaoManager tipoComunicacaoManager;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ParticipanteProcessoManager participanteProcessoManager;
	@In
	private DocumentoManager documentoManager;
	@In
	private LocalizacaoSubTreeHandler localizacaoSubTree;
	@In
	private String raizLocalizacoesComunicacao;
	@In
	private LocalizacaoManager localizacaoManager;
	@In
	private UsuarioPerfilManager usuarioPerfilManager;
	@In
	private DocumentoComunicacaoList documentoComunicacaoList;
	@In
	private ParticipanteProcessoComunicacaoList participanteProcessoComunicacaoList;
	@In
	private PessoaFisicaManager pessoaFisicaManager;
	@In
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private GenericManager genericManager;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ProcessoManager processoManager;
	
	private ModeloComunicacao modeloComunicacao;
	private Long processInstanceId;
	
	private List<TipoComunicacao> tiposComunicacao;
	private List<ClassificacaoDocumento> classificacoes;
	private List<ModeloDocumento> modelosDocumento;
	
	private Localizacao localizacao;
	private List<Integer> idsLocalizacoesSelecionadas = new ArrayList<>();
	private boolean finalizada;
	private String certChain;
	private String signature;
	private boolean adicionarDestinatarioRelator;
	private boolean processoPossuiRelator;
	private Boolean expedida;
	private DestinatarioModeloComunicacao destinatario;
	private boolean inTask = false;
	
	@Create
	public void init() {
		String idJbpm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("jbpmProcessId");
		String idModelo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idModeloComunicacao");
		if (idJbpm != null) { // Nova comunicação fora da aba de saída
			processInstanceId = Long.valueOf(idJbpm);
		} else if (idModelo == null) { // Nova comunicação dentro da aba de saída
			processInstanceId = Long.valueOf(JbpmUtil.getProcesso().getIdJbpm());
			inTask = true;
		}
		initModelo(idModelo == null ? null : Long.valueOf(idModelo));
		initLists();
		initLocalizacaoRaiz();
		initClassificacoes();
	}

	private void initClassificacoes() {
		classificacoes = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true, null, null);
		if (classificacoes.size() == 1 && modeloComunicacao.getClassificacaoComunicacao() == null) {
			modeloComunicacao.setClassificacaoComunicacao(classificacoes.get(0));
		}
	}

	private void initLocalizacaoRaiz() {
		Localizacao localizacaoRaiz = localizacaoManager.getLocalizacaoDentroEstrutura(raizLocalizacoesComunicacao);
		if (localizacaoRaiz != null) {
			localizacaoSubTree.setIdLocalizacaoPai(localizacaoRaiz.getIdLocalizacao());
		} else {
			FacesMessages.instance().add("O parâmetro raizLocalizacoesComunicacao não foi definido.");
		}
	}

	/**
	 * Inicialização dos entity lists
	 */
	private void initLists() {
		documentoComunicacaoList.getEntity().setProcesso(modeloComunicacao.getProcesso());
		participanteProcessoComunicacaoList.getEntity().setProcesso(modeloComunicacao.getProcesso());
		
		for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
			documentoComunicacaoList.adicionarIdDocumentoBin(documentoModelo.getDocumento().getDocumentoBin().getId());
		}
		
		PessoaFisica relator = getRelator();
		processoPossuiRelator = relator != null;
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getDestinatario() != null) {
				participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
				if (relator != null && !adicionarDestinatarioRelator && relator.equals(destinatario.getDestinatario())) {
					adicionarDestinatarioRelator = true;
				}
			} else {
				idsLocalizacoesSelecionadas.add(destinatario.getDestino().getIdLocalizacao());
			}
		}
	}

	private void initModelo(Long idModelo) {
		if (idModelo == null) { // Nova comunicação
			org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
			if (taskInstance != null) { // Nova comunicação na aba de saída
				ContextInstance context = taskInstance.getContextInstance();
				Token taskToken = taskInstance.getToken();
				idModelo = (Long) context.getVariable("idModeloComunicacao", taskToken);
			}
		}
		if (idModelo == null) { // Nova Comunicação fora da aba de saída
			this.modeloComunicacao = new ModeloComunicacao();
			this.modeloComunicacao.setProcesso(processoManager.getProcessoByNumero(processoManager.getNumeroProcessoByIdJbpm(processInstanceId)));
		} else { // Comunicação existente
			this.modeloComunicacao = modeloComunicacaoManager.find(idModelo);
			setFinalizada(modeloComunicacao.getFinalizada() != null ? modeloComunicacao.getFinalizada() : false);
			this.processInstanceId = this.modeloComunicacao.getProcesso().getIdJbpm();
		}
	}
	
	public void gravar() {
		try {
			validarGravacao();
			
			if (modeloComunicacao.getId() == null) {
				modeloComunicacaoManager.persist(modeloComunicacao);
			}
			for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
				if (destinatario.getId() == null) {
					genericManager.persist(destinatario);
				}
			}
			for (DocumentoModeloComunicacao documento : modeloComunicacao.getDocumentos()) {
				if (documento.getId() == null) {
					genericManager.persist(documento);
				}
			}
			
			modeloComunicacaoManager.update(modeloComunicacao);
			setIdModeloVariable(modeloComunicacao.getId());
			if (isFinalizada()) {
				comunicacaoService.finalizarComunicacao(modeloComunicacao);
			}
			FacesMessages.instance().add("Registro gravado com sucesso");
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				FacesMessages.instance().add(e.getMessage());
			}
			resetEntityIds();
		}
	}

	private void setIdModeloVariable(Long id) {
		org.jbpm.taskmgmt.exe.TaskInstance taskInstance = TaskInstance.instance();
		if (taskInstance != null) {
			ContextInstance context = taskInstance.getContextInstance();
			Token taskToken = taskInstance.getToken();
			context.setVariable("idModeloComunicacao", id, taskToken);
		}
	}

	private void validarGravacao() {
		if (modeloComunicacao.getTipoComunicacao() == null) {
			throw new BusinessException("Escolha o tipo de comunicação");
		}
		if (modeloComunicacao.getDestinatarios().isEmpty()) {
			throw new BusinessException("Nenhum destinatário foi selecionado");
		}
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			if (destinatario.getMeioExpedicao() == null) {
				throw new BusinessException("Existe destinatário sem meio de expedição selecionado");
			}
		}
	}

	private void resetEntityIds() {
		this.finalizada = false;
		modeloComunicacao.setFinalizada(false);
		if (!modeloComunicacaoManager.contains(modeloComunicacao)) {
			modeloComunicacao.setId(null);
			setIdModeloVariable(null);
			for (DocumentoModeloComunicacao doc : modeloComunicacao.getDocumentos()) {
				if (!genericManager.contains(doc)) {
					doc.setId(null);
				}
			}
			for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
				if (!genericManager.contains(dest)) {
					dest.setId(null);
				}
			}
		}
	}

	private PessoaFisica getRelator() {
		MetadadoProcesso metadadoProcesso = modeloComunicacao.getProcesso().getMetadado(MetadadoProcessoType.RELATOR);
		return (PessoaFisica) (metadadoProcesso != null ? metadadoProcesso.getValue() : null);
	}
	
	public void expedirComunicacao() {
		try {
			assinaturaDocumentoService.assinarDocumento(destinatario.getComunicacao(), Authenticator.getUsuarioPerfilAtual(), certChain, signature);
			comunicacaoService.expedirComunicacao(destinatario);
			expedida = null;
		} catch (DAOException e) {
			LOG.error("Erro ao expedir comunicação " + modeloComunicacao.getId() + " para o destinatário " + destinatario.getId(), e);
			actionMessagesService.handleDAOException(e);
		} catch (CertificadoException e) {
			LOG.error("Erro ao expedir comunicação " + modeloComunicacao.getId() + " para o destinatário " + destinatario.getId(), e);
			actionMessagesService.handleException("Erro ao expedir comunicação", e);
		} catch (AssinaturaException e) {
			LOG.error("Erro ao expedir comunicação " + modeloComunicacao.getId() + " para o destinatário " + destinatario.getId(), e);
			FacesMessages.instance().add(e.getMessage());
		}
	}

	public void replicarPrazo(DestinatarioModeloComunicacao destinatario) {
		for (DestinatarioModeloComunicacao dest : modeloComunicacao.getDestinatarios()) {
			dest.setPrazo(destinatario.getPrazo());
		}
	}
	
	public void adicionarDestinatario(ParticipanteProcesso participante) {
		DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
		destinatario.setModeloComunicacao(modeloComunicacao);
		try {
			// Tem que remover o proxy porque o proxy vem como Pessoa. 
			// A query sempre retorna PessoaFisica
			destinatario.setDestinatario(pessoaFisicaManager.merge((PessoaFisica) HibernateUtil.removeProxy(participante.getPessoa())));
			participanteProcessoComunicacaoList.adicionarIdPessoa(destinatario.getDestinatario().getIdPessoa());
			modeloComunicacao.getDestinatarios().add(destinatario);
		} catch (DAOException e) {
			LOG.error("", e);
			FacesMessages.instance().add("Erro ao adicionar destinatário");
		}
	}
	
	public void adicionarDestino(Localizacao localizacao) {
		if (idsLocalizacoesSelecionadas.contains(localizacao.getIdLocalizacao())) {
			FacesMessages.instance().add("Localização já adicionada");
			return;
		}
		DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
		destinatario.setModeloComunicacao(modeloComunicacao);
		destinatario.setDestino(localizacao);
		modeloComunicacao.getDestinatarios().add(destinatario);
		idsLocalizacoesSelecionadas.add(localizacao.getIdLocalizacao());
	}
	
	public void removerDestinatario(DestinatarioModeloComunicacao destinatario) {
		modeloComunicacao.getDestinatarios().remove(destinatario);
		if (destinatario.getDestinatario() != null) {
			participanteProcessoComunicacaoList.removerIdPessoa(destinatario.getDestinatario().getIdPessoa());
			PessoaFisica relator = getRelator();
			if (adicionarDestinatarioRelator && destinatario.getDestinatario().equals(relator)) {
				adicionarDestinatarioRelator = false;
			}
		} else {
			idsLocalizacoesSelecionadas.remove(destinatario.getDestino().getIdLocalizacao());
		}
	}
	
	public void adicionarDocumento(Documento documento) {
		DocumentoModeloComunicacao documentoModelo = new DocumentoModeloComunicacao();
		documentoModelo.setDocumento(documento);
		documentoModelo.setModeloComunicacao(modeloComunicacao);
		modeloComunicacao.getDocumentos().add(documentoModelo);
		documentoComunicacaoList.adicionarIdDocumentoBin(documento.getDocumentoBin().getId());
	}
	
	public void removerDocumento(DocumentoModeloComunicacao documentoModelo) {
		modeloComunicacao.getDocumentos().remove(documentoModelo);
		documentoComunicacaoList.removerIdDocumentoBin(documentoModelo.getDocumento().getDocumentoBin().getId());
	}
	
	public void assignModeloDocumento() {
		if (modeloComunicacao.getModeloDocumento() == null) {
			modeloComunicacao.setTextoComunicacao("");
			return;
		}
		modeloComunicacao.setTextoComunicacao(modeloComunicacao.getModeloDocumento().getModeloDocumento());
	}
	
	public void gerenciarRelator() {
		PessoaFisica relator = getRelator();
		if (adicionarDestinatarioRelator) {
			DestinatarioModeloComunicacao destinatario = new DestinatarioModeloComunicacao();
			destinatario.setDestinatario(relator);
			destinatario.setModeloComunicacao(modeloComunicacao);
			modeloComunicacao.getDestinatarios().add(destinatario);
			participanteProcessoComunicacaoList.adicionarIdPessoa(relator.getIdPessoa());
		} else {
			Iterator<DestinatarioModeloComunicacao> it = modeloComunicacao.getDestinatarios().iterator();
			while (it.hasNext()) {
				DestinatarioModeloComunicacao destinatario = it.next();
				if (destinatario.getDestinatario() != null && destinatario.getDestinatario().equals(relator)) {
					it.remove();
					participanteProcessoComunicacaoList.removerIdPessoa(relator.getIdPessoa());
					break;
				}
			}
		}
	}

	public void download() {
		downloadComunicacao(destinatario);
	}
	
	public void downloadComunicacao(DestinatarioModeloComunicacao destinatario) {
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(modeloComunicacao, destinatario);
			FileDownloader.download(pdf, "application/pdf", "comunicacao");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public String getLink(DocumentoBin documento) {
		return documentoBinManager.getUrlValidacaoDocumento(documento);
	}
	
	public MeioExpedicao[] getMeiosExpedicao(DestinatarioModeloComunicacao destinatario) {
		if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoa = destinatario.getDestinatario();
			if (pessoa.getTermoAdesao() != null) {
				return MeioExpedicao.getValues(true);
			}
		}
		return MeioExpedicao.getValues(false);
	}
	
	public List<PerfilTemplate> getPerfisPermitidos() {
		if (modeloComunicacao.getLocalizacaoResponsavelAssinatura() == null) {
			return Collections.emptyList();
		}
		return usuarioPerfilManager.getPerfisPermitidos(modeloComunicacao.getLocalizacaoResponsavelAssinatura());
	}
	
	public List<TipoComunicacao> getTiposComunicacao() {
		if (tiposComunicacao == null) {
			tiposComunicacao = tipoComunicacaoManager.listTiposComunicacaoAtivos();
		}
		return tiposComunicacao;
	}
	
	public String getCertChain() {
		return certChain;
	}
	
	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public boolean isFinalizada() {
		return finalizada;
	}
	
	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
		if (!this.finalizada) {
			modeloComunicacao.setLocalizacaoResponsavelAssinatura(null);
			modeloComunicacao.setPerfilResponsavelAssinatura(null);
			localizacaoSubTree.clearTree();
		}
	}
	
	public List<ClassificacaoDocumento> getClassificacoes() {
		return classificacoes;
	}
	
	public void setClassificacoes(List<ClassificacaoDocumento> classificacoes) {
		this.classificacoes = classificacoes;
	}

	public Localizacao getLocalizacao() {
		return localizacao;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	public ModeloComunicacao getModeloComunicacao() {
		return modeloComunicacao;
	}
	
	public List<ModeloDocumento> getModelosDocumento() {
		if (modelosDocumento == null) {
			modelosDocumento = modeloDocumentoManager.getModeloDocumentoList();
		}
		return modelosDocumento;
	}

	public boolean isAdicionarDestinatarioRelator() {
		return adicionarDestinatarioRelator;
	}
	
	public void setAdicionarDestinatarioRelator(boolean adicionarDestinatarioRelator) {
		this.adicionarDestinatarioRelator = adicionarDestinatarioRelator;
	}
	
	public boolean isProcessoPossuiRelator() {
		return processoPossuiRelator;
	}
	
	public boolean isExpedida() {
		if (expedida == null && modeloComunicacao.getFinalizada()) {
			expedida = modeloComunicacaoManager.isExpedida(modeloComunicacao);
		}
		return modeloComunicacao.getFinalizada() && expedida;
	}
	
	public boolean podeRenderizarApplet() {
		UsuarioPerfil usuarioPerfil = Authenticator.getUsuarioPerfilAtual();
		Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
		UsuarioLogin usuario = usuarioPerfil.getUsuarioLogin();
		return destinatario != null && assinaturaDocumentoService.podeRenderizarApplet(papel, modeloComunicacao.getClassificacaoComunicacao(), destinatario.getComunicacao(), usuario);
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
		destinatario.getComunicacao().setModeloDocumento(comunicacaoService.evaluateComunicacao(destinatario));
		destinatario.getComunicacao().setMd5Documento(MD5Encoder.encode(destinatario.getComunicacao().getModeloDocumento()));
	}
	
	public String getFaceletPath() {
		return getClass().getResource("/taskpages/enviarComunicacao.xhtml").toString();
	}
	
	public boolean isInTask() {
		return inTask;
	}
}
