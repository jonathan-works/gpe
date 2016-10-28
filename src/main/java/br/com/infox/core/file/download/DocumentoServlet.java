package br.com.infox.core.file.download;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

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

import org.apache.commons.io.IOUtils;

import br.com.infox.epp.documento.DocumentoBinSearch;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;

@WebServlet(urlPatterns = DocumentoServlet.BASE_SERVLET_PATH + "/*")
public class DocumentoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public static final String BASE_SERVLET_PATH = "/file";

    @Inject private DocumentoBinSearch documentoBinSearch;
    @Inject private DocumentoBinManager documentoBinManager;
    @Inject private FileDownloader fileDownloader;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DocumentoInfo downloadDocumentoInfo = extractFromRequest(req, DocumentoServletOperation.DOWNLOAD);
        DocumentoBin documento=null;
        DownloadResource downloadResource = null;
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
            } else if (documentoDownload instanceof DownloadResource){
                downloadResource = (DownloadResource) documentoDownload;
            }
        }
        if (documento == null && downloadResource == null) {
            documento = firstNonNull(
                documentoBinSearch.getTermoAdesaoByUUID(UUID.fromString(downloadDocumentoInfo.getUid())),
                documentoBinSearch.getDocumentoPublicoByUUID(UUID.fromString(downloadDocumentoInfo.getUid()))
            );
        }
        
        if (downloadResource != null){
            processDownload(req, resp, downloadResource);
        } else if (documento != null){
            processDownload(req, resp, documento);
        } else {
            writeNotFoundResponse(resp);
        }
    }
    
    private void processDownload(HttpServletRequest req, HttpServletResponse resp, DownloadResource downloadResource) throws IOException{
        String nomeArquivo = downloadResource.getFileName();
        if (!URI.create(req.getRequestURI()).getPath().endsWith(nomeArquivo)) {
            UriBuilder uriBuilder = UriBuilder.fromPath(req.getRequestURI());
            uriBuilder.path(nomeArquivo);
            resp.sendRedirect(uriBuilder.build().toString());
            return;
        }
        req.getSession().removeAttribute("documentoDownload");
        writeDownloadResponse(resp, downloadResource);
    }
    
    private void processDownload(HttpServletRequest req, HttpServletResponse resp, DocumentoBin documento) throws IOException{
        String nomeArquivo = fileDownloader.extractNomeArquivo(documento);
        if (!URI.create(req.getRequestURI()).getPath().endsWith(nomeArquivo)) {
            UriBuilder uriBuilder = UriBuilder.fromPath(req.getRequestURI());
            uriBuilder.path(nomeArquivo);
            resp.sendRedirect(uriBuilder.build().toString());
            return;
        }
        req.getSession().removeAttribute("documentoDownload");
        writeDownloadResponse(resp, documento);
    }

    private void deleteFile(final DownloadResource toRemove) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    toRemove.delete();
                } catch (IOException e) {
                }
            }
        }).start();
    }
    
    private void writeNotFoundResponse(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        resp.flushBuffer();
    }

    private void writeDownloadResponse(HttpServletResponse resp, DownloadResource fileDownloadWrapper) throws IOException {
        resp.setContentType(fileDownloadWrapper.getContentType());
        resp.setStatus(HttpServletResponse.SC_OK);
        IOUtils.copy(fileDownloadWrapper.getInputStream(), resp.getOutputStream());
        resp.getOutputStream().flush();
        deleteFile(fileDownloadWrapper);
    }
    
    private void writeDownloadResponse(HttpServletResponse resp, DocumentoBin documento) throws IOException {
        byte[] data = fileDownloader.getData(documento);
        String contentType = "application/" + documento.getExtensao();
        resp.setContentType(contentType);
        resp.setStatus(HttpServletResponse.SC_OK);
        if ("application/pdf".equalsIgnoreCase(contentType)){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            documentoBinManager.writeMargemDocumento(documento, data, out);
            data = out.toByteArray();
        }
        resp.getOutputStream().write(data);
        resp.getOutputStream().flush();
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