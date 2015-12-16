package br.com.infox.epp.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.ws.bean.UsuarioPerfilBean;

@Path(PerfilRest.PATH)
public interface PerfilRest {
    
    public static final String PATH = "/perfil";
    public static final String PATH_ADICIONAR_PERFIL = "/adicionar";
    
    @POST
    @Path(PATH_ADICIONAR_PERFIL)
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String adicionarPerfil(@HeaderParam("token") String token, UsuarioPerfilBean bean) throws DAOException;

}
