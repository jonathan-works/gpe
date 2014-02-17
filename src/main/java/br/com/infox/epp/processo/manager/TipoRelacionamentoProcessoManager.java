package br.com.infox.epp.processo.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.dao.TipoRelacionamentoProcessoDAO;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;

@AutoCreate
@Name(TipoRelacionamentoProcessoManager.NAME)
public class TipoRelacionamentoProcessoManager extends Manager<TipoRelacionamentoProcessoDAO, TipoRelacionamentoProcesso> {

    public static final String NAME = "tipoRelacionamentoProcessoManager";
    private static final long serialVersionUID = 1L;

}
