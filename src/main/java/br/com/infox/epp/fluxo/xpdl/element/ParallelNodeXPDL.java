package br.com.infox.epp.fluxo.xpdl.element;

import org.jbpm.graph.def.Node;

public class ParallelNodeXPDL extends Node {

	private static final long serialVersionUID = 1L;
	private static final String NAME = "[Temporary node]";

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ParallelNodeXPDL)) {
			return false;
		}
		ParallelNodeXPDL other = (ParallelNodeXPDL) obj;
		return getId() == other.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)getId();
		return result;
	}

	@Override
	public String toString() {
		return NAME + this.getName();
	}
}
