package br.com.infox.epp.access.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoECPF;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.assinador.CertificadosDownloader;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(CertificateManager.NAME)
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class CertificateManager {

    public static final String NAME = "certificateManager";
    private static final LogProvider LOG = Logging.getLogProvider(CertificateManager.class);
    private static final String BR = System.getProperty("line.separator");
    private List<String> acceptedCaNameList;
    private StringBuilder acceptedCaNameSb;
    private List<X509Certificate> listCertificadosCA;
    private Date lastUpdated=new Date(0);

    @Observer({ "org.jboss.seam.postInitialization",
        "org.jboss.seam.postReInitialization" })
    public synchronized void init() {
        updateIfModified();
        LOG.info("Inicializado");
    }

    public List<X509Certificate> getListCertificadosCA() {
        updateIfModified();
        return listCertificadosCA;
    }

    private void populateListMap() throws CertificateException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

        List<URL> files = new ArrayList<URL>();
        try {
            files = getResourceListing(CertificateManager.class, "certificados/", "(.*crt$|.*cer$)");
        } catch (Exception e) {
            LOG.error("CertificadosCaCheckManager.populateListMap()", e);
        }

        InputStream is = null;
        for (URL fileCert : files) {
            try {
                is = fileCert.openStream();
                X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(is);
                register(x509Cert);
                is.close();
            } catch (IOException | CertificateException e) {
                LOG.error("Erro ao gerar certificado para " + fileCert, e);
            } finally {
                FileUtil.close(is);
            }
        }
        Date lastModified=Beans.getReference(CertificadosDownloader.class).getLastModified();
        for (Iterator<X509Certificate> iterator = Beans.getReference(CertificadosDownloader.class).download().iterator(); iterator.hasNext();) {
            register(iterator.next());
        }
        lastUpdated=lastModified;
    }


    private void updateIfModified() {
        if (!Beans.getReference(CertificadosDownloader.class).isUpToDate(lastUpdated)){
            listCertificadosCA = new ArrayList<X509Certificate>();
            acceptedCaNameList = new ArrayList<String>();
            acceptedCaNameSb = new StringBuilder();
            try {
                populateListMap();
            } catch (CertificateException e) {
                LOG.error("CertificadosCaCheckManager.init()", e);
                throw new RuntimeException("Erro ao iniciar " + NAME + ": "
                        + e.getMessage(), e);
            }
        }
    }
    
    private void register(X509Certificate x509Cert) {
        listCertificadosCA.add(x509Cert);
        String cnName = CertificadoECPF.getCNValue(x509Cert.getSubjectDN().getName());
        acceptedCaNameList.add(cnName);
        acceptedCaNameSb.append(cnName);
        acceptedCaNameSb.append(BR);
    }

    public void verificaCertificado(String certChain) throws CertificadoException, CertificateException {
        X509Certificate[] x509Certificates = DigitalSignatureUtils.loadCertFromBase64String(certChain);
        verificaCertificado(x509Certificates);
    }

    public void verificaCertificado(X509Certificate[] certChain) throws CertificateException {
        updateIfModified();
        X509Certificate certificate = certChain[0];

        // primeiro valida se cada elemento da cadeia está validado pelo proximo
        if (certChain.length > 1) {
            for (int i = 1; i < certChain.length; i++) {
                X509Certificate trustedCert = certChain[i];
                try {
                    certificate.verify(trustedCert.getPublicKey());
                } catch (GeneralSecurityException ex) {
                    String msg = "A validade da cadeia não pode ser verificada.";
                    LOG.info(msg);
                    throw new CertificateException(msg);
                }
                certificate = trustedCert;
            }
        }

        X509Certificate[] aTrustedCertificates = listCertificadosCA.toArray(new X509Certificate[listCertificadosCA.size()]);

        // valida se algum elemento da cadeia foi assinado pela lista das
        // certificadoras da certSign
        // se foi então o certificado é valido, pois a cadeia foi devidamente
        // testada.
        for (int i = 0; i < certChain.length; i++) {
            X509Certificate cert = certChain[i];
            try {
                DigitalSignatureUtils.verifyCertificate(cert, aTrustedCertificates);
                LOG.info("Certificado Verificado com sucesso. ");
                return;
            } catch (CertificateExpiredException e) {
                throw e;
            } catch (GeneralSecurityException e) {
                LOG.error("CertificadosCaCheckManager.verificaCertificado(X509Certificate[])", e);
            }
        }

        String msg = "A validade do certificado não pode ser verificada junto ao ICP-Brasil.";
        LOG.info(msg);
        throw new CertificateException(msg);
    }

    public List<String> getAcceptedCaNameList() {
        updateIfModified();
        return acceptedCaNameList;
    }

    public String getAcceptedCa() {
        updateIfModified();
        return acceptedCaNameSb.toString();
    }

    public void download() {
        byte[] data = getAcceptedCa().getBytes();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("text/plain");
        response.setContentLength(data.length);
        String file = "file.txt";
        response.setHeader("Content-disposition", "inline; filename=\"" + file
                + "\"");

        try {
            OutputStream out = response.getOutputStream();
            out.write(data);
            out.flush();
            facesContext.responseComplete();
        } catch (IOException ex) {
            FacesMessages.instance().add(Severity.ERROR, "Error while downloading the file: "
                    + file);
        }
    }

    public static CertificateManager instance() {
        return ComponentUtil.getComponent(NAME, ScopeType.APPLICATION);
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation. Works for regular files and also
     * JARs.
     * 
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources
     *        you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    public static List<URL> getResourceListing(Class<? extends Object> clazz,
            String path, String pattern) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL == null)
            return Collections.emptyList();
        
        if (dirURL.getProtocol().equals("vfsfile")
                || dirURL.getProtocol().equals("vfs")) {
            dirURL = new URL("file", dirURL.getHost(), dirURL.getFile());
        }
        if (dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            List<URL> ret = new ArrayList<URL>();
            for (File f : new File(dirURL.toURI()).listFiles()) {
                if (f.getName().matches(pattern)) {
                    ret.add(f.toURI().toURL());
                }
            }
            return ret;
        }

        /*
         * In case of a jar file, we can't actually find a directory. Have to
         * assume the same jar as clazz.
         */
        String me = clazz.getName().replace(".", "/") + ".class";
        dirURL = clazz.getClassLoader().getResource(me);

        if (dirURL.getProtocol().equals("vfszip")) {
            dirURL = new URL("jar", dirURL.getHost(), "file:"
                    + dirURL.getFile().replace(".jar/", ".jar!/"));
        }
        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip
                                                                                           // out
                                                                                           // only
                                                                                           // the
                                                                                           // JAR
                                                                                           // file
            JarFile jar = null;
            Enumeration<JarEntry> entries = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                entries = jar.entries(); // gives ALL entries
            } finally {
                if (jar != null) {
                    jar.close();
                }
            }
            // in jar
            List<URL> result = new ArrayList<URL>(); 
            // avoid duplicates in case it is a subdirectory
            while (entries != null && entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path) && !name.equals(path)
                        && name.matches(pattern)) { 
                    // filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf('/');
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    URL u = new URL("jar:file:" + jarPath + "!/" + name);
                    result.add(u);
                }
            }
            return result;
        }

        throw new UnsupportedOperationException("Cannot list files for URL "
                + dirURL);
    }
    
    public CertificadoDadosPessoaFisica getDadosPessoaFisicaFromCertificate(String token) throws CertificadoException {
    	CertificateSignatureBundleBean bundle = getSignatureBundle(token);
		String certChain = bundle.getSignatureBeanList().get(0).getCertChain();
		Certificado c = CertificadoFactory.createCertificado(certChain);
		CertificadoDadosPessoaFisica certificadoDadosPessoaFisica = (CertificadoDadosPessoaFisica) c;
        return certificadoDadosPessoaFisica;
    }
    
    private CertificateSignatureBundleBean getSignatureBundle(String token) {
		CertificateSignatures certificateSignatures = ComponentUtil.getComponent(CertificateSignatures.NAME);
		return certificateSignatures.get(token);
	}

    public CertificadoDadosPessoaFisica getDadosPessoaFisica(DadosAssinatura dadosAssinatura) throws CertificadoException {
        Certificado c = CertificadoFactory.createCertificado(dadosAssinatura.getCertChainBase64());
        CertificadoDadosPessoaFisica certificadoDadosPessoaFisica = (CertificadoDadosPessoaFisica) c;
        return certificadoDadosPessoaFisica;
    }

}
