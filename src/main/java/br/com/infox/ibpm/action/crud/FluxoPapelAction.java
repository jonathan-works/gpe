package br.com.infox.ibpm.action.crud;

import java.util.Iterator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.ibpm.component.tree.PapelTreeHandler;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.FluxoPapel;
import br.com.infox.ibpm.home.FluxoHome;
import br.com.infox.ibpm.manager.FluxoPapelManager;

@Name(FluxoPapelAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class FluxoPapelAction extends AbstractCrudAction<FluxoPapel>{

	public static final String NAME = "fluxoPapelAction";

	@In
	private FluxoPapelManager fluxoPapelManager; 
	
	private PapelTreeHandler papelTreeHandler = new PapelTreeHandler();
	private List<FluxoPapel> fluxoPapelList;
	private Fluxo fluxo;
	
	@Override
	protected boolean beforeSave() {
		getInstance().setFluxo(fluxo);
		return super.beforeSave();
	}
	
	@Override
	protected void afterSave() {
		newInstance();
		listByNatureza();
	}
	
	@Override
	public String remove(Object obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			getFluxoPapelList().remove(obj);
		}
		return remove;
	}

	public void removeAll() {
		for (Iterator<FluxoPapel> iterator = getFluxoPapelList().iterator(); iterator.hasNext();) {
			FluxoPapel nl = iterator.next();
			try {
				genericManager.remove(nl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			iterator.remove();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init() {
		FluxoHome fluxoHome = FluxoHome.instance();
		fluxo = fluxoHome.getInstance();
		listByNatureza();
	}

	private void listByNatureza() {
		setFluxoPapelList(fluxoPapelManager.listByFluxo(fluxo));
	}

	public void setPapelTreeHandler(PapelTreeHandler papelTreeHandler) {
		this.papelTreeHandler = papelTreeHandler;
	}

	public PapelTreeHandler getPapelTreeHandler() {
		return papelTreeHandler;
	}

	public void setFluxoPapelList(List<FluxoPapel> fluxoPapelList) {
		this.fluxoPapelList = fluxoPapelList;
	}

	public List<FluxoPapel> getFluxoPapelList() {
		return fluxoPapelList;
	}	
	
}