package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.DELETE_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA_ALVO_TIPO_RESTRICAO;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.PARAM_ALVO;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.PARAM_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.PARAM_TIPO_RESTRICAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;

@Stateless
@AutoCreate
@Name(PastaRestricaoDAO.NAME)
public class PastaRestricaoDAO extends DAO<PastaRestricao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaRestricaoDAO";
    
    public List<PastaRestricao> getByPasta(Pasta pasta) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PASTA, pasta);
        return getNamedResultList(GET_BY_PASTA, params);
    }
    
    public PastaRestricao getByPastaAlvoTipoRestricao(Pasta pasta, Integer alvo, PastaRestricaoEnum tipoRestricao) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PASTA, pasta);
        params.put(PARAM_ALVO, alvo);
        params.put(PARAM_TIPO_RESTRICAO, tipoRestricao);
        return getNamedSingleResult(GET_BY_PASTA_ALVO_TIPO_RESTRICAO, params);
    }
    
    public void deleteByPasta(Pasta pasta) throws DAOException {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_PASTA, pasta);
        executeNamedQueryUpdate(DELETE_BY_PASTA, params);
    }
}
