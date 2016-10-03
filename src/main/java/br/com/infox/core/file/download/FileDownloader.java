package br.com.infox.core.file.download;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(FileDownloader.NAME)
@Scope(ScopeType.EVENT)
@Stateless
@BypassInterceptors
public class FileDownloader implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "fileDownloader";
    private static final LogProvider LOG = Logging.getLogProvider(FileDownloader.class);
    
    @Inject
    private DocumentoBinarioManager documentoBinarioManager;
    
    public static void download(byte[] data, String contentType, String fileName) {
        if (data == null) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = prepareDownloadResponse(contentType, fileName);
        response.setContentLength(data.length);
        try {
            OutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            facesContext.responseComplete();
        } catch (IOException ex) {
            LOG.error(".download()", ex);
            FacesMessages.instance().add("Erro ao descarregar o arquivo: "
                    + fileName);
        }
    }
    
    public static HttpServletResponse prepareDownloadResponse(String contentType, String fileName) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType(contentType);
        if (!contentType.equals("text/html") && !contentType.equals("application/pdf")) {
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + fileName + "\"");
        }
        return response;
    }

    public boolean isPdf(DocumentoTemporario documento){
        return isPdf(documento.getDocumentoBin());
    }
    public boolean isPdf(Documento documento){
        return isPdf(documento.getDocumentoBin());
    }
    public boolean isPdf(DocumentoBin documentoBin){
        return "pdf".equalsIgnoreCase(documentoBin.getExtensao()) || StringUtils.isEmpty(documentoBin.getExtensao());
    }
    
    public String getDownloadUrl(DocumentoTemporario documento){
        return getDownloadUrl(documento.getDocumentoBin());
    }
    public String getDownloadUrl(Documento documento){
        return getDownloadUrl(documento.getDocumentoBin());
    }
    public String getDownloadUrl(DocumentoBin documentoBin){
        String contextPath = getRequest().getContextPath();
        UriBuilder uriBuilder = UriBuilder.fromPath(contextPath);
        uriBuilder = uriBuilder.path(DocumentoServlet.BASE_SERVLET_PATH);
        uriBuilder = uriBuilder.path(DocumentoServletOperation.DOWNLOAD.getPath());
        return uriBuilder.build().toString();
    }
    
    public String getDownloadUrlDocumentoPublico(DocumentoTemporario documento){
        return getDownloadUrl(documento.getDocumentoBin());
    }
    public String getDownloadUrlDocumentoPublico(Documento documento){
        return getDownloadUrl(documento.getDocumentoBin());
    }
    public String getDownloadUrlDocumentoPublico(DocumentoBin documentoBin){
        String contextPath = getRequest().getContextPath();
        UriBuilder uriBuilder = UriBuilder.fromPath(contextPath);
        uriBuilder = uriBuilder.path(DocumentoServlet.BASE_SERVLET_PATH);
        uriBuilder = uriBuilder.path(documentoBin.getUuid().toString());
        uriBuilder = uriBuilder.path(DocumentoServletOperation.DOWNLOAD.getPath());
        return uriBuilder.build().toString();
    }
    
    public String getContentType(DocumentoTemporario documento){
        return getContentType(documento.getDocumentoBin());
    }
    public String getContentType(Documento documento){
        return getContentType(documento.getDocumentoBin());
    }
    public String getContentType(DocumentoBin documentoBin){
        return String.format("application/%s", documentoBin.getExtensao());
    }
    public void downloadDocumentoViaServlet(DocumentoBin documentoBin) throws IOException {
        HttpSession session = getRequest().getSession();
        session.setAttribute("documentoDownload", documentoBin);
        
        getResponse().sendRedirect(getDownloadUrl(documentoBin));
    }
    public void downloadDocumentoViaServlet(Documento documento) throws IOException {
        HttpSession session = getRequest().getSession();
        session.setAttribute("documentoDownload", documento);
        
        getResponse().sendRedirect(getDownloadUrl(documento));
    }
    
    public HttpServletResponse getResponse() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null){
            return (HttpServletResponse) facesContext.getExternalContext().getResponse();
        }
        return BeanManager.INSTANCE.getReference(HttpServletResponse.class);
    }

    public HttpServletRequest getRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null){
            return (HttpServletRequest) facesContext.getExternalContext().getRequest();
        }
        return BeanManager.INSTANCE.getReference(HttpServletRequest.class);
    }

    public void download(DocumentoBin documentoBin) {
    	byte[] data = documentoBinarioManager.getData(documentoBin.getId());
    	download(data, "application/" + documentoBin.getExtensao(), documentoBin.getNomeArquivo());
    }
}
