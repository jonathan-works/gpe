package br.com.infox.epp.processo.partes.manager;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.partes.dao.HistoricoParticipanteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@AutoCreate
@Name(HistoricoParticipanteProcessoManager.NAME)
@Stateless
public class HistoricoParticipanteProcessoManager extends Manager<HistoricoParticipanteProcessoDAO, HistoricoParticipanteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParticipanteProcessoManager";

    public void createHistorico(ParticipanteProcesso participante, String motivoModificacao) throws DAOException {
        HistoricoParticipanteProcesso historicoParticipanteProcesso = new HistoricoParticipanteProcesso();
        historicoParticipanteProcesso.setParticipanteModificado(participante);
        historicoParticipanteProcesso.setAtivo(participante.getAtivo());
        historicoParticipanteProcesso.setMotivoModificacao(motivoModificacao);
        persist(historicoParticipanteProcesso);
    }

    public List<HistoricoParticipanteProcesso> listByParticipanteProcesso(ParticipanteProcesso pp) {
        return getDao().listByParticipanteProcesso(pp);
    }
    
    public boolean hasHistoricoParticipante(ParticipanteProcesso pp) {
        return getDao().hasHistoricoParticipante(pp);
    }
}
