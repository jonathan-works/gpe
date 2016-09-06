package br.com.infox.epp.usuario.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;

@Path("/usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UsuarioRest {

	public static final String JWT_TOKEN_NAME = "Authorization";
	final String PATH_GRAVAR_USUARIO = "/gravar";
    final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";

	@POST
	Response adicionarUsuario(@Context UriInfo uriInfo, UsuarioDTO usuarioDTO);

	@GET
	Response getUsuarios(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset);

	@Path("/{cpf}")
	UsuarioResource getUsuarioResource(@PathParam("cpf") String cpf);

	@POST
	@Path("/signin")
	Response login(@HeaderParam(JWT_TOKEN_NAME) String jwt);

	@GET
	@Path("/signin")
	Response loginGet(@QueryParam("epp.auth.jwt") String jwt);
	
	//TODO serviço criado pro tcmba, movido para essa classe pela urgência do bug #74700. Avaliar no futuro padronização desse serviço
    @POST
    @Path(PATH_GRAVAR_USUARIO)
    @Produces(MediaType.TEXT_PLAIN)
    public String gravarUsuario(@HeaderParam("token") String token, UsuarioBean bean) throws DAOException;
    
    @POST
    @Path(PATH_ATUALIZAR_SENHA)
    @Produces(MediaType.TEXT_PLAIN)
    public String atualizarSenha(@HeaderParam("token") String token, UsuarioSenhaBean bean) throws DAOException;
}
