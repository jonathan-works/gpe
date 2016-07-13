package br.com.infox.epp.usuario.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UsuarioRestImpl implements UsuarioRest {

	@Inject
	private UsuarioLoginRestService usuarioRestService;
	@Inject
	private UsuarioResource usuarioResource;
	@Inject
	private LoginRestService loginRestService;

	@Override
	public Response adicionarUsuario(UsuarioDTO usuarioDTO) {
		usuarioRestService.adicionarUsuario(usuarioDTO);
		return Response.ok().build();
	}

	@Override
	public Response getUsuarios() {
		return Response.ok(usuarioRestService.getUsuarios()).build();
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

}
