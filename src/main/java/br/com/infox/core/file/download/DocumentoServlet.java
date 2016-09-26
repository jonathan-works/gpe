package br.com.infox.core.file.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.ObjectUtils;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.lowagie.text.DocumentException;

import br.com.infox.core.pdf.PdfManager;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.documento.DocumentoBinSearch;
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
        if (downloadDocumentoInfo == null) {
            writeNotFoundResponse(resp);
            return;
        }
        UUID uuid = UUID.fromString(downloadDocumentoInfo.getUid());
        DocumentoBin documento = ObjectUtils.firstNonNull(
            documentoBinSearch.getTermoAdesaoByUUID(uuid),
            documentoBinSearch.getDocumentoPublicoByUUID(uuid),
            documentoBinSearch.getDocumentoByUsuarioPerfilUUID(uuid, getUsuarioPerfil(req))
        );
        
        if (documento == null) {
            writeNotFoundResponse(resp);
            return;
        }

        if (downloadDocumentoInfo.isFileNameEmpty()) {
            resp.sendRedirect(buildPathWithFilename(req, documento));
            return;
        }

        writeDocumentoBinToResponse(resp, documento);
    }
    
    private UsuarioPerfil getUsuarioPerfil(HttpServletRequest req) throws ServletException, IOException {
        /**
         * Necessário enquanto a sessão for controlada pelo seam
         */
        final UsuarioPerfilWrapper usuarioPerfilHolder = new UsuarioPerfilWrapper();
        new ContextualHttpServletRequest(req){
            @Override
            public void process() throws Exception {
                usuarioPerfilHolder.setUsuarioPerfil(Authenticator.getUsuarioPerfilAtual());
            }
        }.run();
        return usuarioPerfilHolder.getUsuarioPerfil();
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
                pdfManager.convertHtmlToPdf(documento.getModeloDocumento(), outputStream);
                documento.setExtensao("pdf");
            } catch (DocumentException e) {
            }
            data = outputStream.toByteArray();
        }
        return data;
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