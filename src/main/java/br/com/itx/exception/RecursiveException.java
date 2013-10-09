package br.com.itx.exception;

public class RecursiveException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public static final String EXCEPTION = RecursiveException.class.getSimpleName();

	public RecursiveException(String msg) {
		super(msg);
	}

}
