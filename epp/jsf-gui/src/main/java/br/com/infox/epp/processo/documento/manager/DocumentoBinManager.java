package br.com.infox.epp.processo.documento.manager;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.manager.ProcessoManager;
import com.lowagie.text.pdf.*;
import org.apache.commons.io.IOUtils;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.exceptions.BadPasswordException;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.assinaturaeletronica.AssinaturaEletronicaSearch;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;
import br.com.infox.epp.documento.entity.DocumentoBinario;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.modelo.ModeloDocumentoSearch;
import br.com.infox.epp.documento.type.ExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.PosicaoTextoAssinaturaDocumentoEnum;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.entity.RegistroAssinaturaSuficiente;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.download.Carimbador;
import br.com.infox.epp.processo.documento.download.Carimbo;
import br.com.infox.epp.processo.documento.download.CarimboCancelamento;
import br.com.infox.epp.processo.documento.download.CarimboQrCode;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.task.home.VariableTypeResolver;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Stateless
@AutoCreate
@Name(DocumentoBinManager.NAME)
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

	private static final long serialVersionUID = 1L;
	private static final String TEXTO_DOCUMENTO_CANCELADO = "Documento cancelado.";
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
	@Inject
    private PdfManager pdfManager;
	@Inject
    private InfoxMessages infoxMessages;
	@Inject
	private DAO<Processo> processoDao;
	@Inject
	private ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private ModeloDocumentoSearch modeloDocumentoSearch;
	@Inject
	private AssinaturaEletronicaSearch assinaturaEletronicaSearch;

	public void remove(List<Integer> listaDocBin) {
	    if(listaDocBin == null || listaDocBin.isEmpty()) {
	        return;
	    }
	    getDao()
	        .getEntityManager()
	        .createQuery("delete from DocumentoBin where id in (:lista)")
	        .setParameter("lista", listaDocBin)
	        .executeUpdate();
	    documentoBinarioManager.remove(listaDocBin);
	}

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

	public byte[] writeCancelamentoDocumento(byte[] pdf) {
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
			writeCancelamentoDocumento(pdf, outStream);
			return outStream.toByteArray();
		} catch (IOException e) {
        	throw new MargemPdfException("Erro ao gravar o cancelamento do PDF", e);
        }
	}

    public byte[] writeMargemDocumento(byte[] pdf, String textoAssinatura, UUID uuid, final byte[] qrcode, PosicaoTextoAssinaturaDocumentoEnum posicaoAssinatura, boolean cancelado) {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            writeMargemDocumento(pdf, textoAssinatura, uuid, qrcode, outStream, posicaoAssinatura, cancelado);
            return outStream.toByteArray();
        } catch (IOException e) {
        	throw new MargemPdfException("Erro ao gravar a margem do PDF", e);
        }
    }

    public DocumentoBin createDocumentoBinResumoDocumentosProcesso(Processo processo) {
    	try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
	    	Document document = new Document();
	    	PdfSmartCopy copy = new PdfSmartCopy(document, stream);
	    	document.open();
	    	documentoToPdfCopy(copy, getModeloDocumentoToByteArray(modeloDocumentoSearch.getModeloDocumentoByCodigo(Parametros.CD_MODELO_DOCUMENTO_FOLHA_ROSTO_RESUMO_PROCESSO.getValue()), processo));
	    	documentoToPdfCopy(copy, getModeloDocumentoToByteArray(modeloDocumentoSearch.getModeloDocumentoByCodigo(Parametros.CD_MODELO_DOCUMENTO_FOLHA_TRAMITACAO_RESUMO_PROCESSO.getValue()), processo));
    		for(Documento documento : getListAllDocumentoByProcessoOrderData(processo)) {
    			try {
	    			appendDocumento(copy, documento);
    			} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    	document.close();
	    	DocumentoBin bin = createProcessoDocumentoBin("Documento do processo " + processo.getNumeroProcesso(), stream.toByteArray(), "pdf");
	    	bin = createProcessoDocumentoBin(bin);
	    	atualizarDocumentoBinResumoProcesso(processo, bin);
	    	return bin;
    	} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
    }

	private void appendDocumento(PdfSmartCopy copy, Documento documento) throws IOException, BadPdfFormatException {
		boolean podeExibirMargem = podeExibirMargem(documento.getDocumentoBin());
		boolean documentoExcluido = documento.getExcluido();
		boolean isPDF = "pdf".equalsIgnoreCase(documento.getDocumentoBin().getExtensao());
		boolean documentoSemCarimbos = !(podeExibirMargem || isPDF && documentoExcluido);
		if (documentoSemCarimbos) {
			documentoImageToPdfCopy(copy, (getOriginalData(documento.getDocumentoBin())));
		} else {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
				List<Carimbo> carimbos = gerarCarimbos(documento.getDocumentoBin(), podeExibirMargem, documentoExcluido);
				writeCarimbos(getOriginalData(documento.getDocumentoBin()), baos, carimbos);
				baos.flush();
				documentoToPdfCopy(copy, baos.toByteArray());
			}
		}
	}

    private boolean podeExibirMargem(DocumentoBin documento) {
        return "pdf".equalsIgnoreCase(documento.getExtensao())
                && (Boolean.TRUE.equals(documento.getSuficientementeAssinado())
                        || documento.getAssinaturas() != null && !documento.getAssinaturas().isEmpty());
    }

    private List<Documento> getListAllDocumentoByProcessoOrderData(Processo processo) {
    	EntityManager entityManager = Beans.getReference(EntityManager.class);
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Documento> query = cb.createQuery(Documento.class);
    	Root<Documento> doc = query.from(Documento.class);
    	Join<Documento, Pasta> pasta = doc.join(Documento_.pasta, JoinType.INNER);
    	query.where(cb.equal(pasta.get(Pasta_.processo), processo));
    	query.orderBy(cb.asc(doc.get(Documento_.dataInclusao)));
    	return entityManager.createQuery(query).getResultList();
    }

    private byte[] getModeloDocumentoToByteArray(ModeloDocumento modeloDocumento, Processo processo)
            throws DocumentException {
        String documentoAvaliado = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, createExpressionResolver(processo));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfManager.convertHtmlToPdf(documentoAvaliado, outputStream);
        return outputStream.toByteArray();
    }

    private void documentoToPdfCopy(PdfSmartCopy copy, byte[] documento) throws IOException, BadPdfFormatException {
    	PdfReader reader = new PdfReader(documento);
		for(int i = 1; i < reader.getNumberOfPages() + 1; i++) {
			copy.addPage(copy.getImportedPage(reader, i));
		}
		copy.freeReader(reader);
	    reader.close();
    }

    private void documentoImageToPdfCopy(PdfSmartCopy copy, byte[] documento) throws IOException, BadPdfFormatException {
    	Document imageDocument = new Document();
    	try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
    		PdfWriter imageDocumentWriter = PdfWriter.getInstance(imageDocument, stream);
            imageDocument.open();
            if (imageDocument.newPage()) {

                Image image = Image.getInstance(documento);

                float scaler = ((imageDocument.getPageSize().getWidth() - imageDocument.leftMargin()
                        - imageDocument.rightMargin()) / image.getWidth()) * 100;

                image.scalePercent(scaler);

                imageDocument.add(image);
                imageDocument.close();
                imageDocumentWriter.close();

                PdfReader reader = new PdfReader(stream.toByteArray());

                copy.addPage(copy.getImportedPage(reader, 1));

                copy.freeReader(reader);
                reader.close();
         }
    	} catch (DocumentException e) {
			e.printStackTrace();
		}
    }

    public byte[] getOriginalData(DocumentoBin documento) {
        if (documentoBinarioManager.existeBinario(documento.getId())) {
        	byte[] data = documentoBinarioManager.getData(documento.getId());
        	documentoBinarioManager.detach(documento.getId());
            return data;
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                String modeloDocumento = documento.isBinario() ? getMensagemDocumentoNulo()
                        : defaultIfNull(documento.getModeloDocumento(), getMensagemDocumentoNulo());
                pdfManager.convertHtmlToPdf(modeloDocumento, outputStream);
                documento.setExtensao("pdf");
            } catch (DocumentException e) {
            }
            return outputStream.toByteArray();
        }
	}

    public String getMensagemDocumentoNulo() {
        return infoxMessages.get("documentoProcesso.error.noFileOrDeleted");
    }

	public List<Carimbo> gerarCarimbos(DocumentoBin documento, boolean gerarMargens, boolean documentoCancelado) {
		List<Carimbo> carimbos = new ArrayList<>();
        if (gerarMargens && podeExibirMargem(documento)) {
        	String textoAssinatura = getTextoAssinatura(documento);
        	int posicao = Carimbo.RODAPE;
        	if (PosicaoTextoAssinaturaDocumentoEnum.LATERAL_VERTICAL.equals(documentoDAO.getPosicaoTextoAssinaturaDocumento(documento))) {
    			posicao = Carimbo.LATERAL;
        	}
        	UUID uuidDocumento = documento.getUuid();
        	String urlValidacaoDocumento = getUrlValidacaoDocumento(documento);
            CarimboQrCode carimboQrCode = new CarimboQrCode(urlValidacaoDocumento, uuidDocumento, textoAssinatura, documentoCancelado, posicao);
            carimbos.addAll(assinaturaEletronicaSearch.criarCarimbosAssinaturaEletronica(documento));
            carimbos.add(carimboQrCode);
        }
        if(documentoCancelado) {
        	CarimboCancelamento carimboCancelamento = new CarimboCancelamento("CANCELADO");
        	carimbos.add(carimboCancelamento);
        }
		return carimbos;
	}

	public void writeCarimbos(byte[] pdf, OutputStream outStream, List<Carimbo> carimbos) {
		if(InfoxPdfReader.isCriptografado(pdf)) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar");
    	}
        try {
        	final PdfReader pdfReader = new PdfReader(pdf);
        	final PdfStamper stamper = new PdfStamper(pdfReader, outStream);
	        Carimbador carimbador = new Carimbador(carimbos);
	        carimbador.aplicar(pdfReader, stamper);
	        stamper.close();
        	outStream.flush();
        } catch (BadPasswordException e) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar", e);
        } catch (IOException | DocumentException e) {
            throw new MargemPdfException("Erro ao gravar o cancelamento do PDF", e);
        }
	}

    public void writeCancelamentoDocumento(byte[] pdf, OutputStream outStream) {
    	if(InfoxPdfReader.isCriptografado(pdf)) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar");
    	}
        try {
        	final PdfReader pdfReader = new PdfReader(pdf);
        	final PdfStamper stamper = new PdfStamper(pdfReader, outStream);

        	//Variáveis para a parte de cancelamento
			Font f = new Font(Font.TIMES_ROMAN, 80);
			f.setColor(Color.RED);
	        Phrase p = new Phrase("CANCELADO", f);
	        PdfGState gs1 = new PdfGState();
	        gs1.setFillOpacity(0.5f);
	        PdfContentByte over;
	        Rectangle pagesize;
	        float x, y;

	        for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
    	        pagesize = pdfReader.getPageSizeWithRotation(page);
                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                y = (pagesize.getTop() + pagesize.getBottom()) / 1.75F;
                over = stamper.getOverContent(page);
                over.saveState();
                over.setGState(gs1);
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 45);
                over.restoreState();
	        }

	        stamper.close();
        	outStream.flush();
        } catch (BadPasswordException e) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar", e);
        } catch (IOException | DocumentException e) {
            throw new MargemPdfException("Erro ao gravar o cancelamento do PDF", e);
        }
    }

	public void writeMargemDocumento(byte[] pdf, String textoAssinatura, UUID uuid, final byte[] qrcode,
			OutputStream outStream, PosicaoTextoAssinaturaDocumentoEnum posicaoAssinatura, boolean cancelado) {
    	if(InfoxPdfReader.isCriptografado(pdf)) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar");
    	}
        try {
        	final PdfReader pdfReader = new PdfReader(pdf);
        	final PdfStamper stamper = new PdfStamper(pdfReader, outStream);
        	final Font font = new Font(Font.TIMES_ROMAN, 8);

        	Phrase phrase;
			if (!textoAssinatura.isEmpty()) {
				phrase = new Paragraph(textoAssinatura, font);
			} else {
				phrase = null;
			}

			Phrase codPhrase;

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
        			codPhrase = new Phrase(getTextoCodigoSomente(uuid), font);
        			image.setAbsolutePosition(0, 0);
                    content.addImage(image);
                    if (phrase != null) {
                    	ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, phrase, 52, 37, 0);
					}
                    if(cancelado) {
                    	ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, new Phrase(TEXTO_DOCUMENTO_CANCELADO + " " + TEXTO_AUTENTICIDADE_DOCUMENTO, font), 52, 25, 0);
                    } else {
                    	ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, new Phrase(TEXTO_AUTENTICIDADE_DOCUMENTO, font), 52, 25, 0);
                    }
                    ColumnText.showTextAligned(content, Element.ALIGN_BOTTOM, new Phrase(codPhrase), 52, 12, 0);
        		} else {
        			codPhrase = new Phrase(getTextoCodigo(uuid, cancelado), font);
					image.setAbsolutePosition(right - 65, top - 70);
                    content.addImage(image);
                    if (phrase != null) {
                    	ColumnText.showTextAligned(content, Element.ALIGN_LEFT, phrase, right - 25, top - 70, -90);
                    }
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
            outStream.write(writeMargemDocumento(pdf, getTextoAssinatura(documento), documento.getUuid(), getQrCodeSignatureImage(documento), documentoDAO.getPosicaoTextoAssinaturaDocumento(documento), documentoDAO.getDocumentosFromDocumentoBin(documento).get(0).getExcluido()));
            outStream.flush();
        } catch (IOException e) {
            throw new BusinessException("Erro ao gravar a margem do PDF", e);
        }
    }

        public byte[] getQrCodeSignatureImage(final DocumentoBin documento){
            return QRCode.from(getUrlValidacaoDocumento() + "?cod=" + documento.getUuid().toString())
                    .to(ImageType.GIF).withSize(60, 60).stream().toByteArray();
        }

	public String getTextoCodigo(final UUID uuid, boolean cancelado) {
        final StringBuilder sb = new StringBuilder();
        if(cancelado) {
        	sb.append(TEXTO_DOCUMENTO_CANCELADO);
        	sb.append(" ");
        }

        sb.append(TEXTO_AUTENTICIDADE_DOCUMENTO);
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

        if(assinadores.length() > 0) {
	        Documento doc = documento.getDocumentoList().isEmpty()? null : documento.getDocumentoList().get(0);
	        if(doc != null && doc.getPasta().getProcesso() != null) {
	        	assinadores.append(", Nº do processo ").append(doc.getPasta().getProcesso().getNumeroProcesso());
	        }

	        return Parametros.TEXTO_RODAPE_DOCUMENTO.getValue()
	            .replaceAll("\\$\\{assinadores\\}", assinadores.toString());
        } else {
        	return "";
        }

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

	public void atualizarDocumentoBinResumoProcesso(Processo processo, DocumentoBin documentoBinResumoProcesso) {

		if(processo.getDocumentoBinResumoProcesso() != null) {
			processoDao.refresh(processo);
			DocumentoBin documentoResumoAntigo = processo.getDocumentoBinResumoProcesso();
			processo.setDocumentoBinResumoProcesso(null);
			remove(documentoResumoAntigo);
		}
		processo.setDocumentoBinResumoProcesso(documentoBinResumoProcesso);

		processoDao.update(processo);

	}

	private ExpressionResolver createExpressionResolver(Processo processo) {
		if (processo == null) {
			throw new BusinessRollbackException("Não existe processo para essa solicitação");
		} else {
			VariableTypeResolver variableTypeResolver = ComponentUtil.getComponent(VariableTypeResolver.NAME);
			EntityManager entityManager = Beans.getReference(EntityManager.class);
			ProcessInstance processInstance = entityManager.find(ProcessInstance.class, processo.getIdJbpm());
			if (variableTypeResolver.getProcessInstance() == null) {
				variableTypeResolver.setProcessInstance(processInstance);
			}
			ExecutionContext executionContext = new ExecutionContext(processInstance.getRootToken());
			return ExpressionResolverChainBuilder.defaultExpressionResolverChain(processo.getIdProcesso(),
					executionContext);
		}
	}

    public void atualizarConteudoDocumentoBin(DocumentoBin documentoBin, String conteudo) {
        documentoBin.setModeloDocumento(conteudo);
        documentoBin.setMd5Documento(MD5Encoder.encode(conteudo));
        update(documentoBin);
    }
}
