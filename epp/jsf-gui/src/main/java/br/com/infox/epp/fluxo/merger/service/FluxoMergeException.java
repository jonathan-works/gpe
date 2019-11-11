package br.com.infox.epp.fluxo.merger.service;

import org.jboss.seam.annotations.ApplicationException;

import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;

@ApplicationException(rollback = true, end = false)
@javax.ejb.ApplicationException(rollback = true)
public class FluxoMergeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private MergePointsBundle invalidMergePoints;
	
    public FluxoMergeException(MergePointsBundle invalidMergePoints) {
    	this.invalidMergePoints = invalidMergePoints;
    }
    
    public MergePointsBundle getInvalidMergePoints() {
		return invalidMergePoints;
	}
}
