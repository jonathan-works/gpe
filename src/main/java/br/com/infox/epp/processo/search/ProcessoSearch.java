package br.com.infox.epp.processo.search;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;

import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoSearch.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoSearch {
    
    public static final String NAME = "processoSearch";
    
    /**
     * Método redireciona para visualização do processo escolhido no paginador
     * 
     * @param processo Processo a ser visualizado no paginador
     */
    public void visualizarProcesso(Processo processo) {
        Redirect.instance().setConversationPropagationEnabled(false);
        Redirect.instance().setViewId("/Processo/Consulta/list.xhtml");
        Redirect.instance().setParameter("id", processo.getIdProcesso());
        Redirect.instance().setParameter("idJbpm", processo.getIdJbpm());
        Redirect.instance().execute();
    }

}
