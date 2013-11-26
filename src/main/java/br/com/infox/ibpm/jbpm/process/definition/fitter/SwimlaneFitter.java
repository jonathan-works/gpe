package br.com.infox.ibpm.jbpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.taskmgmt.def.Swimlane;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.ibpm.jbpm.handler.SwimlaneHandler;
import br.com.infox.ibpm.jbpm.process.definition.ProcessBuilder;
import br.com.itx.util.ComponentUtil;

@Name(SwimlaneFitter.NAME)
@AutoCreate
public class SwimlaneFitter extends Fitter implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "swimlaneFitter";
	
	private List<SwimlaneHandler> swimlanes;
	private SwimlaneHandler currentSwimlane;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	
	public void addSwimlane() {
		Swimlane s = new Swimlane("Raia " + (swimlanes.size() + 1));
		currentSwimlane = new SwimlaneHandler(s);
		pb.getInstance().getTaskMgmtDefinition().addSwimlane(s);
		swimlanes.add(currentSwimlane);
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
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

	@SuppressWarnings(WarningConstants.UNCHECKED)
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
