package br.com.infox.epp.usuarioperfil;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import br.com.infox.epp.cdi.config.BeanManager;

public class UsuarioPerfilRestImpl implements UsuarioPerfilRest {

	@Context
	private UriInfo uriInfo;
	@Inject
	private UsuarioPerfilRestService usuarioPerfilRestService;
	
	@Override
	public List<UsuarioPerfilDTO> listar(String usuario) {
		return usuarioPerfilRestService.listar(usuario);
	}
	
	@Override
	public Response adicionar(UsuarioPerfilDTO usuarioPerfilDTO) {
		usuarioPerfilRestService.novo(usuarioPerfilDTO);
		URI location = uriInfo.getAbsolutePathBuilder().path(usuarioPerfilDTO.getUsuario()).path(usuarioPerfilDTO.getPerfil()).path(usuarioPerfilDTO.getLocalizacao()).build();
		return Response.created(location).build();
	}

	@Override
	public UsuarioPerfilResource getUsuarioPerfilResource(String usuario, String perfil, String localizacao) {
		UsuarioPerfilResourceImpl usuarioPerfilResourceImpl = BeanManager.INSTANCE.getReference(UsuarioPerfilResourceImpl.class);
		usuarioPerfilResourceImpl.setCpfUsuario(usuario);
		usuarioPerfilResourceImpl.setCodigoPerfil(perfil);
		usuarioPerfilResourceImpl.setCodigoLocalizacao(localizacao);
		
		return usuarioPerfilResourceImpl;
	}

	@Override
	public UsuarioPerfilResource getUsuarioPerfilResourceAlt(String usuario, String perfil, String localizacao) {
		return getUsuarioPerfilResource(usuario, perfil, localizacao);
	}


}
