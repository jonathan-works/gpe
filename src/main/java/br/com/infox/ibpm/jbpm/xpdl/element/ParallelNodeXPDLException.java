package br.com.infox.ibpm.jbpm.xpdl.element;

public class ParallelNodeXPDLException extends Exception{

	private static final long serialVersionUID = 1L;

	String msg;
	public ParallelNodeXPDLException(String msg) {
		super(msg);
		this.msg = msg;
	}
}
