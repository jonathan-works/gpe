package br.com.infox.epp.processo.documento.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.dao.DocumentoBinDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.path.PathResolver;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

@AutoCreate
@Name(DocumentoBinManager.NAME)
public class DocumentoBinManager extends Manager<DocumentoBinDAO, DocumentoBin> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "documentoBinManager";
    
    @In
    private PathResolver pathResolver;
    
    public DocumentoBin createProcessoDocumentoBin(
            Documento documento) throws DAOException {
        DocumentoBin bin = documento.getDocumentoBin();
        if (bin.getMd5Documento() == null) {
            bin.setMd5Documento(MD5Encoder.encode(documento.getDocumentoBin().getModeloDocumento()));
        }
        return persist(bin);
    }
    
    public DocumentoBin createProcessoDocumentoBin(final String tituloDocumento, final String conteudo) throws DAOException {
        DocumentoBin bin = new DocumentoBin();
        bin.setNomeArquivo(tituloDocumento);
        bin.setModeloDocumento(conteudo);
        bin.setMd5Documento(MD5Encoder.encode(conteudo));
        return persist(bin);
    }
    
    public DocumentoBin getByUUID(UUID uuid) {
        return getDao().getByUUID(uuid);
    }
    
    public void writeMargemDocumento(DocumentoBin documento, byte[] pdf, OutputStream outStream) {
        try {
            PdfReader pdfReader = new PdfReader(pdf);
            PdfStamper stamper = new PdfStamper(pdfReader, outStream);
            Font font = new Font(Font.TIMES_ROMAN, 8);
            
            Phrase phrase = createPhraseAssinatura(documento, font);
            Phrase codPhrase = createPhraseCodigo(font, documento.getUuid().toString());
            
            byte[] qrcode = QRCode.from(getUrlValidacaoDocumento() + "?cod=" + documento.getUuid().toString()).to(ImageType.GIF).withSize(60, 60).stream().toByteArray();
            
            for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
                PdfContentByte content = stamper.getOverContent(page);
                Image image = Image.getInstance(qrcode);
                image.setAbsolutePosition(pdfReader.getCropBox(page).getRight() - 60, pdfReader.getCropBox(page).getTop() - 70);
                content.addImage(image);
                ColumnText.showTextAligned(content, Element.ALIGN_LEFT, phrase, pdfReader.getCropBox(page).getRight() - 15, pdfReader.getCropBox(page).getTop() - 70, -90);
                ColumnText.showTextAligned(content, Element.ALIGN_LEFT, codPhrase, pdfReader.getCropBox(page).getRight() - 25, pdfReader.getCropBox(page).getTop() - 70, -90);
            }
            
            stamper.close();
            outStream.flush();
        } catch (IOException | DocumentException e) {
            throw new BusinessException("Erro ao gravar a margem do PDF", e);
        }
    }
    
    private Phrase createPhraseCodigo(Font font, String uuid) {
        StringBuilder sb = new StringBuilder("Acesse em: ");
        sb.append(getUrlValidacaoDocumento());
        sb.append(" Código do documento: ");
        sb.append(uuid);        
        Phrase codPhrase = new Phrase(sb.toString(), font);
        return codPhrase;
    }

    private Phrase createPhraseAssinatura(DocumentoBin documento, Font font) {
        StringBuilder sb = new StringBuilder("Documento Assinado Digitalmente por: ");
        for (AssinaturaDocumento assinatura : documento.getAssinaturas()) {
            sb.append(assinatura.getNomeUsuario());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        Phrase phrase = new Phrase(sb.toString(), font);
        return phrase;
    }

    @Override
    public DocumentoBin persist(DocumentoBin o) throws DAOException {
    	try {
            o.setUuid(UUID.randomUUID());
        	o = super.persist(o);
        } catch (DAOException e) {
            GenericDatabaseErrorCode error = e.getDatabaseErrorCode();
            if (error != null && error == GenericDatabaseErrorCode.UNIQUE_VIOLATION && getByUUID(o.getUuid()) != null) {
                o = persist(o);
            } else {
                throw e;
            }
        }
    	return o;
    }
    
    private String getUrlValidacaoDocumento() {
        return pathResolver.getUrlProject() + "/validaDoc.seam";
    }
}
