package br.com.infox.certificado;

import java.security.cert.X509Certificate;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.crl.CrlCheckControler;
import br.com.infox.certificado.crl.CrlCheckException;
import br.com.infox.certificado.crl.jobs.LoginCrlCertTestJobActionListner;
import br.com.infox.certificado.util.DigitalSignatureUtils;

public final class VerificaCertificado {
    
    private VerificaCertificado(){
        super();
    }
	
	private static final LogProvider LOG = Logging.getLogProvider(VerificaCertificado.class);
	
	public static void verificaValidadeCertificado(String certChainBase64Encoded) throws CertificadoException {
		X509Certificate[] x509Certificates = DigitalSignatureUtils.loadCertFromBase64String(certChainBase64Encoded);
		verificaValidadeCertificado(x509Certificates);
	}
	
	public static void verificaValidadeCertificado(X509Certificate[] x509Certificates) throws CertificadoException {
		try {
			CertificadosCaCheckManager instance = CertificadosCaCheckManager.instance();
			if (instance != null) {
				instance.verificaCertificado(x509Certificates);
			}
		} catch (Exception e) {
			throw new CertificadoException("Erro ao válidar certificado: " + e.getMessage(), e);
		} 
	}	
	
	public static void verificaRevogacaoCertificado(String certChainBase64Encoded) throws CertificadoException {
		try {
			Certificado c = new Certificado(certChainBase64Encoded);
			try {
				boolean certificadoRevogado = CrlCheckControler.instance().isCertificadoRevogado(c, new LoginCrlCertTestJobActionListner());
				if (certificadoRevogado) {
					throw new CertificadoException("Certificado revogado");
				}
			} catch (CrlCheckException e) {
				LOG.warn("Erro ao verificar Crl: " + e.getMessage());
			}
		} catch (Exception e) {
			throw new CertificadoException("Erro ao válidar certificado: " + e.getMessage(), e);
		} 
	}		
	

}