package br.com.infox.epp.processo.documento.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.type.PosicaoTextoAssinaturaDocumentoEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Stateless
@AutoCreate
@Name(DocumentoBinManager.NAME)
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

	private static final long serialVersionUID = 1L;
	private static final String TEXTO_AUTENTICIDADE_DOCUMENTO = "A autenticidade do documento pode ser conferida neste link: ";
	public static final String NAME = "documentoBinManager";

	@In
	private PathResolver pathResolver;
	@Inject
	private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
	@Inject
	private DocumentoBinarioManager documentoBinarioManager;
	@Inject
	private DocumentoDAO documentoDAO;

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
		bin.setDataInclusao(new Date());
		bin = persist(bin);
		if (bin.isBinario() && dados != null) {
			documentoBinarioManager.salvarBinario(bin.getId(), dados);
		}
		return bin;
	}

	public DocumentoBin createProcessoDocumentoBin(DocumentoBin bin, InputStream inputStream) throws DAOException, IOException {
	    byte[] dados = IOUtils.toByteArray(inputStream);
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
        bin.setDataInclusao(new Date());
        bin = persist(bin);
        if (bin.isBinario() && dados != null) {
            DocumentoBinario documentoBinario = documentoBinarioManager.salvarBinario(bin.getId(), dados);
            documentoBinarioManager.detach(documentoBinario);
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
        return bin;
	}

	public DocumentoBin getByUUID(final UUID uuid) {
		return getDao().getByUUID(uuid);
	}

	public static class MargemPdfException extends BusinessException {

		private static final long serialVersionUID = 1L;

		public MargemPdfException(String mensagem) {
			super(mensagem);
		}

		public MargemPdfException(String mensagem, Throwable e) {
			super(mensagem, e);
		}

	}

    public byte[] writeMargemDocumento(byte[] pdf, String textoAssinatura, UUID uuid, final byte[] qrcode, PosicaoTextoAssinaturaDocumentoEnum posicaoAssinatura) {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            writeMargemDocumento(pdf, textoAssinatura, uuid, qrcode, outStream, posicaoAssinatura);
            return outStream.toByteArray();
        } catch (IOException e) {
        	throw new MargemPdfException("Erro ao gravar a margem do PDF", e);
        }
    }

	public void writeMargemDocumento(byte[] pdf, String textoAssinatura, UUID uuid, final byte[] qrcode,
			OutputStream outStream, PosicaoTextoAssinaturaDocumentoEnum posicaoAssinatura) {
    	if(InfoxPdfReader.isCriptografado(pdf)) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar");
    	}
        try {
        	final PdfReader pdfReader = new PdfReader(pdf);
        	final PdfStamper stamper = new PdfStamper(pdfReader, outStream);
        	final Font font = new Font(Font.TIMES_ROMAN, 8);

        	final Phrase phrase = new Paragraph(textoAssinatura, font);

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
        		
        		
        		if(PosicaoTextoAssinaturaDocumentoEnum.RODAPE_HORIZONTAL.equals(posicaoAssinatura)) {
        			image.setAbsolutePosition(0, 0);
                    content.addImage(image);
                    ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, phrase, 52, 37, 0);
                    ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, new Phrase(TEXTO_AUTENTICIDADE_DOCUMENTO, font), 52, 25, 0);
                    ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, new Phrase(getTextoCodigoSomente(uuid), font), 52, 12, 0);
        		} else {
        			Phrase codPhrase = new Phrase(getTextoCodigo(uuid), font);
					image.setAbsolutePosition(right - 65, top - 70);
                    content.addImage(image);
                    ColumnText.showTextAligned(content, Element.ALIGN_LEFT, phrase, right - 25, top - 70, -90);
                    ColumnText.showTextAligned(content, Element.ALIGN_LEFT, codPhrase, right - 35, top - 70, -90);
        		}
        	}

        	stamper.close();
        	outStream.flush();
        } catch (BadPasswordException e) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar", e);
        } catch (IOException | DocumentException e) {
            throw new MargemPdfException("Erro ao gravar a margem do PDF", e);
        }

	}

    public void writeMargemDocumento(final DocumentoBin documento, final byte[] pdf, final OutputStream outStream) {
        try {
            outStream.write(writeMargemDocumento(pdf, getTextoAssinatura(documento), documento.getUuid(), getQrCodeSignatureImage(documento), documentoDAO.getPosicaoTextoAssinaturaDocumento(documento)));
            outStream.flush();
        } catch (IOException e) {
            throw new BusinessException("Erro ao gravar a margem do PDF", e);
        }
    }

        public byte[] getQrCodeSignatureImage(final DocumentoBin documento){
            return QRCode.from(getUrlValidacaoDocumento() + "?cod=" + documento.getUuid().toString())
                    .to(ImageType.GIF).withSize(60, 60).stream().toByteArray();
        }

	public String getTextoCodigo(final UUID uuid) {
        final StringBuilder sb = new StringBuilder(TEXTO_AUTENTICIDADE_DOCUMENTO);
		sb.append(getTextoCodigoSomente(uuid));
        return sb.toString();
    }
	
	public String getTextoCodigoSomente(final UUID uuid) {
        final StringBuilder sb = new StringBuilder();
		sb.append(getUrlValidacaoDocumento());
		sb.append("?cod=");
		sb.append(uuid);
        return sb.toString();
    }

    public String getTextoAssinatura(final DocumentoBin documento) {
        final StringBuilder assinadores = new StringBuilder();
        for (int i = 0; i < documento.getAssinaturas().size(); i++) {
            if (i != 0) {
                assinadores.append(", ");
            }
            AssinaturaDocumento assinatura = documento.getAssinaturas().get(i);
            assinadores.append(
                String.format("%s, %s, às %3$tH:%3$tM",
                    assinatura.getNomeUsuario(),
                    assinatura.getPapel(),
                    assinatura.getDataAssinatura()
                )
            );
        }

        return Parametros.TEXTO_RODAPE_DOCUMENTO.getValue()
            .replaceAll("\\$\\{assinadores\\}", assinadores.toString());
    }

	@Override
	public DocumentoBin persist(DocumentoBin documentoBin) throws DAOException {
		if (documentoBin.isBinario()) {
            documentoBin.setMinuta(false);
        }
		documentoBin = super.persist(documentoBin);
		List<Documento> documentoList = documentoDAO.getDocumentosFromDocumentoBin(documentoBin);
		if (!documentoBin.getSuficientementeAssinado() && !documentoList.isEmpty()) {
			if (!classificacaoDocumentoPapelManager.classificacaoExigeAssinatura(documentoList.get(0).getClassificacaoDocumento()) && !documentoBin.isMinuta()) {
				this.setDocumentoSuficientementeAssinado(documentoBin);
			}
		}
		return documentoBin;
	}


	public void setDocumentoSuficientementeAssinado(DocumentoBin documentoBin) throws DAOException {
		documentoBin.setSuficientementeAssinado(Boolean.TRUE);
		documentoBin.setDataSuficientementeAssinado(new Date());
		List<RegistroAssinaturaSuficiente> registrosAssinaturaSuficiente = documentoBin.getRegistrosAssinaturaSuficiente();
		List<Documento> documentoList = documentoDAO.getDocumentosFromDocumentoBin(documentoBin);
		GenericManager genericManager = ComponentUtil.getComponent(GenericManager.NAME);
        if (documentoList != null && !documentoList.isEmpty()) {
            Documento documento = documentoList.get(0);
            for (ClassificacaoDocumentoPapel classificacaoDocumentoPapel : documento.getClassificacaoDocumento().getClassificacaoDocumentoPapelList()) {
                RegistroAssinaturaSuficiente registroAssinaturaSuficiente = new RegistroAssinaturaSuficiente();
                registroAssinaturaSuficiente.setDocumentoBin(documentoBin);
                registroAssinaturaSuficiente.setPapel(classificacaoDocumentoPapel.getPapel().getIdentificador());
                registroAssinaturaSuficiente.setTipoAssinatura(classificacaoDocumentoPapel.getTipoAssinatura());
                registrosAssinaturaSuficiente.add(registroAssinaturaSuficiente);
                genericManager.persist(registroAssinaturaSuficiente);
            }
        }
        update(documentoBin);
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
