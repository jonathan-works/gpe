package br.com.infox.epp.fluxo.xpdl.activities;

import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;

public class ActivityNotAllowedXPDLException extends IllegalXPDLException {

	private static final long serialVersionUID = 1L;

	public ActivityNotAllowedXPDLException(String msg) {
		super(msg);
	}
}
