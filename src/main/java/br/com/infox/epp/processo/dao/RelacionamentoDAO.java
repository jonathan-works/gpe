package br.com.infox.epp.processo.dao;

import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.RelacionamentoProcessoQuery.RELACIONAMENTO_BY_PROCESSO;

import java.util.HashMap;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.entity.Relacionamento;

@Name(RelacionamentoDAO.NAME)
public class RelacionamentoDAO extends DAO<Relacionamento> {

    public static final String NAME = "relacionamentoDAO";
    private static final long serialVersionUID = 1L;

    public Relacionamento getRelacionamentoByProcesso(final ProcessoEpa processo) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ID_PROCESSO, processo);
        return getNamedSingleResult(RELACIONAMENTO_BY_PROCESSO, parameters);
    }
    
}
