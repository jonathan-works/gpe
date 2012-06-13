package br.com.infox.core.certificado;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("certificadoLogHome")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class CertificadoLogHome implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "certificadoLogHome";

	public String getTextLogFile() {
		try {
			return CertificadoLog.getTextLogFile();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}