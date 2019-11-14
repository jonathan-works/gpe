package br.com.infox.epp.access.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class RecursoCrudAction extends AbstractCrudAction<Recurso, RecursoManager> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private RecursoManager recursoManager;

    public void setRecurso(String identificador) {
        setInstance(getManager().getRecursoByIdentificador(identificador));
        setTab(TAB_FORM);
    }

    @Override
    protected RecursoManager getManager() {
        return recursoManager;
    }
}
