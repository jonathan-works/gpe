package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.entity.Recurso;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.manager.RecursoManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.seam.security.operation.UpdateResourcesOperation;

@Named
@ViewScoped
public class RecursoPapelController implements Serializable {

	private static final long serialVersionUID = 1L;

    private Recurso recurso;
    private List<String> papeis;
    private List<String> papeisDisponiveis;

    @Inject
    private RecursoManager recursoManager;
    @Inject
    private InfoxMessages infoxMessages;
    private PapelManager papelManager = BeanManager.INSTANCE.getReference(PapelManager.class);

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
