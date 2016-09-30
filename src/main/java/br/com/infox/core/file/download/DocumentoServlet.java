package br.com.infox.core.file.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.ObjectUtils;

import com.lowagie.text.DocumentException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.documento.DocumentoBinSearch;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@WebServlet(urlPatterns = DocumentoServlet.BASE_SERVLET_PATH + "/*")
public class DocumentoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public static final String BASE_SERVLET_PATH = "/file";

    @Inject private DocumentoBinSearch documentoBinSearch;
    @Inject private DocumentoBinManager documentoBinManager;
    @Inject private DocumentoBinarioManager documentoBinarioManager;
    @Inject private PdfManager pdfManager;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DocumentoInfo downloadDocumentoInfo = extractFromRequest(req, DocumentoServletOperation.DOWNLOAD);
        DocumentoBin documento=null;
        if (downloadDocumentoInfo == null) {
            Object documentoDownload = req.getSession().getAttribute("documentoDownload");
            if (documentoDownload == null){
                writeNotFoundResponse(resp);
                return;
            }
            if (documentoDownload instanceof Documento){
                documento = ((Documento) documentoDownload).getDocumentoBin();
            } else if (documentoDownload instanceof DocumentoBin){
                documento = (DocumentoBin) documentoDownload;
            }
        }
        if (documento == null)
        documento = ObjectUtils.firstNonNull(
            documentoBinSearch.getTermoAdesaoByUUID(UUID.fromString(downloadDocumentoInfo.getUid())),
            documentoBinSearch.getDocumentoPublicoByUUID(UUID.fromString(downloadDocumentoInfo.getUid()))
        );
        
        if (documento == null) {
            writeNotFoundResponse(resp);
            return;
        }
        String suffix="";
        if (!documento.isBinario()){
            documento.setExtensao("pdf");
        }
        if (StringUtil.isEmpty(documento.getNomeArquivo())){
            documento.setNomeArquivo(BigInteger.probablePrime(32, new SecureRandom()).toString(Character.MAX_RADIX));
        }
        if (documento.getNomeArquivo().endsWith("."+documento.getExtensao()))
            suffix = documento.getNomeArquivo();
        else    
            suffix = documento.getNomeArquivo()+"."+documento.getExtensao();
        if (!URI.create(req.getRequestURI()).getPath().endsWith(suffix)) {
            resp.sendRedirect(buildPathWithFilename(req, documento));
            return;
        }
        req.getSession().removeAttribute("documentoDownload");
        writeDocumentoBinToResponse(resp, documento);
    }
    
    private void writeNotFoundResponse(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        resp.flushBuffer();
    }

    private void writeDocumentoBinToResponse(HttpServletResponse resp, DocumentoBin documento) throws IOException {
        byte[] data = getData(documento);
        String contentType = "application/" + documento.getExtensao();
        resp.setContentType(contentType);
        resp.setStatus(200);
        if ("application/pdf".equalsIgnoreCase(contentType)){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            documentoBinManager.writeMargemDocumento(documento, data, out);
            data = out.toByteArray();
        }
        resp.getOutputStream().write(data);
        resp.getOutputStream().flush();
    }

    private byte[] getData(DocumentoBin documento) {
        byte[] data = new byte[0];
        if (documentoBinarioManager.existeBinario(documento.getId())){
            data = documentoBinarioManager.getData(documento.getId());
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                String modeloDocumento = ObjectUtils.defaultIfNull(documento.getModeloDocumento(), getMensagemDocumentoNulo());
                pdfManager.convertHtmlToPdf(modeloDocumento, outputStream);
                documento.setExtensao("pdf");
            } catch (DocumentException e) {
            }
            data = outputStream.toByteArray();
        }
        return data;
    }

    private String getMensagemDocumentoNulo() {
        return "<div style=\"text-align:center;font-weight:bolder;\">"+InfoxMessages.getInstance().get("documentoProcesso.error.noFileOrDeleted")+"</div>";
    }

    private String buildPathWithFilename(HttpServletRequest req, DocumentoBin documento) {
        UriBuilder uriBuilder = UriBuilder.fromPath(req.getRequestURI());
        if (!documento.isBinario()){
            documento.setExtensao("pdf");
        }
        if (!documento.getNomeArquivo().endsWith("."+documento.getExtensao()))
            uriBuilder = uriBuilder.path(String.format("%s.%s", documento.getNomeArquivo(), documento.getExtensao()));
        else 
            uriBuilder = uriBuilder.path(documento.getNomeArquivo());
        return uriBuilder.build().toString();
    }

    private DocumentoInfo extractFromRequest(HttpServletRequest req, DocumentoServletOperation action) {
        String uriPath = req.getRequestURI();
        try {
            URI uri = new URI(uriPath);
            uriPath = uri.getPath();
        } catch (URISyntaxException e) {
        }

        String basePath = req.getContextPath() + BASE_SERVLET_PATH + "/";
        if (!uriPath.startsWith(basePath)) {
            return null;
        }
        uriPath = uriPath.substring(basePath.length());
        int indexOfActionPath = uriPath.indexOf(action.getPath());
        if (indexOfActionPath < 0) {
            return null;
        }
        String uid = uriPath.substring(0, indexOfActionPath);

        uriPath = uriPath.substring(indexOfActionPath + action.getPath().length());
        if (uriPath.length() > 0 && uriPath.charAt(0) == '/')
            uriPath = uriPath.substring(0);

        String filename = new File(uriPath).getName();

        return new DocumentoInfo(uid, filename);
    }

}