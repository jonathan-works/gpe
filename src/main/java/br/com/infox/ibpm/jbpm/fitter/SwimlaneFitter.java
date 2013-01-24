package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.handler.SwimlaneHandler;
import br.com.itx.util.ComponentUtil;

@Name(SwinlaneFitter.NAME)
@Scope(ScopeType.CONVERSATION)
public class SwinlaneFitter implements Serializable, Fitter {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "swinlaneFitter";
	
	private List<SwimlaneHandler> swimlanes;
	private SwimlaneHandler currentSwimlane;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	
	public void addSwimlane() {
		Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
		currentSwimlane = new SwimlaneHandler(s);
		pb.getInstance().getTaskMgmtDefinition().addSwimlane(s);
		swimlanes.add(currentSwimlane);
	}

	public void removeSwimlane(SwimlaneHandler s) {
		swimlanes.remove(s);
		currentSwimlane = null;
		Map<String, Swimlane> swimlaneMap = pb.getInstance().getTaskMgmtDefinition()
				.getSwimlanes();
		swimlaneMap.remove(s.getSwimlane().getName());
	}
	
	public SwimlaneHandler getCurrentSwimlane() {
		return currentSwimlane;
	}

	public void setCurrentSwimlane(SwimlaneHandler cSwimlane) {
		this.currentSwimlane = cSwimlane;
	}
	
	public List<SwimlaneHandler> getSwimlanes() {
		if (swimlanes == null) {
			swimlanes = SwimlaneHandler.createList(pb.getInstance());
		}
		return swimlanes;
	}

	public List<String> getSwimlaneList() {
		Map<String, Swimlane> swimlaneList = pb.getInstance().getTaskMgmtDefinition()
				.getSwimlanes();
		if (swimlaneList == null) {
			return null;
		}
		return new ArrayList<String>(swimlaneList.keySet());
	}
	
	@Override
	public void clear() {
		swimlanes = null;
	}
	
}
