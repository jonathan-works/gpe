package br.com.infox.epp.assinador.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface DocumentoResource {
	
	
	@Path("md5")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getMD5Hex();
	
	@Path("md5")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] getMD5();
	
	@Path("sha1")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSHA1Hex();
	
	@Path("sha1")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] getSHA1();
	
	@Path("sha256")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSHA256Hex();
	
	@Path("sha256")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] getSHA256();
	
	@Path("cms")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@POST
	public void setAssinaturaCms(byte[] assinatura);
	
	@Path("pkcs7")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@POST
	public void setAssinaturaPkcs7(byte[] assinatura);

}
