package br.com.infox.epp.usuarioperfil;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import br.com.infox.epp.ws.interceptors.Log;

@Log
public class UsuarioPerfilResourceImpl implements UsuarioPerfilResource {

	private String cpfUsuario;
	private String codigoPerfil;
	private String codigoLocalizacao;
	
	@Inject
	private UsuarioPerfilRestService usuarioPerfilRestService;
	
	public void setCpfUsuario(String cpfUsuario) {
		this.cpfUsuario = cpfUsuario;
	}
	public void setCodigoPerfil(String codigoPerfil) {
		this.codigoPerfil = codigoPerfil;
	}
	public void setCodigoLocalizacao(String codigoLocalizacao) {
		this.codigoLocalizacao = codigoLocalizacao;
	}
	@Override
	public Response remover() {
		usuarioPerfilRestService.delete(cpfUsuario, codigoPerfil, codigoLocalizacao);;		
		return Response.noContent().build();
	}
	@Override
	public UsuarioPerfilDTO getUsuarioPerfil() {
		return usuarioPerfilRestService.find(cpfUsuario, codigoPerfil, codigoLocalizacao);
	}
	@Override
	public Response atualizar(UsuarioPerfilDTO usuarioPerfilDTO) {
		usuarioPerfilRestService.atualizar(cpfUsuario, codigoPerfil, codigoLocalizacao, usuarioPerfilDTO);
		return Response.noContent().build();
	}
	
}
