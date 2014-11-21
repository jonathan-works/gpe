package br.com.infox.epp.processo.comunicacao.manager;

import java.io.ByteArrayOutputStream;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Tag;
import org.xhtmlrenderer.pdf.ITextRenderer;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

import com.github.neoflyingsaucer.defaultuseragent.DefaultUserAgent;
import com.github.neoflyingsaucer.jsouptodom.DOMBuilder;
import com.lowagie.text.DocumentException;

@Name(ModeloComunicacaoManager.NAME)
@AutoCreate
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	@In
	private DocumentoManager documentoManager;
	@In
	private ProcessoDAO processoDAO;
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private FluxoManager fluxoManager;
	@In
	private String codigoFluxoComunicacao;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	
	public void expedirComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
//		String oldText = modeloComunicacao.getComunicacao().getModeloDocumento();
//		DocumentoBin comunicacao = modeloComunicacao.getComunicacao();
//
//		try {
//			Date dataFimPrazoCiencia = DateTime.now().plusDays(modeloComunicacao.getTipoComunicacao().getQuantidadeDiasCiencia()).toDate();
//			Localizacao localizacao = Authenticator.getLocalizacaoAtual();
//			UsuarioLogin usuario = Authenticator.getUsuarioLogado();
//			Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacao);
//			if (fluxo == null) {
//				throw new DAOException("Fluxo de comunicação não encontrado");
//			}
//			List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
//			if (ncfs.isEmpty()) {
//				throw new DAOException("Não existe natureza/categoria/fluxo configurada para o fluxo de comunicação");
//			}
//			NaturezaCategoriaFluxo ncf = ncfs.get(0);
//
//			comunicacao.setModeloDocumento(modeloDocumentoManager.evaluateModeloDocumento(modeloComunicacao.getModeloDocumento(), comunicacao.getModeloDocumento(), null));
//			comunicacao.setMd5Documento(MD5Encoder.encode(comunicacao.getModeloDocumento()));
//			
//			for (DestinatarioModeloComunicacao destinatarioModelo : modeloComunicacao.getDestinatarios()) {
//				ProcessoComunicacao processo = new ProcessoComunicacao();
//				processo.setModeloComunicacao(modeloComunicacao);
//				processo.setDataInicio(new Date());
//				processo.setDataFimPrazoCiencia(dataFimPrazoCiencia);
//				processo.setLocalizacao(localizacao);
//				processo.setPrazo(destinatarioModelo.getPrazo());
//				processo.setTipoComunicacao(modeloComunicacao.getTipoComunicacao());
//				processo.setUsuarioCadastroProcesso(usuario);
//				if (destinatarioModelo.getDestinatario() != null) {
//					processo.setDestinatario(destinatarioModelo.getDestinatario());
//				} else {
//					processo.setLocalizacaoDestinataria(destinatarioModelo.getLocalizacaoDestinataria());
//				}
//				processo.setMeioExpedicao(destinatarioModelo.getMeioExpedicao());
//				processo.setNumeroProcesso("");
//				processo.setNaturezaCategoriaFluxo(ncf);
//				processoDAO.persist(processo);
//				
//				processo.setNumeroProcesso(processo.getIdProcesso().toString());
//				processo.setComunicacao(documentoManager.createDocumento(processo, comunicacao.getNomeArquivo(), comunicacao, modeloComunicacao.getClassificacaoComunicacao()));
//				for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
//					Documento documento = documentoModelo.getDocumento();
//					processo.getDocumentoList().add(documentoManager.createDocumento(processo, documento.getDescricao(), documento.getDocumentoBin(), documento.getClassificacaoDocumento()));
//				}
//				processoDAO.update(processo);
//			}
//			modeloComunicacao.setExpedida(true);
//			update(modeloComunicacao);
//		} catch (Exception e) {
//			comunicacao.setModeloDocumento(oldText);
//			comunicacao.setMd5Documento(MD5Encoder.encode(oldText));
//			throw new DAOException(e);
//		}
	}
	
	public byte[] gerarPdfCompleto(ModeloComunicacao modeloComunicacao) throws DAOException {
		ByteArrayOutputStream pdf = new ByteArrayOutputStream();
		Document doc = Jsoup.parse(modeloComunicacao.getComunicacao().getModeloDocumento());
		doc.outputSettings().escapeMode(EscapeMode.xhtml);
		Element head = doc.head();
		Element style = new Element(Tag.valueOf("style"), doc.baseUri());
		style.text("img { -fs-fit-images-to-width: 100% }");
		head.appendChild(style);
		DefaultUserAgent userAgent = new DefaultUserAgent();
		ITextRenderer renderer = new ITextRenderer(userAgent);
		renderer.setDocument(DOMBuilder.jsoup2DOM(doc), doc.baseUri());
		renderer.layout();
		try {
			renderer.createPDF(pdf);
		} catch (DocumentException e) {
			throw new DAOException(e);
		}
		return pdf.toByteArray();
	}
}
