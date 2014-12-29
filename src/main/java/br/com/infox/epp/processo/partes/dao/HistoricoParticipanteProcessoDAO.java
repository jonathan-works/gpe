package br.com.infox.epp.processo.partes.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.*;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(HistoricoParticipanteProcessoDAO.NAME)
public class HistoricoParticipanteProcessoDAO extends DAO<HistoricoParticipanteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParticipanteProcessoDAO";
    
    public List<HistoricoParticipanteProcesso> listByParticipanteProcesso(ParticipanteProcesso pp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PARTICIPANTE_PROCESSO, pp);
        return getNamedResultList(LIST_BY_PARTICIPANTE_PROCESSO, params);
    }

    public boolean hasHistoricoParticipante(ParticipanteProcesso pp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PARTICIPANTE_PROCESSO, pp);
        return (long) getNamedSingleResult(HAS_HISTORICO_BY_PARTICIPANTE, params) > 0;
    }
}
