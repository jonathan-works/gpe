package br.com.infox.certificado.service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.seam.path.PathResolver;

@Name(CertificadoDigitalPathResolver.NAME)
@AutoCreate
@Scope(ScopeType.EVENT)
public class CertificadoDigitalPathResolver {
	public static final String NAME = "certificadoDigitalPathResolver";
	
	@In
	private PathResolver pathResolver;
	
	public String getJNLPUrl() {
		return pathResolver.getUrlProject() + CertificadoDigitalJNLPServlet.SERVLET_PATH;
	}
	
	public String getCertificadoWSBasePath() {
		return pathResolver.getUrlProject() + "/rest" + CertificadoDigitalWS.PATH;
	}
}
