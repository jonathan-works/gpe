package br.com.infox.ibpm.jbpm.importe;

public class ParallelNodeException extends Exception{

	private static final long serialVersionUID = 1L;

	String msg;
	public ParallelNodeException(String msg) {
		super(msg);
		this.msg = msg;
	}
}
