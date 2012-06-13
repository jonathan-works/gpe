package br.com.infox.core.certificado;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.certificado.util.DigitalSignatureUtils;
import br.com.itx.component.MeasureTime;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.FileUtil;

/**
 * Componente com escopo de aplicação responsavel pelo teste
 * de validade dos certificados confrontando a corrente de 
 * certificados com a lista das certificadoras que o ICP-Brasil 
 * fornece: http://www.iti.gov.br/twiki/bin/view/Certificacao/RepoZipCert.
 * Os arquivos ficam descompctados em uma pasta no servidor e 
 * quando a aplicação sobe os arquivos são lidos e os certificados
 * são carregados em uma lista.
 * @author Rodrigo Menezes - rodrigo@infox.com.br
 *
 */
@Name(CertificadosCaCheckManager.NAME)
@BypassInterceptors
@Scope(ScopeType.APPLICATION)
public class CertificadosCaCheckManager {

	public static final String NAME = "certificadosCaCheckManager";
	private static final LogProvider log = Logging.getLogProvider(CertificadosCaCheckManager.class);
	private static final String BR = System.getProperty("line.separator");
	private List<String> acceptedCaNameList;
	private StringBuilder acceptedCaNameSb;
	private List<X509Certificate> listCertificadosCA;
	
	@Observer({"org.jboss.seam.postInitialization","org.jboss.seam.postReInitialization"})
	public synchronized void init() {
		MeasureTime mt = new MeasureTime(true);
		listCertificadosCA = new ArrayList<X509Certificate>();
		acceptedCaNameList = new ArrayList<String>();
		acceptedCaNameSb = new StringBuilder();
		try {
			populateListMap();
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao iniciar " + NAME + ": " + e.getMessage(), e);
		}
		log.info(MessageFormat.format("Inicializado [{0} ms]", mt.getTime()));
	}

	public List<X509Certificate> getListCertificadosCA() {
		return listCertificadosCA;
	}

	private void populateListMap() throws CertificateException {
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		
		List<URL> files = new ArrayList<URL>();
		try {
			files = getResourceListing(CertificadosCaCheckManager.class, "certificados/", "(.*crt$|.*cer$)");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		InputStream is = null;
		for (URL fileCert : files) {
			try {
				is = fileCert.openStream();
				X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(is);
				listCertificadosCA.add(x509Cert);
				String cnName = Certificado.getCNValue(x509Cert.getSubjectDN().getName());
				acceptedCaNameList.add(cnName);
				acceptedCaNameSb.append(cnName);
				acceptedCaNameSb.append(BR);
				is.close();
			} catch (Exception e) {
				System.out.println(fileCert.toString());
				e.printStackTrace();
			} finally {
				FileUtil.close(is);
			}
		}

	}
	
	public void verificaCertificado(String certChain) throws CertificadoException, CertificateException {
		X509Certificate[] x509Certificates = DigitalSignatureUtils.loadCertFromBase64String(certChain);
		verificaCertificado(x509Certificates);
	}
	  
    public void verificaCertificado(X509Certificate[] certChain) throws CertificateException {
    	MeasureTime mt = new MeasureTime(true);
    	X509Certificate certificate = certChain[0];
    	boolean valid = true;

    	//primeiro valida se cada elemento da cadeia está validado pelo proximo
    	if (certChain.length > 1) {
    		for (int i = 1; i < certChain.length; i++) {
    			X509Certificate trustedCert = certChain[i];
    			try {
    				certificate.verify(trustedCert.getPublicKey());
    			}
    			catch (GeneralSecurityException ex) {
    				valid = false;
    			}
    			certificate = trustedCert;
    		}
    	}
    	
    	if (!valid) {
    		String msg = "A validade da cadeia não pode ser verificada.";
    		log.info(msg + mt.getTime());
    		throw new CertificateException(msg);    	
    	}
    	
    	X509Certificate[] aTrustedCertificates = (X509Certificate[]) listCertificadosCA.toArray(
    			new X509Certificate[listCertificadosCA.size()]);    		

    	//valida se algum elemento da cadeia foi assinado pela lista das certificadoras da certSign
    	//se foi então o certificado é valido, pois a cadeia foi devidamente testada.
    	for (int i = 0; i < certChain.length; i++) {
    		X509Certificate cert = certChain[i];
    		try {
				DigitalSignatureUtils.verifyCertificate(cert, aTrustedCertificates);
				log.info("Certificado Verificado com sucesso. ");
				return;
			} catch (GeneralSecurityException e) { }
		}
    	
		String msg = "A validade do certificado não pode ser verificada junto ao ICP-Brasil.";
		log.info(msg + mt.getTime());
		throw new CertificateException(msg);
    }
    
    public List<String> getAcceptedCaNameList() {
		return acceptedCaNameList;
	}
    
    public String getAcceptedCa() {
    	return acceptedCaNameSb.toString();
    }

	public void download() {
		byte[] data = getAcceptedCa().getBytes();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) facesContext
					.getExternalContext().getResponse();
		response.setContentType("text/plain");
		response.setContentLength(data.length);
		String file = "file.txt";
		response.setHeader("Content-disposition", "inline; filename=\""
				+ file + "\"");

		try {
			OutputStream out = response.getOutputStream();
			out.write(data);
			out.flush();
			facesContext.responseComplete();
		} catch (IOException ex) {
			FacesMessages.instance().add(Severity.ERROR,
					"Error while downloading the file: " + file);
		}
	}    
    
    public static CertificadosCaCheckManager instance() {
    	return ComponentUtil.getComponent(NAME, ScopeType.APPLICATION);
    }
	
	
	/**
	   * List directory contents for a resource folder. Not recursive.
	   * This is basically a brute-force implementation.
	   * Works for regular files and also JARs.
	   * 
	   * @author Greg Briggs
	   * @param clazz Any java class that lives in the same place as the resources you want.
	   * @param path Should end with "/", but not start with one.
	   * @return Just the name of each member item, not the full paths.
	   * @throws URISyntaxException 
	   * @throws IOException 
	   */
	public static List<URL> getResourceListing(Class<? extends Object> clazz, String path,
			String pattern) throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL.getProtocol().equals("vfsfile")) {
			dirURL = new URL("file", dirURL.getHost(), dirURL.getFile());
		}
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
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
		 * In case of a jar file, we can't actually find a directory. Have
		 * to assume the same jar as clazz.
		 */
		String me = clazz.getName().replace(".", "/") + ".class";
		dirURL = clazz.getClassLoader().getResource(me);

		if (dirURL.getProtocol().equals("vfszip")) {
			dirURL = new URL("jar", dirURL.getHost(), "file:" + dirURL.getFile().replace(".jar/", ".jar!/"));
		}
		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5,
					dirURL.getPath().indexOf("!")); // strip out only the JAR
													// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries
															// in jar
			List<URL> result = new ArrayList<URL>(); // avoid duplicates in case
														// it is a subdirectory
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path) && !name.equals(path)
						&& name.matches(pattern)) { // filter according to the
													// path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory
						// name
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

}