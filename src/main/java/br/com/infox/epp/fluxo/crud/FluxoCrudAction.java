package br.com.infox.epp.fluxo.crud;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.controller.Controller;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Named
@ViewScoped
public class FluxoCrudAction implements Controller {

    private static final long serialVersionUID = 1L;
    @Inject
    private FluxoController fluxoController;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    
    private Fluxo replica;
    private String tab = TAB_SEARCH;
    private boolean hasProcessoRunning = false;
    
    @ExceptionHandled(MethodType.INACTIVE)
    public void inactive(final Fluxo fluxo) {
        if (!fluxoManager.existemProcessosAssociadosAFluxo(fluxo)) {
            fluxo.setAtivo(false);
            fluxoManager.update(fluxo);
        } else {
            String message = "#{infoxMessages['fluxo.remocaoProibida']}";
            FacesMessages.instance().add(ERROR, message);
        }
    }

    public Fluxo getInstance() {
    	if (fluxoController.getFluxo() == null) {
    		newInstance();
    	}
    	return fluxoController.getFluxo();
    }
    
    @ExceptionHandled(MethodType.UPDATE)
    public void update() {
    	setInstance(fluxoManager.update(getInstance()));
    }
    
    @ExceptionHandled(MethodType.PERSIST)
    public void persist() {
    	fluxoManager.persist(getInstance());
    	definicaoVariavelProcessoManager.createDefaultDefinicaoVariavelProcessoList(getInstance());
        this.hasProcessoRunning = false;
    }
    
    public void gerarReplica() {
    	replica = getInstance().makeCopy();
    }
    
    @ExceptionHandled(successMessage = "Fluxo exportado com sucesso")
    public void gravarReplica() {
    	fluxoManager.gravarReplica(replica);
    	setInstance(replica);
    	replica = null;
    }
    
    public boolean isManaged() {
    	return getInstance().getIdFluxo() != null;
    }
    
    @Override
    public String getTab() {
		return tab;
	}
    
    @Override
    public void setTab(String tab) {
		this.tab = tab;
	}
    
    @Override
    public void onClickSearchTab() {
    	if (isManaged()) {
    		fluxoManager.detach(getInstance());
    	}
    	newInstance();
    }
    
    @Override
    public void onClickFormTab() {
    }

	@Override
	public Object getId() {
		return getInstance().getIdFluxo();
	}

	@Override
	public void setId(Object id) {
		if (id == null) {
			setInstance(null);
		} else if (!id.equals(getInstance().getIdFluxo())) {
			setInstance(fluxoManager.find(id));
		}
	}
	
	public Fluxo getReplica() {
		return replica;
	}
	
    public boolean isHasProcessoRunning() {
        return hasProcessoRunning;
    }
    
    public void newInstance() {
    	fluxoController.setFluxo(new Fluxo());
        getInstance().setPublicado(false);
        this.replica = null;
        this.hasProcessoRunning = false;
    }
    
	public void setInstance(Fluxo instance) {
		fluxoController.setFluxo(instance);
		this.hasProcessoRunning = fluxoManager.existemProcessoEmAndamento(getInstance());
	}
}
