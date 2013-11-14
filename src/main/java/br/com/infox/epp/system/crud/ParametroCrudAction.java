package br.com.infox.epp.system.crud;

import java.util.Date;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.system.entity.Parametro;

@Name(ParametroCrudAction.NAME)
public class ParametroCrudAction extends AbstractCrudAction<Parametro> {
    
    public static final String NAME = "parametroCrudAction";
    
    @Override
    protected boolean beforeSave() {
        getInstance().setUsuarioModificacao(Authenticator.getUsuarioLogado());
        getInstance().setDataAtualizacao(new Date());
        return super.beforeSave();
    }

}
