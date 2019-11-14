package br.com.infox.epp.system.crud;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;

@Name(ParametroCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ParametroCrudAction extends AbstractCrudAction<Parametro, ParametroManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "parametroCrudAction";

    @Override
    protected boolean isInstanceValid() {
        getInstance().setUsuarioModificacao(Authenticator.getUsuarioLogado());
        getInstance().setDataAtualizacao(new Date());
        return super.isInstanceValid();
    }

    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        if (PERSISTED.equals(ret) || UPDATED.equals(ret)) {
            Contexts.getApplicationContext().set(getInstance().getNomeVariavel(), getInstance().getValorVariavel());
        }
    }
}
