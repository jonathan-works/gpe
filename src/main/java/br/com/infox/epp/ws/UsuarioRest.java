package br.com.infox.epp.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioBean;
import br.com.infox.epp.ws.bean.UsuarioSenhaBean;

@Path(UsuarioRest.PATH)
public interface UsuarioRest {
    
    final String PATH = "/usuario";
    final String PATH_GRAVAR_USUARIO = "/gravar";
    final String PATH_ATUALIZAR_SENHA = "/atualizarSenha";
    
    @POST
    @Path(PATH_GRAVAR_USUARIO)
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String gravarUsuario(@HeaderParam("token") String token, UsuarioBean bean) throws DAOException;
    
    @POST
    @Path(PATH_ATUALIZAR_SENHA)
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String atualizarSenha(@HeaderParam("token") String token, UsuarioSenhaBean bean) throws DAOException;

}
