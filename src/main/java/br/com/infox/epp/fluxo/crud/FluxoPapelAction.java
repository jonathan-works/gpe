package br.com.infox.epp.fluxo.crud;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(FluxoPapelAction.NAME)
@ContextDependency
public class FluxoPapelAction extends AbstractCrudAction<FluxoPapel, FluxoPapelManager> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(FluxoPapelAction.class);

    public static final String NAME = "fluxoPapelAction";

    @Inject
    private FluxoController fluxoController;
    
    @Inject
    private PapelTreeHandler papelTreeHandler;
    private List<FluxoPapel> fluxoPapelList;
    
    @Override
    protected void beforeSave() {
        getInstance().setFluxo(fluxoController.getFluxo());
    }

    @Override
    protected void afterSave() {
        newInstance();
        listByNatureza();
    }

    @Override
    public String remove(final FluxoPapel obj) {
        final String remove = super.remove(obj);
        if (remove != null) {
            listByNatureza();
        }
        return remove;
    }

    public void removeAll() {
        boolean allValid = true;
        for (final Iterator<FluxoPapel> iterator = getFluxoPapelList().iterator(); iterator.hasNext();) {
            final FluxoPapel nl = iterator.next();
            try {
                getManager().remove(nl);
            } catch (final Exception e) {
                LOG.error(".removeAll()", e);
                allValid = false;
            }
            iterator.remove();
        }
        final StatusMessages messages = getMessagesHandler();
        messages.clear();
        if (allValid) {
            messages.add("Registros removidos com sucesso!");
        } else {
            messages.add("Houve erro na remoção de alguns dos itens");
        }
        listByNatureza();
    }

    @Override
    public void init() {
    	super.init();
        listByNatureza();
    }

    private void listByNatureza() {
        setFluxoPapelList(getManager().listByFluxo(fluxoController.getFluxo()));
    }

    public void setPapelTreeHandler(final PapelTreeHandler papelTreeHandler) {
        this.papelTreeHandler = papelTreeHandler;
    }

    public PapelTreeHandler getPapelTreeHandler() {
        return papelTreeHandler;
    }

    public void setFluxoPapelList(final List<FluxoPapel> fluxoPapelList) {
        this.fluxoPapelList = fluxoPapelList;
    }

    public List<FluxoPapel> getFluxoPapelList() {
        return fluxoPapelList;
    }

}
