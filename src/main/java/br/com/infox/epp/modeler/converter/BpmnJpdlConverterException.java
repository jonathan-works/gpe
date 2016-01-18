package br.com.infox.epp.modeler.converter;

public class BpmnJpdlConverterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BpmnJpdlConverterException() {
    }

	public BpmnJpdlConverterException(String message) {
        super(message);
    }

    public BpmnJpdlConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpmnJpdlConverterException(Throwable cause) {
        super(cause);
    }
}
