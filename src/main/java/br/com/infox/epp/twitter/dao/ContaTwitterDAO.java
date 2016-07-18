package br.com.infox.epp.twitter.dao;

import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_ID_USUARIO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_LOCALIZACAO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.CONTA_TWITTER_BY_USUARIO;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.ID_GRUPO_EMAIL_PARAM;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.ID_USUARIO_PARAM;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.LIST_TWITTER_BY_ID_GRUPO_EMAIL;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.LOCALIZACAO_PARAM;
import static br.com.infox.epp.twitter.query.ContaTwitterQuery.USUARIO_PARAM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.twitter.entity.ContaTwitter;

@Stateless
@AutoCreate
@Name(ContaTwitterDAO.NAME)
public class ContaTwitterDAO extends DAO<ContaTwitter> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "contaTwitterDAO";

    public ContaTwitter getContaTwitterByLocalizacao(Localizacao localizacao) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LOCALIZACAO_PARAM, localizacao);
        return getNamedSingleResult(CONTA_TWITTER_BY_LOCALIZACAO, parameters);
    }

    public ContaTwitter getContaTwitterByUsuario(UsuarioLogin usuario) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(USUARIO_PARAM, usuario);
        return getNamedSingleResult(CONTA_TWITTER_BY_USUARIO, parameters);
    }

    public ContaTwitter getContaTwitterByIdUsuario(Integer idUsuario) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_USUARIO_PARAM, idUsuario);
        return getNamedSingleResult(CONTA_TWITTER_BY_ID_USUARIO, parameters);
    }

    public List<ContaTwitter> listaContasTwitter(int idGrupoEmail) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_GRUPO_EMAIL_PARAM, idGrupoEmail);
        return getNamedResultList(LIST_TWITTER_BY_ID_GRUPO_EMAIL, parameters);
    }

}
