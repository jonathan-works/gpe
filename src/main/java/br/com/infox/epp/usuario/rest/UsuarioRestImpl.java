package br.com.infox.epp.usuario.rest;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

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
			Matcher matcher = Pattern.compile("Bearer\\s*<(.+)>").matcher(jwt);
			if (matcher.find()){
				return Response.seeOther(new URI(loginRestService.login(matcher.group(1)))).build();
			}
			JsonObject obj = new JsonObject();
			obj.addProperty("message","Authorization token not found");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(new Gson().toJson(obj)).build());
		} catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			throw new WebApplicationException(e, Status.UNAUTHORIZED);
		} catch (Exception e){
			throw new WebApplicationException(e, Response.status(Status.BAD_REQUEST).build());
		}
	}

}
