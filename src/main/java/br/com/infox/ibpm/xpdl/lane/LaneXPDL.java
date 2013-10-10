package br.com.infox.ibpm.xpdl.lane;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.taskmgmt.def.Swimlane;
import org.jdom2.Element;

import br.com.infox.ibpm.xpdl.activities.ActivityXPDL;
import br.com.infox.ibpm.xpdl.element.ElementXPDL;

public final class LaneXPDL extends ElementXPDL {
	
	private static final long serialVersionUID = 1L;
	private Swimlane swimlane;
	
	private LaneXPDL(Element element, String name) {
		super(element, name);
	}
	
	public static LaneXPDL createInstance(Element element, String name) {
		return new LaneXPDL(element, name);
	}
	
	/**
	 * Retorna a Raia (Swimlane) do sistema JBPM correspondente a raia do XPDL
	 * @return
	 */
	public Swimlane toSwimlane() {
		if(swimlane == null) {
			swimlane = new Swimlane(this.getName());
		}
		return swimlane;
	}
	
	/**
	 * Retorna a lista de atividadesXPDL que pertencem a essa raia.
	 * @param list
	 * @return
	 */
	public List<ActivityXPDL> findActivitiesBelongingToLane(List<ActivityXPDL> list) {
		List<ActivityXPDL> belonging = new ArrayList<ActivityXPDL>();
		for(ActivityXPDL act:list) {
			if(contains(act)) {
				belonging.add(act);
			}
		}
		return belonging;
	}
	
	public boolean contains(ElementXPDL element) {
		return getGraphics().getRectangle().contains(element.getGraphics().getRectangle());
	}
	
	@Override
	public String toString() {
		return super.toString().replace("[ElementXPDL]", "[Lane]");
	}

}
 