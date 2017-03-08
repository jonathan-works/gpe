package br.com.infox.core.file.download;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.primefaces.context.RequestContext;

import com.lowagie.text.DocumentException;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
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
    @Inject private PdfManager pdfManager;
    @Inject private InfoxMessages infoxMessages;
    @Inject private DownloadResourceFactory downloadResourceFactory;
    @Inject private DocumentoBinManager documentoBinManager;
    
    public static void download(DownloadResource downloadResource){
        if (downloadResource == null)
            return;
        HttpServletResponse response;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        response = facesContext != null ? (HttpServletResponse) facesContext.getExternalContext().getResponse() : BeanManager.INSTANCE.getReference(HttpServletResponse.class);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength((int) downloadResource.getDataLength());
        response.setContentType(downloadResource.getContentType());
        if ("application/pdf".equals(downloadResource.getContentType())){
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", downloadResource.getFileName()));
        }
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if(externalContext.isSecure()) {
            externalContext.setResponseHeader("Cache-Control", "public");
            externalContext.setResponseHeader("Pragma", "public");
        }
        try {
            IOUtils.copy(downloadResource.getInputStream(), response.getOutputStream());
            response.getOutputStream().flush();
            if (facesContext != null){
                facesContext.responseComplete();
            }
            downloadResource.delete();
        } catch (IOException e) {
            LOG.error(".download()", e);
            FacesMessages.instance().add(String.format("Erro ao descarregar o arquivo: %s", downloadResource.getFileName()));
        }
    }
    
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
        return getDownloadUrl(documento == null ? null : documento.getDocumentoBin());
    }
    public String getDownloadUrl(Documento documento){
        return getDownloadUrl(documento == null ? null : documento.getDocumentoBin());
    }
    public String getDownloadUrl(DocumentoBin documentoBin){
        String contextPath = getRequest().getContextPath();
        UriBuilder uriBuilder = UriBuilder.fromPath(contextPath);
        uriBuilder = uriBuilder.path(DocumentoServlet.BASE_SERVLET_PATH);
        if (documentoBin != null){
            uriBuilder = uriBuilder.path(documentoBin.getUuid().toString());
            uriBuilder = uriBuilder.path(DocumentoServletOperation.DOWNLOAD.getPath());
            uriBuilder = uriBuilder.path(extractNomeArquivo(documentoBin));
        } else {
            uriBuilder = uriBuilder.path(DocumentoServletOperation.DOWNLOAD.getPath());
        }
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
        if (documentoBin == null)
            return;
        
        downloadDocumentoViaServlet(getData(documentoBin), getContentType(documentoBin), extractNomeArquivo(documentoBin));
    }
    public void downloadDocumentoViaServlet(Documento documento) throws IOException {
        if (documento == null)
            return;
        downloadDocumentoViaServlet(documento.getDocumentoBin());
    }
    public void downloadDocumentoViaServlet(DocumentoBin documentoBin, boolean redirect) throws IOException {
        if (documentoBin == null)
            return;
        
        downloadDocumentoViaServlet(getData(documentoBin), getContentType(documentoBin), extractNomeArquivo(documentoBin), redirect);
    }
    public void downloadDocumentoViaServlet(Documento documento, boolean redirect) throws IOException {
        if (documento == null)
            return;
        downloadDocumentoViaServlet(documento.getDocumentoBin(), redirect);
    }
    
    public void downloadDocumentoViaServlet(byte[] data, String contentType, String fileName) throws IOException{
        downloadDocumentoViaServlet(data, contentType, fileName, true);
    }
    public void downloadDocumentoViaServlet(byte[] data, String contentType, String fileName, boolean redirect) throws IOException{
        DownloadResource downloadResource = downloadResourceFactory.create(fileName, contentType, data);
        HttpSession session = getRequest().getSession();
        session.setAttribute("documentoDownload", downloadResource);
        if (redirect){
            redirect(downloadResource);
        } else {
            openPopUp(downloadResource);
        }
    }
    
    private void openPopUp(DownloadResource downloadResource) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("window.open('").append(getDownloadUrl((DocumentoBin)null)).append("'").append(", ");
        sb.append("'").append(downloadResource.getFileName()).append("'").append(", ");
        sb.append("[");
        sb.append("'width=',outerWidth,");
        sb.append("',height=',outerHeight,");
        sb.append("',top=',screen.top || window['screenY'] || window['screenTop'] || 0,");
        sb.append("',left=',screen.left || window['screenX'] || window['screenLeft'] || 0,");
        sb.append("',resizable=YES',");
        sb.append("',scrollbars=YES',");
        sb.append("',status=NO',");
        sb.append("',location=NO'");
        sb.append("].join(''));");
        RequestContext.getCurrentInstance().execute(sb.toString());
    }
    
    private void redirect(DownloadResource downloadResource) throws IOException{
        getResponse().sendRedirect(getDownloadUrl((DocumentoBin)null));
    }

    public String getMensagemDocumentoNulo() {
        return infoxMessages.get("documentoProcesso.error.noFileOrDeleted");
    }
    public String extractNomeArquivo(DocumentoBin documento) {
        String nomeArquivo=documento.getNomeArquivo();
        String extensao = documento.getExtensao();
        if (StringUtil.isEmpty(extensao)){
            documento.setExtensao("pdf");
        }
        if (StringUtil.isEmpty(nomeArquivo)){
            nomeArquivo=documento.getUuid().toString();
        }
        if (!nomeArquivo.endsWith("."+extensao))
            nomeArquivo = nomeArquivo+"."+documento.getExtensao();
        return nomeArquivo;
    }

    public byte[] getData(DocumentoBin documento) {
        byte[] data = new byte[0];
        if (documentoBinarioManager.existeBinario(documento.getId())) {
            data = documentoBinarioManager.getData(documento.getId());
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                String modeloDocumento = documento.isBinario() ? getMensagemDocumentoNulo()
                        : defaultIfNull(documento.getModeloDocumento(), getMensagemDocumentoNulo());
                pdfManager.convertHtmlToPdf(modeloDocumento, outputStream);
                documento.setExtensao("pdf");
            } catch (DocumentException e) {
            }
            data = outputStream.toByteArray();
        }
         
        if (podeExibirMargem(documento)) {
            data = documentoBinManager.writeMargemDocumento(data, documentoBinManager.getTextoAssinatura(documento), documentoBinManager.getTextoCodigo(documento.getUuid()), documentoBinManager.getQrCodeSignatureImage(documento));
        }
        return data;
    }
    
    private boolean podeExibirMargem(DocumentoBin documento) {
        return "pdf".equalsIgnoreCase(documento.getExtensao())
                && (Boolean.TRUE.equals(documento.getSuficientementeAssinado())
                        || documento.getAssinaturas() != null && !documento.getAssinaturas().isEmpty());
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
    	byte[] data = getData(documentoBin);
    	download(data, "application/" + documentoBin.getExtensao(), documentoBin.getNomeArquivo());
    }
}
