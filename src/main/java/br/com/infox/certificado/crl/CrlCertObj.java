package br.com.infox.certificado.crl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.itx.component.UrlUtil;
import br.com.itx.util.FileUtil;


public class CrlCertObj {

	private static final LogProvider LOG = Logging.getLogProvider(CrlCertObj.class);
	
	private List<String> urls;
	private String url;
	private String id;

	private CertificateFactory certificatefactory;
	private X509CRL x509crl;

	public CrlCertObj(String id, List<String> urls) {
		this.id = id;
		try {
			certificatefactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			throw new RuntimeException("Erro ao obter o factory X.509", e);
		}		
		this.urls = urls;
	}
	
	public Date getDataExpiracao() {
		return x509crl != null ? x509crl.getNextUpdate() : null;
	}
 	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urls == null) ? 0 : urls.hashCode());
		return result;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CrlCertObj other = (CrlCertObj) obj;
		if (urls == null) {
			if (other.urls != null) {
				return false;
			}
		} else if (!urls.equals(other.urls)) {
			return false;
		}
		return true;
	}

	public synchronized boolean isCertificadoRevogado(Certificate cert) throws CrlCheckException {		
		if (x509crl == null) {
			atualizarX509crl();
		}
		return x509crl.isRevoked(cert);
		
	}

	public synchronized void atualizarX509crl() throws CrlCheckException {
		File fileTmp = null;
		url = getUrlValida();
		if (url != null) {
			InputStream inputStream = null;
			try {
				fileTmp = downloadTempFile(getUrlValida());
				inputStream = new FileInputStream(fileTmp);


				x509crl = (X509CRL) certificatefactory.generateCRL(inputStream);			

				LOG.info("Ceritificadora: " + x509crl.getIssuerDN() + " /  Arquivo: " + fileTmp.getAbsoluteFile());

				
				fileTmp.delete();

			} catch (Exception e) {
				LOG.info("Erro ao atualizar o X509crl: " + e.getMessage());
				if (fileTmp != null) {
					fileTmp.delete();
				}
				throw new CrlCheckException(e);
			} finally {
				FileUtil.close(inputStream);
			}
		}
	}
	
	public boolean isExpirado() {
		//Precisa prevenir pois o metodo de validação pode ser chamada logo apois o objeto ter sido criadoe neste momento 
		//o arquivo do crl não foi baixado ainda e o campo vai estar null.
		return x509crl != null && (new Date()).after(x509crl.getNextUpdate());
	}
	
	private String getUrlValida() throws CrlCheckException {
		StringBuilder sb = new StringBuilder();
		InputStream inputStreamURL = null;
		for (String url : urls) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(url);
			try {
				inputStreamURL = UrlUtil.getInputStreamUrl(url);
			} catch (IOException e) {
				LOG.warn("Erro ao obter o link do CRL '" + url + "': " + e.getMessage());
			}
			if (inputStreamURL != null) {
				return url;
			}
		}
		throw new CrlCheckException("Não foi possivél se conectar com nenhuma das" +
				" crls listads pelo certificado: " + sb.toString());
	}
	
	private File downloadTempFile(String url) throws IOException {
		InputStream inputStream = UrlUtil.getInputStreamUrl(url);
		String fileName = getFileName(url);
		File f = File.createTempFile(fileName, ".tmp");
		OutputStream stream = null;
		OutputStream os = null; 
		int size = 0;
		try {
			stream = new FileOutputStream(f);
			os = new BufferedOutputStream(stream);
			byte[] buf = new byte[1024];
			int byteRead;
			while ((byteRead = inputStream.read(buf)) != -1) {
				os.write(buf, 0, byteRead);
				size += byteRead;
			}
		} finally {
			FileUtil.close(os);
			FileUtil.close(inputStream);
			if (os != null) {
				os.flush();
			}
		}
        LOG.info(MessageFormat.format(
						"O CRL do link ''{0}'' foi baixado para o arquivo ''{1}'' [{2} bytes]",
						url, fileName, size));
        return f;
	}

	private String getFileName(String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}
	
}