package br.com.infox.epp.fluxo.merger.view;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeException;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessRollbackException;

@Named
@ViewScoped
public class FluxoMergeView implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(FluxoMergeView.class);

    private MergePointsBundle mergePointsBundle;

    @Inject
    private FluxoMergeService fluxoMergeService;
    @Inject
    private ActionMessagesService actionMessagesService;

    public MergePointsBundle getMergePointsBundle() {
        return mergePointsBundle;
    }

    public void deploy(Fluxo fluxo) {
        try {
            mergePointsBundle = fluxoMergeService.publish(fluxo, getMergePointsBundle());
            FacesMessages.instance().add("Fluxo publicado com sucesso");
        } catch (FluxoMergeException e) {
            mergePointsBundle = e.getInvalidMergePoints();
        } catch (Exception e) {
        	LOG.error(".deploy(Fluxo)", e);
        	Exception realException = e;
        	if (e instanceof BusinessRollbackException && e.getCause() instanceof Exception) {
        		realException = (Exception) e.getCause();
        	}
        	actionMessagesService.handleGenericException(realException);
        }
    }
    
    public void clear() {
    	mergePointsBundle = null;
    }
}
