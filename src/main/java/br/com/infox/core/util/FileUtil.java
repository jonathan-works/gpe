package br.com.infox.core.util;

import java.io.Closeable;
import java.io.IOException;

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

}
