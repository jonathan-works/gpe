package br.com.infox.epp.assinaturaeletronica;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.documento.type.LocalizacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.documento.type.OrientacaoAssinaturaEletronicaDocumentoEnum;
import br.com.infox.epp.pessoaFisica.PessoaFisicaSearch;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager.MargemPdfException;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AssinaturaEletronicaService extends PersistenceController {

    @Inject
    private AssinaturaEletronicaSearch assinaturaEletronicaSearch;

    @Inject
    private AssinaturaEletronicaBinSearch assinaturaEletronicaBinSearch;

    @Inject
    @GenericDao
    private Dao<AssinaturaEletronica, Long> assinaturaEletronicaDao;

    @Inject
    private PessoaFisicaSearch pessoaFisicaSearch;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AssinaturaEletronicaDTO salvar(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {

        assinaturaEletronicaDTO.setDataInclusao(new Date());
        AssinaturaEletronica assinaturaEletronica = fromDTO(assinaturaEletronicaDTO);

        try {
            assinaturaEletronica = assinaturaEletronicaDao.persist(assinaturaEletronica);
            assinaturaEletronicaDTO.setId(assinaturaEletronica.getId());

            AssinaturaEletronicaBin assinaturaEletronicaBin = new AssinaturaEletronicaBin();
            assinaturaEletronicaBin.setId(assinaturaEletronicaDTO.getId());
            assinaturaEletronicaBin.setImagem(assinaturaEletronicaDTO.getImagem());
            getEntityManagerBin().persist(assinaturaEletronicaBin);
            getEntityManagerBin().flush();

        } catch (DAOException e) {
            throw new BusinessRollbackException(InfoxMessages.getInstance().get("assinaturaEletronica.salvar.erro"), e);
        }

        return assinaturaEletronicaDTO;
    }

    private AssinaturaEletronica fromDTO(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {
        if(assinaturaEletronicaDTO.getDataInclusao() == null) {
            assinaturaEletronicaDTO.setDataInclusao(new Date());
        }
        AssinaturaEletronica assinaturaEletronica = new AssinaturaEletronica();
        assinaturaEletronica.setNomeArquivo(assinaturaEletronicaDTO.getNomeArquivo());
        assinaturaEletronica.setUuid(assinaturaEletronicaDTO.getUuid());
        assinaturaEletronica.setContentType(assinaturaEletronicaDTO.getContentType());
        assinaturaEletronica.setExtensao(assinaturaEletronicaDTO.getExtensao());
        if(assinaturaEletronicaDTO.getIdPessoa() != null) {
            assinaturaEletronica.setPessoaFisica(pessoaFisicaSearch.find(assinaturaEletronicaDTO.getIdPessoa()));
        }
        assinaturaEletronica.setDataInclusao(assinaturaEletronicaDTO.getDataInclusao());
        return assinaturaEletronica;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(AssinaturaEletronicaDTO assinaturaEletronicaDTO) {
        AssinaturaEletronica assinaturaEletronica = assinaturaEletronicaSearch.getAssinaturaEletronicaByIdPessoaFisica(assinaturaEletronicaDTO.getIdPessoa());
        remove(assinaturaEletronica);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(AssinaturaEletronica assinaturaEletronica) {
        if(assinaturaEletronica != null) {
            AssinaturaEletronicaBin assinaturaEletronicaBin = assinaturaEletronicaBinSearch.getAssinaturaEletronicaBinById(assinaturaEletronica.getId());
            if(assinaturaEletronicaBin != null) {
                getEntityManagerBin().remove(assinaturaEletronicaBin);
            }
            assinaturaEletronicaDao.remove(assinaturaEletronica);
        }
    }

    public void writeImagemAssinaturaEletronica(byte[] pdf, OutputStream outStream, DocumentoBin documentoBin) {
        AssinaturaEletronicaProvider assinaturaEletronicaProvider = new AssinaturaEletronicaProvider(documentoBin);
        writeImagemAssinaturaEletronica(pdf, outStream, assinaturaEletronicaProvider);
    }

    public void writeImagemAssinaturaEletronica(byte[] pdf, OutputStream outStream,
            AssinaturaEletronicaProvider assinaturaEletronicaProvider) {
        writeImagemAssinaturaEletronica(pdf, outStream, assinaturaEletronicaProvider.getImagensAssinatura(),
                assinaturaEletronicaProvider.getLocalizacaoAssinatura(),
                assinaturaEletronicaProvider.getOrientacaoAssinatura(), assinaturaEletronicaProvider.getPaginaUnica());
    }

    public void writeImagemAssinaturaEletronica(byte[] pdf, OutputStream outStream, List<byte[]> listImagem,
            LocalizacaoAssinaturaEletronicaDocumentoEnum localizacaoAssinatura,
            OrientacaoAssinaturaEletronicaDocumentoEnum orientacaoAssinatura, int pagina) {
        if(InfoxPdfReader.isCriptografado(pdf)) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar");
        }

        try {
            final PdfReader pdfReader = new PdfReader(pdf);
            final PdfStamper stamper = new PdfStamper(pdfReader, outStream);

            if(LocalizacaoAssinaturaEletronicaDocumentoEnum.TODAS_PAGINAS.equals(localizacaoAssinatura)) {
                for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
                    writeImagemEletronicaPagina(listImagem, orientacaoAssinatura, pdfReader, stamper, page);
                }
            } else {
                int paginaAplicacao;
                switch (localizacaoAssinatura) {
                    case PAGINA_UNICA:
                        paginaAplicacao = pagina;
                        if(paginaAplicacao > pdfReader.getNumberOfPages()) {
                            paginaAplicacao = pdfReader.getNumberOfPages();
                        }
                        break;
                    case PRIMEIRA_PAGINA:
                        paginaAplicacao = 1;
                        break;
                    case ULTIMA_PAGINA:
                    default:
                        paginaAplicacao = pdfReader.getNumberOfPages();
                        break;
                }
                writeImagemEletronicaPagina(listImagem, orientacaoAssinatura, pdfReader, stamper, paginaAplicacao);
            }

            stamper.close();
            outStream.flush();
        } catch (BadPasswordException e) {
            throw new MargemPdfException("Documento somente leitura, não é possível gravar", e);
        } catch (IOException | DocumentException e) {
            throw new MargemPdfException("Erro ao gravar a assinatura eletrônica do PDF", e);
        }
    }

    private void writeImagemEletronicaPagina(List<byte[]> listImagem,
            OrientacaoAssinaturaEletronicaDocumentoEnum orientacaoAssinatura, final PdfReader pdfReader,
            final PdfStamper stamper, int page)
            throws IOException, DocumentException {
        int rotation = pdfReader.getPageRotation(page);
        final PdfContentByte content = stamper.getOverContent(page);
        float right = pdfReader.getCropBox(page).getRight();
        float top = pdfReader.getCropBox(page).getTop();
        if (rotation == 90 || rotation == 270) {
            // Invertendo posições quando o PDF estiver em modo Paisagem
            float tempRight = right;
            right = top;
            top = tempRight;
        }

        if(OrientacaoAssinaturaEletronicaDocumentoEnum.RODAPE_HORIZONTAL.equals(orientacaoAssinatura)) {
            float absoluteX = 5;
            for (byte[] imagem : listImagem) {
                Image image = Image.getInstance(imagem);
                image.setAbsolutePosition(absoluteX, 2);
                image.scalePercent(55);
                content.addImage(image);
                absoluteX += 155;
            }
        } else {
            float absoluteX = right+40;
            float absoluteY = top+40;
            for (byte[] imagem : listImagem) {
                Image image = Image.getInstance(imagem);
                image.scalePercent(55);
                image.setAbsolutePosition(absoluteX - image.getWidth(),  absoluteY - image.getHeight());
                content.addImage(image);
                absoluteY -= 90;
            }
        }
    }

}
