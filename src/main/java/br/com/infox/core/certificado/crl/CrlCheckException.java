package br.com.infox.core.certificado.crl;

public class CrlCheckException extends Exception {

	private static final long serialVersionUID = 1L;

	public CrlCheckException() {
		super();
	}

	public CrlCheckException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CrlCheckException(String arg0) {
		super(arg0);
	}

	public CrlCheckException(Throwable arg0) {
		super(arg0);
	}
}