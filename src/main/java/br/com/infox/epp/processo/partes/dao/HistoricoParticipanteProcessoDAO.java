package br.com.infox.epp.processo.partes.dao;

import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.HAS_HISTORICO_BY_PARTICIPANTE;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.LIST_BY_PARTICIPANTE_PROCESSO;
import static br.com.infox.epp.processo.partes.query.HistoricoParticipanteProcessoQuery.PARAM_PARTICIPANTE_PROCESSO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@Stateless
@AutoCreate
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
