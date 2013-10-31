package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.entity.ProcessoEpaTarefa;
import br.com.infox.epp.list.ProcessoEpaTarefaList;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(BamHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class BamHome extends AbstractHome<ProcessoEpaTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "bamHome";
    
    @Override
    public void onClickSearchTab() {
        super.onClickSearchTab();
        ProcessoEpaTarefaList instance = ComponentUtil.getComponent(ProcessoEpaTarefaList.NAME);
        instance.newInstance(); 
    }
    
}
