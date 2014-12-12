package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.ModeloComunicacaoRascunhoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(ComunicacaoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ComunicacaoAction implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "comunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ComunicacaoAction.class);
	
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private DocumentoUploader documentoUploader;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@In
	private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@In
	private ModeloComunicacaoRascunhoList modeloComunicacaoRascunhoList;
	@In
	private GenericManager genericManager;
	@In
	private DocumentoDownloader documentoDownloader;
	
	private List<ModeloComunicacao> comunicacoes;
	private Map<Long, List<DestinatarioBean>> destinatarioBeans = new HashMap<>(); // Cache dos destinatários da comunicação
	private List<ClassificacaoDocumento> classificacoesDocumento;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private boolean usuarioExterno = Identity.instance().hasRole("usuarioExterno");
	private Processo processo;
	private List<Documento> documentosDestinatario; // Cache dos documentos do destinatário selecionado
	private Map<Long, Boolean> dadosCiencia = new HashMap<>(); // Cache das confirmações de ciência dos destinatários
	
	private DestinatarioBean destinatario;
	private Date dataCiencia;
	private boolean ciencia;
	
	private boolean prorrogacaoPrazo;
	private boolean documentos;
	
	@Create
	public void init() {
		processo = JbpmUtil.getProcesso();
		modeloComunicacaoRascunhoList.setProcesso(processo);
	}
	
	public List<ModeloComunicacao> getComunicacoesDoProcesso() {
		if (comunicacoes == null) {
			comunicacoes = modeloComunicacaoManager.listModelosComunicacaoPorProcesso(processo);
		}
		return comunicacoes;
	}
	
	public void clearCacheModelos() {
		this.comunicacoes = null;
	}
	
	public List<DestinatarioBean> getDestinatarios(ModeloComunicacao modeloComunicacao) {
		List<DestinatarioBean> destinatarios = destinatarioBeans.get(modeloComunicacao.getId());
		if (destinatarios == null) {
			destinatarios = new ArrayList<>();
			for (DestinatarioModeloComunicacao destinatarioModeloComunicacao : modeloComunicacao.getDestinatarios()) {
				boolean cienciaConfirmada = isCienciaConfirmada(destinatarioModeloComunicacao);
				dadosCiencia.put(destinatarioModeloComunicacao.getId(), cienciaConfirmada);
				if (usuarioExterno && !cienciaConfirmada) {
					continue;
				}
				destinatarios.add(createDestinatarioBean(destinatarioModeloComunicacao));
			}
			Collections.sort(destinatarios, new Comparator<DestinatarioBean>() {
				@Override
				public int compare(DestinatarioBean o1, DestinatarioBean o2) {
					try {
						Date d1 = dateFormat.parse(o1.getDataEnvio());
						Date d2 = dateFormat.parse(o2.getDataEnvio());
						return d2.compareTo(d1);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		return destinatarios;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumento() {
		if (classificacoesDocumento == null) {
			if (isCiencia()) {
				classificacoesDocumento = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(false);
				if (classificacoesDocumento.size() == 1) {
					documentoUploader.setClassificacaoDocumento(classificacoesDocumento.get(0));
				}
			}
		}
		return classificacoesDocumento;
	}
	
	public DestinatarioBean getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatarioCiencia(DestinatarioBean destinatario) {
		this.destinatario = destinatario;
		this.documentosDestinatario = null;
		ciencia = true;
		prorrogacaoPrazo = false;
		documentos = false;
		dataCiencia = null;
		documentoUploader.clear();
	}
	
	public Date getDataCiencia() {
		return dataCiencia;
	}
	
	public void setDataCiencia(Date dataCiencia) {
		this.dataCiencia = dataCiencia;
	}
	
	public boolean isCiencia() {
		return ciencia;
	}
	
	public void darCiencia() {
		Documento documento = documentoUploader.getDocumento();
		Processo comunicacao = destinatario.getComunicacao();

		StringBuilder msg = new StringBuilder();
		if (getClassificacaoDocumento() == null) {
			msg.append("Informe a classificação do documento\n");
		}
		if (getDataCiencia() == null) {
			msg.append("Informe a data de ciência\n");
		}
		if (documento == null) {
			msg.append("Informe o documento de comprovação de ciência");
		}
		
		if (msg.length() > 0) {
			FacesMessages.instance().add(msg.toString());
			return;
		}
		
		try {
			documento.setProcesso(comunicacao);
			Processo processo = documentoUploader.getProcesso();
			documentoUploader.setProcesso(comunicacao);
			documentoUploader.persist();
			documentoUploader.setProcesso(processo);
			
			MetadadoProcesso metadadoDocumento = new MetadadoProcesso();
			metadadoDocumento.setClassType(Documento.class);
			metadadoDocumento.setMetadadoType(ComunicacaoService.DOCUMENTO_COMPROVACAO_CIENCIA);
			metadadoDocumento.setProcesso(comunicacao);
			metadadoDocumento.setValor(documento.getId().toString());
			metadadoProcessoManager.persist(metadadoDocumento);
			
			MetadadoProcesso metadadoDataCiencia = new MetadadoProcesso();
			metadadoDataCiencia.setClassType(Date.class);
			metadadoDataCiencia.setMetadadoType(ComunicacaoService.DATA_CIENCIA);
			metadadoDataCiencia.setProcesso(comunicacao);
			metadadoDataCiencia.setValor(new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataCiencia));
			
			MetadadoProcesso metadadoResponsavelCiencia = new MetadadoProcesso();
			metadadoResponsavelCiencia.setClassType(UsuarioLogin.class);
			metadadoResponsavelCiencia.setMetadadoType(ComunicacaoService.RESPONSAVEL_CIENCIA);
			metadadoResponsavelCiencia.setProcesso(comunicacao);
			metadadoResponsavelCiencia.setValor(Authenticator.getUsuarioLogado().getIdUsuarioLogin().toString());
			
			metadadoProcessoManager.persist(metadadoDataCiencia);
			metadadoProcessoManager.persist(metadadoResponsavelCiencia);
			
			FacesMessages.instance().add("Ciência informada com sucesso");
			destinatarioBeans.remove(destinatario.getIdModeloComunicacao());
			dadosCiencia.put(destinatario.getIdDestinatario(), true);
			destinatario = null;
			dataCiencia = null;
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		ClassificacaoDocumento classificacaoProrrogacao = classificacaoDocumentoManager.findByDescricao("Pedido de Prorrogação de Prazo");
		if (classificacaoProrrogacao == null) {
			FacesMessages.instance().add("A classificação de documento Pedido de Prorrogação de Prazo não existe");
			return;
		}
		this.destinatario = destinatario;
		this.documentosDestinatario = null;
		prorrogacaoPrazo = true;
		ciencia = false;
		documentos = false;
		dataCiencia = null;
		documentoUploader.clear();
		documentoUploader.setClassificacaoDocumento(classificacaoProrrogacao);
	}
	
	public boolean isDocumentos() {
		return documentos;
	}
	
	public void setDestinatarioDocumentos(DestinatarioBean destinatario) {
		this.destinatario = destinatario;
		this.documentosDestinatario = null;
		documentos = true;
		prorrogacaoPrazo = false;
		dataCiencia = null;
		ciencia = false;
	}
	
	public void pedirProrrogacaoPrazo() {
		
	}
	
	public ClassificacaoDocumento getClassificacaoDocumento() {
		return documentoUploader.getClassificacaoDocumento();
	}
	
	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		documentoUploader.setClassificacaoDocumento(classificacaoDocumento);
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public void downloadComunicacao(DestinatarioBean destinatario) {
		DestinatarioModeloComunicacao destinatarioModelo = genericManager.find(DestinatarioModeloComunicacao.class, destinatario.getIdDestinatario());
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(destinatarioModelo.getModeloComunicacao(), destinatarioModelo);
			FileDownloader.download(pdf, "application/pdf", "Comunicação.pdf");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public List<Documento> getDocumentosDestinatario() {
		if (documentosDestinatario == null) {
			DestinatarioModeloComunicacao destinatarioModelo = genericManager.find(DestinatarioModeloComunicacao.class, destinatario.getIdDestinatario());
			documentosDestinatario = new ArrayList<>();
			Documento comunicacao = new Documento();
			comunicacao.setDescricao("Comunicação");
			comunicacao.setDocumentoBin(destinatarioModelo.getComunicacao());
			documentosDestinatario.add(comunicacao);
			for (DocumentoModeloComunicacao documentoModelo : destinatarioModelo.getModeloComunicacao().getDocumentos()) {
				documentosDestinatario.add(documentoModelo.getDocumento());
			}
		}
		return documentosDestinatario;
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public boolean isCienciaConfirmada(DestinatarioBean bean) {
		return dadosCiencia.get(bean.getIdDestinatario());
	}
	
	private DestinatarioBean createDestinatarioBean(DestinatarioModeloComunicacao destinatario) {
		DestinatarioBean bean = new DestinatarioBean();
		bean.setIdDestinatario(destinatario.getId());
		bean.setComunicacao(modeloComunicacaoManager.getComunicacao(destinatario));
		bean.setMeioExpedicao(destinatario.getMeioExpedicao().getLabel());
		bean.setTipoComunicacao(destinatario.getModeloComunicacao().getTipoComunicacao().getDescricao());
		bean.setNome(destinatario.getNome());
		bean.setPrazoAtendimento(destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : "-");
		
		Processo comunicacao = bean.getComunicacao();
		bean.setDataEnvio(dateFormat.format(comunicacao.getDataInicio()));
		bean.setDataConfirmacao(getDataConfirmacao(comunicacao));
		bean.setResponsavelConfirmacao(getResponsavelConfirmacao(comunicacao));
		bean.setPrazoFinal(getPrazoFinal(comunicacao));
		return bean;
	}
	
	private boolean isCienciaConfirmada(DestinatarioModeloComunicacao destinatario) {
		Processo comunicacao = modeloComunicacaoManager.getComunicacao(destinatario);
		return !getDataConfirmacao(comunicacao).equals("-");
	}
	
	private String getDataConfirmacao(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoService.DATA_CIENCIA);
		if (metadado != null) {
			return dateFormat.format(metadado.getValue());
		}
		return "-";
	}
	
	private String getResponsavelConfirmacao(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoService.RESPONSAVEL_CIENCIA);
		if (metadado != null) {
			UsuarioLogin usuario = metadado.getValue();
			return usuario.getNomeUsuario();
		}
		return "-";
	}
	
	private String getPrazoFinal(Processo comunicacao) {
		MetadadoProcesso metadado = comunicacao.getMetadado(ComunicacaoService.DATA_CIENCIA);
		if (metadado != null) {
			Date dataCiencia = metadado.getValue();
			Date prazo = comunicacaoService.contabilizarPrazoCumprimento(comunicacao, dataCiencia);
			if (prazo != null) {
				return dateFormat.format(prazo);
			}
		}
		return "-";
	}

	public static class DestinatarioBean {
		private Long idDestinatario;
		private String nome;
		private String tipoComunicacao;
		private String meioExpedicao;
		private String dataEnvio;
		private String dataConfirmacao;
		private String responsavelConfirmacao;
		private String prazoAtendimento;
		private String prazoFinal;
		private Processo comunicacao;
		private Long idModeloComunicacao;
		
		public Long getIdDestinatario() {
			return idDestinatario;
		}
		public void setIdDestinatario(Long idDestinatario) {
			this.idDestinatario = idDestinatario;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getTipoComunicacao() {
			return tipoComunicacao;
		}
		public void setTipoComunicacao(String tipoComunicacao) {
			this.tipoComunicacao = tipoComunicacao;
		}
		public String getMeioExpedicao() {
			return meioExpedicao;
		}
		public void setMeioExpedicao(String meioExpedicao) {
			this.meioExpedicao = meioExpedicao;
		}
		public String getDataEnvio() {
			return dataEnvio;
		}
		public void setDataEnvio(String dataEnvio) {
			this.dataEnvio = dataEnvio;
		}
		public String getDataConfirmacao() {
			return dataConfirmacao;
		}
		public void setDataConfirmacao(String dataConfirmacao) {
			this.dataConfirmacao = dataConfirmacao;
		}
		public String getResponsavelConfirmacao() {
			return responsavelConfirmacao;
		}
		public void setResponsavelConfirmacao(String responsavelConfirmacao) {
			this.responsavelConfirmacao = responsavelConfirmacao;
		}
		public String getPrazoAtendimento() {
			return prazoAtendimento;
		}
		public void setPrazoAtendimento(String prazoAtendimento) {
			this.prazoAtendimento = prazoAtendimento;
		}
		public String getPrazoFinal() {
			return prazoFinal;
		}
		public void setPrazoFinal(String prazoFinal) {
			this.prazoFinal = prazoFinal;
		}
		public Processo getComunicacao() {
			return comunicacao;
		}
		public void setComunicacao(Processo comunicacao) {
			this.comunicacao = comunicacao;
		}
		public Long getIdModeloComunicacao() {
			return idModeloComunicacao;
		}
		public void setIdModeloComunicacao(Long idModeloComunicacao) {
			this.idModeloComunicacao = idModeloComunicacao;
		}
	}
}
