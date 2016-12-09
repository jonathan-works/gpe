package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.seam.security.operation.UpdateResourcesOperation;

@Scope(ScopeType.CONVERSATION)
@Name(RecursoPapelController.NAME)
public class RecursoPapelController implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "recursoPapelController";

    private Recurso recurso;
    private List<String> papeis;
    private List<String> papeisDisponiveis;

    @In
    private RecursoManager recursoManager;
    @In
    private PapelManager papelManager;
    @In
    private InfoxMessages infoxMessages;

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
        return recursoManager.getPapeisAssociadosARecurso(recurso);
    }

    @Transactional
    public void save() {
    	List<String> papeisTotais = papelManager.getListaDeNomesDosPapeis();
        UpdateResourcesOperation resourcesOperation = new UpdateResourcesOperation(recurso.getIdentificador(), getPapeis(), papeisTotais);
        resourcesOperation.run();
        recursoManager.flush();
        FacesMessages.instance().add(infoxMessages.get("entity_updated"));
    }
}
