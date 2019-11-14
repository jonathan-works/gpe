package br.com.infox.epp.usuario.login.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(LoginUsuarioRest.BASE_PATH)
public interface LoginUsuarioRest {
	
	public static final String BASE_PATH = "/loginUsuario";

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	Response login(@HeaderParam("token") String token, LoginUsuarioDTO loginUsuario);

}
