package br.com.infox.epp.assinador;

import java.security.cert.X509Certificate;
import java.util.Collection;

public interface TrustStoreProvider {

	public Collection<X509Certificate> carregarCertificadosConfiaveis();
	
}
