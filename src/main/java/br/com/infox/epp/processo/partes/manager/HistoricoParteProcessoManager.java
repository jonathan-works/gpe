package br.com.infox.epp.processo.partes.manager;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.partes.dao.HistoricoParteProcessoDAO;
import br.com.infox.epp.processo.partes.entity.HistoricoParteProcesso;

@Name(HistoricoParteProcessoManager.NAME)
public class HistoricoParteProcessoManager extends Manager<HistoricoParteProcessoDAO, HistoricoParteProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParteProcessoManager";

}
