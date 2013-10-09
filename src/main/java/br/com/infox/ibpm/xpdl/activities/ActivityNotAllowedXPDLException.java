package br.com.infox.ibpm.xpdl.activities;

import br.com.infox.ibpm.xpdl.IllegalXPDLException;

public class ActivityNotAllowedXPDLException extends IllegalXPDLException {

	private static final long serialVersionUID = 1L;

	public ActivityNotAllowedXPDLException(String msg) {
		super(msg);
	}
}
