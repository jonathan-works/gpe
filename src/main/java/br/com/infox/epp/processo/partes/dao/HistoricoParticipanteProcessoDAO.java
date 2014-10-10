package br.com.infox.epp.processo.partes.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParticipanteProcesso;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(HistoricoParticipanteProcessoDAO.NAME)
public class HistoricoParticipanteProcessoDAO extends DAO<HistoricoParticipanteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParticipanteProcessoDAO";
}
