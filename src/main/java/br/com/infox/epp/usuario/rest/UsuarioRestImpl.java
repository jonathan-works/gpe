package br.com.infox.epp.usuario.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;
import br.com.infox.epp.ws.services.UsuarioRestService;

public class UsuarioRestImpl implements UsuarioRest {

	@Inject
	private UsuarioLoginRestService usuarioLoginRestService;
	@Inject
	private UsuarioResourceImpl usuarioResource;
	@Inject
	private LoginRestService loginRestService;
	//Servico movido devido ao bug #74700
	@Inject
	private UsuarioRestService usuarioRestService;

	@Override
	public Response adicionarUsuario(UriInfo uriInfo, UsuarioDTO usuarioDTO) {
		usuarioLoginRestService.adicionarUsuario(usuarioDTO);
		URI location = uriInfo.getAbsolutePathBuilder().path(usuarioDTO.getCpf()).build();
        return Response.created(location).build();
	}

	public Response getUsuarios(Integer limit, Integer offset) {
	    if (limit==null){
	        limit = 15;
	    }
	    if (offset == null){
	        offset = 0;
	    }
		return Response.ok(usuarioLoginRestService.getUsuarios(limit, offset)).build();
	}

	@Override
	public UsuarioResource getUsuarioResource(String cpf) {
		usuarioResource.setCpf(cpf);
		return usuarioResource;
	}

	@Override
	public Response login(String jwt) {
		try {
			Matcher matcher = Pattern.compile("Bearer\\s*(.+)").matcher(jwt);
			if (matcher.find()) {
				return Response.seeOther(new URI(loginRestService.login(matcher.group(1)))).build();
			}
			JsonObject obj = new JsonObject();
			obj.addProperty("message", "Authorization token not found");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(new Gson().toJson(obj)).build());
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.UNAUTHORIZED);
		}
	}

    @Override
    public Response loginGet(String jwt) {
        try {
            return Response.seeOther(new URI(loginRestService.loginWithRSA(jwt))).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e, Status.UNAUTHORIZED);
        }
    }

    @Override
	public String gravarUsuario(String token, UsuarioBean bean) throws DAOException {
		return usuarioRestService.gravarUsuario(bean);
	}
	
    @Override
	public String atualizarSenha(String token, UsuarioSenhaBean bean) throws DAOException {
		return usuarioRestService.atualizarSenha(bean);
	}

}
