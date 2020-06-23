package br.com.infox.epp.assinador;

import static br.com.infox.core.util.FileUtil.getTempFolder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Stateless
public class CertificadosDownloader {

    private static final String DOWNLOAD_CERTIFICATE_PATH = "downloadCertificatePath";

    private static final String DEFAULT_PATH = "http://acraiz.icpbrasil.gov.br/credenciadas/CertificadosAC-ICP-Brasil/ACcompactado.zip";

    private static final LogProvider LOG = Logging.getLogProvider(CertificadosDownloader.class);

    private String certificatePath;

    @Inject
    private ParametroManager parametroManager;
    
    @PostConstruct
    void init() {
        if (parametroManager != null){
            parametroManager.getValorParametro(DOWNLOAD_CERTIFICATE_PATH);
        }
    }

    public Date getLastModified() {
        URI certificatePath = URI.create(getCertificatePath());
        try {
            URLConnection urlConnection = certificatePath.toURL().openConnection();
            long lastModified = urlConnection.getLastModified();
            if (lastModified != 0)
                return new Date(lastModified);
        } catch (Exception e) {
            LOG.warn("Falha ao tentar baixar certificados de '"+certificatePath+"'",e);
        }
        return null;
    }
    
    public boolean isUpToDate(Date lastUpdated){
        Date lastModified = getLastModified();
        return lastModified == null || lastModified.before(lastUpdated);
    }

    private Path createTempFile() throws MalformedURLException, FileNotFoundException, IOException {
        URI certificatePath = URI.create(getCertificatePath());
        
        Path baseTmpFolder = Files.createDirectories(getTempFolder().toPath().resolve("certificates"));
        Path directory = Files.createTempDirectory(baseTmpFolder, "cert-");
        Path tempFile = Files.createTempFile(directory, "x509-", ".tmp.zip");
        IOUtils.copy(certificatePath.toURL().openStream(), new FileOutputStream(tempFile.toFile()));
        return tempFile;
    }
    
    public Collection<X509Certificate> download() {
        try {
            DownloaderCollector collector = new DownloaderCollector();
            execute(collector);
            return collector.getLoadedCertificates();
        } catch (Throwable e) {
            LOG.error("Tratamento de erro no download das cadeias validadoras de certificados ICP-Brasil", e);
        }

        return Collections.emptyList();
    }
    
    public void saveTo(final Path outputFolder){
        try {
            execute(new DownloaderCopier(outputFolder));
        } catch (Throwable e) {
            LOG.error("Tratamento de erro no download das cadeias validadoras de certificados ICP-Brasil", e);
        }
    }
    
    private void execute(DownloaderAction action){
        try {
            Path file = createTempFile();
            try {
                action.execute(file);
            } finally {
                file.getParent().toFile().delete();
                file.toFile().delete();
            }
        } catch (Exception e){
            LOG.error(e);
        }
    }

    public String getCertificatePath() {
        return ObjectUtils.defaultIfNull(certificatePath, DEFAULT_PATH);
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

}

interface DownloaderAction {
    void execute(Path fsPath) throws IOException;
}
class DownloaderCollector implements DownloaderAction{
    
    private final static class X509CertificateFileCollector extends SimpleFileVisitor<Path> {
        private  Collection<X509Certificate> loadedCertificates;
        
        public X509CertificateFileCollector(Collection<X509Certificate> loadedCertificates) {
            this.loadedCertificates=loadedCertificates;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            X509Certificate cert = convertToX509Certificate(file.toUri().toURL().openStream());
            if (cert != null)
                loadedCertificates.add(cert);
            return FileVisitResult.CONTINUE;
        }

        private X509Certificate convertToX509Certificate(InputStream bis) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                return (X509Certificate) cf.generateCertificate(bis);
            } catch (CertificateException e) {
                return null;
            }
        }
    }

    private  List<X509Certificate> loadedCertificates = new ArrayList<>();

    @Override
    public void execute(Path fsPath) throws IOException{
        FileSystem fs = FileSystems.newFileSystem(fsPath, ClassLoader.getSystemClassLoader());
        Path root = fs.getPath("/");
        Files.walkFileTree(root, new X509CertificateFileCollector(loadedCertificates));
    }

    public List<X509Certificate> getLoadedCertificates() {
        return loadedCertificates;
    }
    
}
class DownloaderCopier implements DownloaderAction {

    private final static class FileCopier extends SimpleFileVisitor<Path> {
        private Path outputFolder;
        public FileCopier(Path outputFolder) {
            this.outputFolder=outputFolder;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.toString();
            fileName = fileName.substring(fileName.lastIndexOf('/')+1);
            Path out = outputFolder.resolve(fileName);
            Files.createDirectories(out.getParent());
            IOUtils.copy(file.toUri().toURL().openStream(), new FileOutputStream(out.toFile()));
            return FileVisitResult.CONTINUE;
        }
    }

    private Path outputFolder;

    public DownloaderCopier(Path outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public void execute(Path fsPath) throws IOException {
        try(FileSystem fs = FileSystems.newFileSystem(fsPath, ClassLoader.getSystemClassLoader())){
	        Path root = fs.getPath("/");
	        Files.walkFileTree(root, new FileCopier(outputFolder));
        }
    }

}