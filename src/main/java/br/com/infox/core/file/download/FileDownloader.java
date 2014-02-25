package br.com.infox.core.file.download;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

@Name(FileDownloader.NAME)
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class FileDownloader implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "fileDownloader";
    private static final LogProvider LOG = Logging.getLogProvider(FileDownloader.class);

    public static void download(byte[] data, String contentType, String fileName) {
        if (data == null) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType(contentType);
        response.setContentLength(data.length);
        response.setHeader("Content-disposition", "attachment; filename=\""
                + fileName + "\"");
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

}
