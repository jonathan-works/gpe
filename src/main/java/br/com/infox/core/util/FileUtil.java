package br.com.infox.core.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public final class FileUtil {

    private static final LogProvider LOG = Logging.getLogProvider(FileUtil.class);

    private FileUtil() {
    }

    public static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.error(".close()", e);
            }
        }
    }
    
    public static String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret.toLowerCase();
    }
    
    public static File getTempFolder(){
        try {
            return getServletTempFolder();
        } catch (Exception e) {
            return getDefaultTempFolder();
        }
    }
    
    private static File getDefaultTempFolder(){
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    private static File getServletTempFolder(){
        File tempFolder = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = null;
        if (facesContext != null){
            request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        } else {
            request = BeanManager.INSTANCE.getReference(HttpServletRequest.class);
        }
        tempFolder = (File)request.getServletContext().getAttribute(ServletContext.TEMPDIR);
        return tempFolder;
    }

}
