package br.com.infox.epp.usuario.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/usuario")
public interface UsuarioRest {

	public static final String JWT_TOKEN_NAME = "Authorization";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response adicionarUsuario(UsuarioDTO usuarioDTO);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response getUsuarios();

	@Path("/{cpf}")
	UsuarioResource getUsuarioResource(@PathParam("cpf") String cpf);

	@POST
	@Path("/signin")
	Response login(@HeaderParam(JWT_TOKEN_NAME) String jwt);

	// @Path("/{cpf}/perfil/{codigo}")
	// PerfilResource getPerfilResource(@PathParam("cpf") String cpf,
	// @PathParam("codigo") String codigo);

}
