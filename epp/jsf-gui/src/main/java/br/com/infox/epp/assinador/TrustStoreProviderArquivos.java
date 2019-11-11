package br.com.infox.epp.assinador;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import br.com.infox.epp.access.manager.CertificateManager;

public class TrustStoreProviderArquivos implements TrustStoreProvider {
	
	@Inject
	private Logger logger;
	
	@Override
	public Collection<X509Certificate> carregarCertificadosConfiaveis() {
        CertificateFactory certFactory;
		try {
			certFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}

        List<URL> files = new ArrayList<URL>();
        try {
			files = CertificateManager.getResourceListing(CertificateManager.class, "certificados/", "(.*crt$|.*cer$)");
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
        
        List<X509Certificate> retorno = new ArrayList<>();

        //InputStream is = null;
        for (URL fileCert : files) {
            try(InputStream is = fileCert.openStream()) {
                X509Certificate x509Certificate = (X509Certificate) certFactory.generateCertificate(is);
                retorno.add(x509Certificate);
            } catch (IOException | CertificateException e) {
            	String mensagem = "Erro ao carregar truststore dos arquivos";
            	logger.log(Level.SEVERE,  mensagem, e);
            } 
        }
        
        return retorno;
	}

}
