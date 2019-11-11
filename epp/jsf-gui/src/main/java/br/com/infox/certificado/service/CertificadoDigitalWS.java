package br.com.infox.certificado.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;

@Path(CertificadoDigitalWS.PATH)
public class CertificadoDigitalWS {
	public static final String NAME = "certificadoDigitalWS";
	public static final String PATH = "/certificadodigital";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addSignatureInformation(CertificateSignatureBundleBean bundle) {
	    Lifecycle.beginCall();
		getCertificateSignatures().put(bundle.getToken(), bundle);
		Lifecycle.endCall();
		return Response.ok().build();
	}
	
	@GET
	@Path("{token}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSignatureInformation(@PathParam("token") String token) {
	    Lifecycle.beginCall();
		CertificateSignatureBundleBean bundle = getCertificateSignatures().get(token);
		CertificateSignatureBundleStatus status;
		if (bundle != null) {
			status = bundle.getStatus();
		} else {
			status = CertificateSignatureBundleStatus.UNKNOWN;
		}
		Lifecycle.endCall();
		return Response.ok(status.name()).build();
	}

	
    public CertificateSignatures getCertificateSignatures() {
        return (CertificateSignatures) Component.getInstance(CertificateSignatures.NAME);
    }
    
}
