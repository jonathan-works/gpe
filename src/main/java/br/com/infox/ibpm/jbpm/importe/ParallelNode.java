package br.com.infox.ibpm.jbpm.importe;

import br.com.infox.ibpm.jbpm.node.DecisionNode;

public class ParallelNode extends DecisionNode {

	private static final long	serialVersionUID	= 1L;
	private final String		name				= "[Temporary node - DecisionNode]";

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ParallelNode)) {
			return false;
		}
		ParallelNode other = (ParallelNode) obj;
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
		return name + this.getName();
	}
}
