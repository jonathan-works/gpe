package br.com.infox.ibpm.xpdl.activities;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.Node;
import org.jdom.Element;

import br.com.infox.ibpm.xpdl.element.ElementXPDL;
import br.com.infox.ibpm.xpdl.lane.LaneXPDL;

public abstract class ActivityXPDL extends ElementXPDL {
	
	private static final long serialVersionUID = 1L;
	private List<ActivityXPDL> leaves;
	private List<ActivityXPDL> arrives;
	private LaneXPDL lane;
	protected Node	node;
	
	public ActivityXPDL(Element element, String name) {
		super(element, name);
		leaves = new ArrayList<ActivityXPDL>(0);
		arrives = new ArrayList<ActivityXPDL>(0);
	}
	
	public abstract Node toNode();
	
	public void setTransitionTo(ActivityXPDL to) {
		leaves.add(to);
		to.setTransitionFrom(this);
	}
	
	private void setTransitionFrom(ActivityXPDL from) {
		arrives.add(from);
	}

	public List<ActivityXPDL> getLeaves() {
		return leaves;
	}

	public List<ActivityXPDL> getArrives() {
		return arrives;
	}

	public LaneXPDL getLane() {
		return lane;
	}

	public void setLane(LaneXPDL lane) {
		this.lane = lane;
	}
	
	public void setLeaves(List<ActivityXPDL> leaves) {
		this.leaves = leaves;
	}

	public void setArrives(List<ActivityXPDL> arrives) {
		this.arrives = arrives;
	}
	
	@Override
	public String toString() {
		return super.toString().replace("[ElementXPDL]", "[Activity]");
	}

}
