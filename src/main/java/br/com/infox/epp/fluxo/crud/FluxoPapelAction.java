package br.com.infox.epp.fluxo.crud;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(FluxoPapelAction.NAME)
public class FluxoPapelAction extends AbstractCrudAction<FluxoPapel, FluxoPapelManager> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(FluxoPapelAction.class);

    public static final String NAME = "fluxoPapelAction";
    
    @In(value= PapelTreeHandler.NAME)
    private PapelTreeHandler papelTreeHandler;
    private List<FluxoPapel> fluxoPapelList;
    private Fluxo fluxo;

    @Override
    protected void beforeSave() {
        getInstance().setFluxo(fluxo);
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

    public void init(final Fluxo fluxo) {
        this.fluxo = fluxo;
        listByNatureza();
    }

    private void listByNatureza() {
        setFluxoPapelList(getManager().listByFluxo(fluxo));
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
