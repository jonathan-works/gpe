package br.com.infox.epp.usuarioperfil;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UsuarioPerfilResource {
	
	@DELETE
	Response remover();

	@GET
	UsuarioPerfilDTO getUsuarioPerfil();
	
	@PUT
	Response atualizar(UsuarioPerfilDTO usuarioPerfilDTO);
	
}
