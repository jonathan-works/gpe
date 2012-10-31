package br.com.infox.ibpm.jbpm.xpdl.lane;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.taskmgmt.def.Swimlane;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityXPDL;
import br.com.infox.ibpm.jbpm.xpdl.element.ElementXPDL;

public class LaneXPDL extends ElementXPDL{
	
	private static final long serialVersionUID = 1L;
	private Swimlane swimlane;
	
	public LaneXPDL(Element element, String name) {
		super(element, name);
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
		if(list == null)
			return null;
		List<ActivityXPDL> belonging = new ArrayList<ActivityXPDL>();
		Rectangle2D rectangle = this.getGraphics().getRectangle();
		for(ActivityXPDL act:list) {
			if(rectangle.contains(act.getGraphics().getRectangle())) {
				belonging.add(act);
			}
		}
		return belonging;
	}
	
	@Override
	public String toString() {
		return super.toString().replace("[ElementXPDL]", "[Lane]");
	}

}
 