package br.com.infox.epp.access.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.seam.security.operation.UpdateResourcesOperation;

@Name(RecursoPapelController.NAME)
@Scope(ScopeType.CONVERSATION)
public class RecursoPapelController {

    public static final String NAME = "recursoPapelController";

    private Recurso recurso;

    private List<String> papeis;
    private List<String> papeisDisponiveis;

    @In
    private RecursoManager recursoManager;
    @In
    private PapelManager papelManager;

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
        papeis = getPapeisAssociadosARecurso();
        papeisDisponiveis = papelManager.getListaDeNomesDosPapeis();
    }

    public List<String> getPapeis() {
        return papeis;
    }

    public void setPapeis(List<String> papeis) {
        this.papeis = papeis;
    }

    public List<String> getPapeisDisponiveis() {
        return papeisDisponiveis;
    }

    public void setPapeisDisponiveis(List<String> papeisDisponiveis) {
        this.papeisDisponiveis = papeisDisponiveis;
    }

    private List<String> getPapeisAssociadosARecurso() {
        return papeis = recursoManager.getPapeisAssociadosARecurso(recurso);
    }

    public String save() {
        final UpdateResourcesOperation resourcesOperation = new UpdateResourcesOperation(recurso.getIdentificador(), getPapeis(), getPapeisDisponiveis());
        resourcesOperation.run();
        recursoManager.flush();
        return "updated";
    }

}
