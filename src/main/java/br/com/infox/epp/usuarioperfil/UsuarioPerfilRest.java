package br.com.infox.epp.usuarioperfil;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("usuarioPerfil")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UsuarioPerfilRest {
	
	@GET
	public List<UsuarioPerfilDTO> listar(@QueryParam("usuario") String usuario);
	
	@POST
	public Response adicionar(UsuarioPerfilDTO usuarioPerfilDTO);
	
	@Path("/{usuario},{perfil},{localizacao}")
	UsuarioPerfilResource getUsuarioPerfilResourceAlt(@PathParam("usuario") String usuario, @PathParam("perfil") String perfil, @PathParam("localizacao") String localizacao);
	
	@Path("/{usuario}/{perfil}/{localizacao}")
	UsuarioPerfilResource getUsuarioPerfilResource(@PathParam("usuario") String usuario, @PathParam("perfil") String perfil, @PathParam("localizacao") String localizacao);
	
}
