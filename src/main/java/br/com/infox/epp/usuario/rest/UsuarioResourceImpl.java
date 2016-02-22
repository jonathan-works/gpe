package br.com.infox.epp.usuario.rest;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class UsuarioResourceImpl implements UsuarioResource {

	private String cpf;
	@Inject
	private UsuarioLoginRestService usuarioRestService;

	@Override
	public Response atualizarUsuario(UsuarioDTO usuarioDTO) {
		usuarioRestService.atualizarUsuario(cpf, usuarioDTO);
		return Response.ok().build();
	}

	@Override
	public Response getUsuario() {
		UsuarioDTO usuarioDTO = usuarioRestService.getUsuarioByCpf(cpf);
		return Response.ok(usuarioDTO).build();
	}

	@Override
	public Response removerUsuario() {
		usuarioRestService.removerUsuario(cpf);
		return Response.ok().build();
	}

	@Override
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

}
