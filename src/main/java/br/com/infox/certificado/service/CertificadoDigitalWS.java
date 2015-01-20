package br.com.infox.certificado.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.bean.CertificateSignatureInformation;

@Path(CertificadoDigitalWS.PATH)
@Name(CertificadoDigitalWS.NAME)
@Scope(ScopeType.STATELESS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CertificadoDigitalWS {
	public static final String NAME = "certificadoDigitalWS";
	public static final String PATH = "/certificadodigital";

	@In
	private CertificateSignatures certificateSignatures;
	
	@POST
	public Response addSignatureInformation(CertificateSignatureBundleBean bundle) {
		certificateSignatures.put(bundle.getToken(), bundle);
		return Response.ok().build();
	}
	
	@GET
	@Path("{token}")
	public Response getSignatureInformation(@PathParam("token") String token) {
		CertificateSignatureInformation information = new CertificateSignatureInformation();
		CertificateSignatureBundleBean bundle = certificateSignatures.get(token);
		if (bundle != null) {
			information.setMessage(bundle.getMessage());
			information.setStatus(bundle.getStatus());
		} else {
			information.setStatus(CertificateSignatureBundleStatus.UNKNOWN);
		}
		return Response.ok(information).build();
	}
}
