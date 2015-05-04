package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.GET_BY_PASTA;
import static br.com.infox.epp.processo.documento.query.PastaRestricaoQuery.PARAM_PASTA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;

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
}
