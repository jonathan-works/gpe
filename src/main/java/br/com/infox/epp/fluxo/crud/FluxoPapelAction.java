package br.com.infox.epp.fluxo.crud;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.PapelTreeHandler;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.FluxoPapel;
import br.com.infox.epp.fluxo.manager.FluxoPapelManager;

@Name(FluxoPapelAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class FluxoPapelAction extends AbstractCrudAction<FluxoPapel> {
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(FluxoPapelAction.class);

	public static final String NAME = "fluxoPapelAction";

	@In
	private FluxoPapelManager fluxoPapelManager; 
	
	private PapelTreeHandler papelTreeHandler = new PapelTreeHandler();
	private List<FluxoPapel> fluxoPapelList;
	private Fluxo fluxo;
	
	@Override
	protected boolean isInstanceValid() {
		getInstance().setFluxo(fluxo);
		return super.isInstanceValid();
	}
	
	@Override
	protected void afterSave() {
		newInstance();
		listByNatureza();
	}
	
	@Override
	public String remove(final FluxoPapel obj) {
		final String remove = super.remove(obj);
		if(remove != null) {
			getFluxoPapelList().remove(obj);
		}
		return remove;
	}

	public void removeAll() {
		for (final Iterator<FluxoPapel> iterator = getFluxoPapelList().iterator(); iterator.hasNext();) {
			final FluxoPapel nl = iterator.next();
			try {
				getGenericManager().remove(nl);
			} catch (final Exception e) {
			    LOG.error(".removeAll()", e);
			}
			iterator.remove();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init(final Fluxo fluxo) {
		this.fluxo = fluxo;
		listByNatureza();
	}

	private void listByNatureza() {
		setFluxoPapelList(fluxoPapelManager.listByFluxo(fluxo));
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