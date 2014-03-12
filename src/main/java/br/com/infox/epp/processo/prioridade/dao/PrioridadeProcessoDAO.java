package br.com.infox.epp.processo.prioridade.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;

@Name(PrioridadeProcessoDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PrioridadeProcessoDAO extends DAO<PrioridadeProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "prioridadeProcessoDAO";
}
