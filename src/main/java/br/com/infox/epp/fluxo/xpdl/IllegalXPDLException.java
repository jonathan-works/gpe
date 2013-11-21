package br.com.infox.epp.fluxo.xpdl;

public class IllegalXPDLException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public IllegalXPDLException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalXPDLException(String message) {
		super(message);
	}

}
