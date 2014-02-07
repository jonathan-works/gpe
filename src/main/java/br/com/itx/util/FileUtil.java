package br.com.itx.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.jar.JarFile;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public final class FileUtil {

    private static final LogProvider LOG = Logging.getLogProvider(FileUtil.class);

    private FileUtil() {
    }

    public static boolean deleteDir(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return dir.delete();
    }

    /**
     * Copia um diretorio em outro, recursivamente.
     * 
     * @param fromDir origem. Se não for diretório é lançada
     *        IllegalArgumentException
     * @param toDir destino
     * 
     */
    public static void copyDir(File fromDir, File toDir) {
        if (!fromDir.isDirectory()) {
            throw new IllegalArgumentException(fromDir + " is not a directory.");
        }
        toDir.mkdirs();
        for (File f : fromDir.listFiles()) {
            File to = new File(toDir, f.getName());
            if (f.isDirectory()) {
                copyDir(f, to);
            }
            copy(f, to);
        }
    }

    public static void copyFile(File fromFile, File toFile) {
        if (!fromFile.equals(toFile)) {
            toFile.delete();
            toFile.getParentFile().mkdirs();
        }
        if (fromFile.isFile() && !toFile.exists()) {
            copy(fromFile, toFile);
        }
    }

    private static void copy(File fromFile, File toFile) {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = new FileInputStream(fromFile).getChannel();
            toChannel = new FileOutputStream(toFile).getChannel();
            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
        } catch (IOException err) {
            LOG.error(".copy()", err);
        } finally {
            FileUtil.close(fromChannel);
            FileUtil.close(toChannel);
        }
    }

    public static void writeFile(File file, InputStream in) {
        file.delete();
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            OutputStream out = null;
            try {
                byte[] trecho = new byte[10240];
                int quant = 0;
                out = new FileOutputStream(file);
                while ((quant = in.read(trecho)) > -1) {
                    out.write(trecho, 0, quant);
                    out.flush();
                }

            } catch (IOException err) {
                LOG.error(".writeFile()", err);
            } finally {
                close(out);
                close(in);
            }
        }
    }

    public static void readFile(File file, OutputStream out) {
        if (file.isFile()) {
            InputStream in = null;
            try {
                byte[] trecho = new byte[10240];
                int quant = 0;
                in = new FileInputStream(file);
                while ((quant = in.read(trecho)) > -1) {
                    out.write(trecho, 0, quant);
                    out.flush();
                }
            } catch (IOException err) {
                LOG.error(".readFile()", err);
            } finally {
                close(out);
                close(in);
            }
        }
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

    public static void writeText(File file, boolean append, String text) {
        if (file != null && text != null) {
            FileWriter out = null;
            try {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                out = new FileWriter(file, append);
                out.write(text);

            } catch (IOException e) {
                LOG.error(".writeText()", e);
            } finally {
                close(out);
            }
        }
    }

    public static String readText(File file) throws IOException {
        StringBuilder text = new StringBuilder();
        FileReader in = null;
        BufferedReader br = null;
        try {
            if (file != null && file.isFile()) {
                in = new FileReader(file);
                br = new BufferedReader(in);
                String lineSep = System.getProperty("line.separator");
                String line = null;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append(lineSep);
                }

            }
        } catch (IOException e) {
            LOG.error(".readText()", e);
        } finally {
            close(in);
            close(br);
        }
        return text.toString();
    }

    public static String readStreamAsText(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (in != null) {
            try {
                byte[] trecho = new byte[10240];
                int quant = 0;
                while ((quant = in.read(trecho)) > -1) {
                    out.write(trecho, 0, quant);
                    out.flush();
                }
            } catch (IOException err) {
                LOG.error(".readStreamAsText()", err);
            } finally {
                close(out);
                close(in);
            }
        }
        return new String(out.toByteArray());
    }

}
