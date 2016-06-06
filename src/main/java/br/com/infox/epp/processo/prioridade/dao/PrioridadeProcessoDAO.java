package br.com.infox.epp.processo.prioridade.dao;

import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.query.PrioridadeProcessoQuery;

@Stateless
@AutoCreate
@Name(PrioridadeProcessoDAO.NAME)
public class PrioridadeProcessoDAO extends DAO<PrioridadeProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "prioridadeProcessoDAO";
    
    public List<PrioridadeProcesso> listPrioridadesAtivas() {
        return getNamedResultList(PrioridadeProcessoQuery.NAMED_QUERY_PRIORIDADES_ATIVAS);
    }
}
