package br.com.itx.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.jar.JarFile;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

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

    public static void close(JarFile jarFile) {
        if (jarFile != null) {
            try {
                jarFile.close();
            } catch (IOException e) {
                LOG.error(".close()", e);
            }
        }
    }


}
