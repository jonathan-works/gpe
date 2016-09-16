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

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

    @Override
    public Response getSituacaoTermoAdesao() {
        Object entity;
        if (Boolean.TRUE.equals(usuarioRestService.getAssinouTermoAdesao(cpf))){
            entity = "ASSINADO";
        } else {
            entity = "NAO_ASSINADO";
        }
        return Response.ok(entity).build();
    }

}
