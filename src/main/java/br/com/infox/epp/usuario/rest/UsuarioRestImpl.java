package br.com.infox.epp.usuario.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class UsuarioRestImpl implements UsuarioRest {

	@Inject
	private UsuarioLoginRestService usuarioRestService;
	@Inject
	private UsuarioResource usuarioResource;
	
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

}
