package br.com.infox.epp.assinador;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
public class CertificadosDownloaderMain {
 
    public static void copyProxyEnv(){
        String http_proxy = System.getenv("http_proxy");
        String no_proxy = System.getenv("no_proxy");
        if (no_proxy != null){
            no_proxy=no_proxy.replaceAll("[,|;| ]", "|");
        }
        
        if (http_proxy != null && http_proxy.matches(".*(:[0-9]+)?")){
            String[] split = http_proxy.split(":");
            System.setProperty("http.proxyHost", split[0]);
            System.setProperty("http.proxyPort", split.length > 1 ? split[1] : "80");
            if (no_proxy != null){
                System.setProperty("http.nonProxyHosts", no_proxy);
            }
        }
        String https_proxy = System.getenv("https_proxy");
        if (https_proxy != null && https_proxy.matches(".*(:[0-9]+)?")){
            String[] split = https_proxy.split(":");
            System.setProperty("https.proxyHost", split[0]);
            System.setProperty("https.proxyPort", split.length > 1 ? split[1] : "443");
        }
        String ftp_proxy = System.getenv("ftp_proxy");
        if (ftp_proxy != null && ftp_proxy.matches(".*(:[0-9]+)?")){
            String[] split = ftp_proxy.split(":");
            System.setProperty("ftp.proxyHost", split[0]);
            System.setProperty("ftp.proxyPort", split.length > 1 ? split[1] : "80");
            if (no_proxy != null){
                System.setProperty("ftp.nonProxyHosts", no_proxy);
            }
        }
    }
    
    public static void main(String[] args) {
        copyProxyEnv();
        String url = null;
        String out = "";
        for (int i = 0; i < args.length; i++) {
            switch(args[i]){
            case "--url":
                url = args[++i];
                break;
            case "-o":
                out = args[++i];
                break;
            }
        }
        
        CertificadosDownloader certificadosDownloader = new CertificadosDownloader();
        certificadosDownloader.setCertificatePath(url);
        
        Path outputFolder = Paths.get(out);
        Date lastModified = getLastUpdated(outputFolder);
        if (!certificadosDownloader.isUpToDate(lastModified)) {
            certificadosDownloader.saveTo(outputFolder);
            saveLastModifiedDate(outputFolder, certificadosDownloader.getLastModified());
        }
    }

    private static void saveLastModifiedDate(Path outputFolder, Date lastModified){
        try {
            Path lastModifiedPath = outputFolder.resolve("lastModified");
            if (Files.isWritable(lastModifiedPath.getParent())){
                Files.deleteIfExists(lastModifiedPath);
                Files.write(lastModifiedPath, new DateTime(lastModified).toString(ISODateTimeFormat.dateTime()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e){
        }
    }
    
    private static Date getLastUpdated(Path outputFolder) {
        try {
            Path lastModifiedPath = outputFolder.resolve("lastModified");
            if (Files.exists(lastModifiedPath)){
                List<String> lines = Files.readAllLines(lastModifiedPath, StandardCharsets.UTF_8);
                if (lines != null && !lines.isEmpty()){
                    return DateTime.parse(lines.get(0), ISODateTimeFormat.dateTime()).toDate();
                }
            }
        } catch (Exception e){
        }
        return new Date(0);
    }
     
}
