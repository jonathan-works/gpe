package br.com.infox.core.file.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import br.com.infox.epp.documento.DocumentoBinSearch;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;

@WebServlet(urlPatterns = DocumentoServlet.BASE_SERVLET_PATH + "/*")
public class DocumentoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public static final String BASE_SERVLET_PATH = "/file";

    @Inject private DocumentoBinSearch documentoBinSearch;
    @Inject private DocumentoBinarioManager documentoBinarioManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DocumentoInfo downloadDocumentoInfo = extractFromRequest(req, DocumentoServletOperation.DOWNLOAD);
        if (downloadDocumentoInfo == null) {
            writeNotFoundResponse(resp);
            return;
        }

        DocumentoBin documento = documentoBinSearch.getTermoAdesaoByUUID(downloadDocumentoInfo.getUid());
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

    private void writeNotFoundResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.getOutputStream().flush();
    }

    private void writeDocumentoBinToResponse(HttpServletResponse resp, DocumentoBin documento) throws IOException {
        byte[] data = documentoBinarioManager.getData(documento.getId());
        String contentType = "application/" + documento.getExtensao();
        resp.setContentType(contentType);
        resp.setStatus(200);
        resp.getOutputStream().write(data);
        resp.getOutputStream().flush();
    }

    private String buildPathWithFilename(HttpServletRequest req, DocumentoBin documento) {
        UriBuilder uriBuilder = UriBuilder.fromPath(req.getRequestURI());
        uriBuilder = uriBuilder.path(String.format("%s.%s", documento.getNomeArquivo(), documento.getExtensao()));
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

class DocumentoInfo {
    private final String uid;
    private final String filename;

    DocumentoInfo(String uid, String filename) {
        this.uid = uid;
        this.filename = filename;
    }

    public String getUid() {
        return uid;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isFileNameEmpty() {
        return filename == null || filename.isEmpty();
    }

}