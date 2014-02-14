package br.com.infox.epp.processo.partes.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.processo.partes.entity.HistoricoParteProcesso;
import br.com.infox.epp.processo.partes.manager.HistoricoParteProcessoManager;

@Name(HistoricoParteProcessoCrudAction.NAME)
public class HistoricoParteProcessoCrudAction extends AbstractCrudAction<HistoricoParteProcesso, HistoricoParteProcessoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "historicoParteProcessoCrudAction";

}
