package br.com.infox.epp.tarefa.component.combo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.tarefa.type.PrazoEnum;

@Name(PrazoCombo.NAME)
@Scope(ScopeType.EVENT)
public class PrazoCombo {

    public static final String NAME = "prazoCombo";

    public PrazoEnum[] getTipoPrazoList() {
        return PrazoEnum.values();
    }

}
