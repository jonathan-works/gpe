package br.com.infox.ibpm.variable.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.ibpm.variable.VariableAccessHandler;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.manager.DominioVariavelTarefaManager;

@Name(DominioVariavelTarefaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DominioVariavelTarefaCrudAction extends AbstractCrudAction<DominioVariavelTarefa, DominioVariavelTarefaManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "dominioVariavelTarefaCrudAction";

    private VariableAccessHandler currentVariable;

    public void setCurrentVariable(VariableAccessHandler currentVariable) {
        this.currentVariable = currentVariable;
        if (this.currentVariable.getDominioVariavelTarefa() != null) {
            setInstance(this.currentVariable.getDominioVariavelTarefa());
            setTab("form");
        }
    }

    public void selecionarDominio() {
        this.currentVariable.setDominioVariavelTarefa(getInstance());
        reset();
    }

    public void reset() {
        newInstance();
        setTab("search");
    }
    
}
