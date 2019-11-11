package br.com.infox.epp.assinador;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class TrustStoreService {

	@Inject
	private TrustStoreProvider trustStoreProvider;

	private static KeyStore keyStore;

	public KeyStore getTrustStore() {
		if (keyStore == null) {
			try {
				try {
					keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
					keyStore.load(null, null);
				} catch (KeyStoreException e) {
					throw new RuntimeException("Erro ao iniciar trustStore", e);
				}
				Collection<X509Certificate> certificadosConfiaveis = trustStoreProvider
						.carregarCertificadosConfiaveis();
				Iterator<X509Certificate> it = certificadosConfiaveis.iterator();
				while (it.hasNext()) {
					X509Certificate certificadoConfiavel = it.next();
					try {
						keyStore.setCertificateEntry(certificadoConfiavel.getSubjectDN().getName(),
								certificadoConfiavel);
					} catch (KeyStoreException e) {
						keyStore = null;
						throw new RuntimeException(e);
					}
				}
			} catch (Exception e) {
				keyStore = null;
				throw new RuntimeException(e);
			}
		}
		return keyStore;
	}

}
