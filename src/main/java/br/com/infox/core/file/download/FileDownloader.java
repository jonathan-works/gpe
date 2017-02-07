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
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;

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
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;

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
    @Inject
    private PdfManager pdfManager;
    @Inject
    private InfoxMessages infoxMessages;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private PathResolver pathResolver;
    
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

    public String getContentType(DocumentoTemporario documento){
        return getContentType(documento.getDocumentoBin());
    }

    public String getContentType(Documento documento){
        return getContentType(documento.getDocumentoBin());
    }

    public String getContentType(DocumentoBin documentoBin){
        return String.format("application/%s", documentoBin.getExtensao());
    }

    public void downloadDocumento(DocumentoBin documentoBin) throws IOException {
        if (documentoBin == null)
            return;
        
        downloadDocumento(getData(documentoBin), getContentType(documentoBin), extractNomeArquivo(documentoBin));
    }

    public void downloadDocumento(Documento documento) throws IOException {
        if (documento == null)
            return;
        downloadDocumento(documento.getDocumentoBin());
    }

    public void downloadDocumento(byte[] data, String contentType, String fileName) throws IOException{
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setContentType(contentType);
        response.addHeader("Content-disposition", "filename=\"" + MimeUtility.encodeWord(fileName ) + "\"");
        response.setContentLength(data.length);
        response.getOutputStream().write(data, 0, data.length);
        response.getOutputStream().flush();
        FacesContext.getCurrentInstance().responseComplete();
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

    public String getWindowOpen(Boolean isPdf) {
        return isPdf
                ? "window.open('" + pathResolver.getContextPath() + "/downloadDocumento.seam', '_blank');"
                : "window.open('" + pathResolver.getContextPath() + "/downloadDocumento.seam', '_self');";
    }

    public String getWindowOpen(DocumentoBin documentoBin) {
        return getWindowOpen(isPdf(documentoBin));
    }

    public String getWindowOpenByIdDocumento(Integer idDocumento) {
        Documento documento = documentoManager.find(idDocumento);
        return documento == null ? "" : getWindowOpen(documento.getDocumentoBin());
    }

    public String getWindowOpenByIdDocumentoBin(Integer idDocumentoBin) {
        DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
        return getWindowOpen(documentoBin);
    }
}
