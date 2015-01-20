package br.com.infox.certificado;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.certificado.bean.CertificateSignatureBundleBean;

@Name(CertificateSignatures.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class CertificateSignatures implements Serializable {
	public static final String NAME = "certificateSignatures";
	private static final long serialVersionUID = 1L;

	private Map<String, CertificateSignatureBundleBean> signatures = new ConcurrentHashMap<>();
	
	public CertificateSignatureBundleBean get(String token) {
		return signatures.get(token);
	}
	
	public void put(String token, CertificateSignatureBundleBean bundle) {
		signatures.put(token, bundle);
	}
}
