package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.processo.entity.TipoRelacionamentoProcesso;
import br.com.infox.epp.relacionamentoprocessos.TipoRelacionamentoProcessoManager;

@Name(TipoRelacionamentoProcessoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoRelacionamentoProcessoCrudAction extends AbstractCrudAction<TipoRelacionamentoProcesso, TipoRelacionamentoProcessoManager> {
    public static final String NAME = "tipoRelacionamentoProcessoCrudAction";
    private static final long serialVersionUID = 1L;

}
