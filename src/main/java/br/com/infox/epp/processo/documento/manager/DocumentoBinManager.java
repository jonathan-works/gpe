package br.com.infox.epp.processo.documento.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.path.PathResolver;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@AutoCreate
@Name(DocumentoBinManager.NAME)
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoBinManager";

	@In
	private PathResolver pathResolver;
	@In
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private DocumentoBinarioManager documentoBinarioManager;

	public DocumentoBin createProcessoDocumentoBin(final Documento documento) throws DAOException {
		return createProcessoDocumentoBin(documento.getDocumentoBin());
	}
	
	public DocumentoBin createProcessoDocumentoBin(DocumentoBin bin) throws DAOException {
		byte[] dados = bin.getProcessoDocumento();
		if (bin.isBinario() && dados != null) {
			bin.setModeloDocumento(InfoxPdfReader.readPdfFromByteArray(dados));
		}
		if (bin.getMd5Documento() == null) {
			if (bin.isBinario()) {
				bin.setMd5Documento(MD5Encoder.encode(dados));
			} else {
				bin.setMd5Documento(MD5Encoder.encode(bin.getModeloDocumento()));
			}
		}
		bin = persist(bin);
		if (bin.isBinario() && dados != null) {
			documentoBinarioManager.salvarBinario(bin.getId(), dados);
		}
		return bin;
	}

	public DocumentoBin createProcessoDocumentoBin(final String tituloDocumento, final String conteudo)
			throws DAOException {
		final DocumentoBin bin = new DocumentoBin();
		bin.setNomeArquivo(tituloDocumento);
		bin.setModeloDocumento(conteudo);
		bin.setMd5Documento(MD5Encoder.encode(conteudo));
		bin.setMinuta(false);
		return persist(bin);
	}
	
	public DocumentoBin createProcessoDocumentoBin(final String tituloDocumento, final byte[] conteudo, final String fileType) throws DAOException{
		DocumentoBin bin = new DocumentoBin();
        bin.setNomeArquivo(tituloDocumento);
        bin.setExtensao(fileType);
        bin.setMd5Documento(MD5Encoder.encode(conteudo));
        bin.setSize(conteudo.length);
        bin.setProcessoDocumento(conteudo);
        bin.setModeloDocumento(InfoxPdfReader.readPdfFromByteArray(conteudo));
        bin.setDataInclusao(new Date());
        return persist(bin);
	}

	public DocumentoBin getByUUID(final UUID uuid) {
		return getDao().getByUUID(uuid);
	}

	public void writeMargemDocumento(final DocumentoBin documento, final byte[] pdf, final OutputStream outStream) {
		try {
			final PdfReader pdfReader = new PdfReader(pdf);
			final PdfStamper stamper = new PdfStamper(pdfReader, outStream);
			final Font font = new Font(Font.TIMES_ROMAN, 8);

			final Phrase phrase = createPhraseAssinatura(documento, font);
			final Phrase codPhrase = createPhraseCodigo(font, documento.getUuid().toString());

			final byte[] qrcode = QRCode.from(getUrlValidacaoDocumento() + "?cod=" + documento.getUuid().toString())
					.to(ImageType.GIF).withSize(60, 60).stream().toByteArray();
			for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
				int rotation = pdfReader.getPageRotation(page);
				final PdfContentByte content = stamper.getOverContent(page);
				final Image image = Image.getInstance(qrcode);
				float right = pdfReader.getCropBox(page).getRight();
				float top = pdfReader.getCropBox(page).getTop();
				if (rotation == 90 || rotation == 270) {
					// Invertendo posições quando o PDF estiver em modo Paisagem
					float tempRight = right;
					right = top;
					top = tempRight;
				}
				image.setAbsolutePosition(right - 60, top - 70);
				content.addImage(image);
				ColumnText.showTextAligned(content, Element.ALIGN_LEFT, phrase, right - 15, top - 70, -90);
				ColumnText.showTextAligned(content, Element.ALIGN_LEFT, codPhrase, right - 25, top - 70, -90);
			}

			stamper.close();
			outStream.flush();
		} catch (IOException | DocumentException e) {
			throw new BusinessException("Erro ao gravar a margem do PDF", e);
		}
	}

	private Phrase createPhraseCodigo(final Font font, final String uuid) {
		final StringBuilder sb = new StringBuilder("Acesse em: ");
		sb.append(getUrlValidacaoDocumento());
		sb.append(" Código do documento: ");
		sb.append(uuid);
		final Phrase codPhrase = new Phrase(sb.toString(), font);
		return codPhrase;
	}

	private Phrase createPhraseAssinatura(final DocumentoBin documento, final Font font) {
		final StringBuilder sb = new StringBuilder("Documento Assinado Digitalmente por: ");
		for (final AssinaturaDocumento assinatura : documento.getAssinaturas()) {
			sb.append(assinatura.getNomeUsuario());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		final Phrase phrase = new Phrase(sb.toString(), font);
		return phrase;
	}

	@Override
	public DocumentoBin persist(DocumentoBin o) throws DAOException {
		try {
			o.setUuid(UUID.randomUUID());
			if (o.isBinario()) {
                o.setMinuta(false);
            }
			if (!o.getSuficientementeAssinado() && !o.getDocumentoList().isEmpty()) {
				if (!classificacaoDocumentoPapelManager.classificacaoExigeAssinatura(o.getDocumentoList().get(0).getClassificacaoDocumento()) && !o.isMinuta()) {
					assinaturaDocumentoService.setDocumentoSuficientementeAssinado(o, Authenticator.getUsuarioPerfilAtual());
				}
			}
			o = super.persist(o);
		} catch (final DAOException e) {
			final GenericDatabaseErrorCode error = e.getDatabaseErrorCode();
			if ((error != null) && (error == GenericDatabaseErrorCode.UNIQUE_VIOLATION)
					&& (getByUUID(o.getUuid()) != null)) {
				o = persist(o);
			} else {
				throw e;
			}
		}
		return o;
	}

	public List<Documento> getDocumentosNaoSuficientementeAssinados(DocumentoBin documentoBin) {
		return getDao().getDocumentosNaoSuficientementeAssinados(documentoBin);
	}

	private String getUrlValidacaoDocumento() {
		return this.pathResolver.getUrlProject() + "/validaDoc.seam";
	}

	public String getUrlValidacaoDocumento(final DocumentoBin documento) {
		return getUrlValidacaoDocumento() + "?cod=" + documento.getUuid();
	}
	
	public Boolean isDocumentoBinAssinadoPorPapel(DocumentoBin bin, Papel papel) {
	    return getDao().isDocumentoBinAssinadoPorPapel(bin, papel);
	}
}
