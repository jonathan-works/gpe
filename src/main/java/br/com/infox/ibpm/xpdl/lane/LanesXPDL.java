package br.com.infox.ibpm.xpdl.lane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jdom.Element;

import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;

public class LanesXPDL implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<LaneXPDL>	lanes;
	private String pollName;
	
	private LanesXPDL(String pollName, List<LaneXPDL> lanes) {
		this.pollName = pollName;
		this.lanes = lanes;
	}

	private static List<LaneXPDL> createLaneXPDLList(List<Element> list) {
		List<LaneXPDL> laneList = new ArrayList<LaneXPDL>();
		int index = 1;
		for (Element ele : list) {
			LaneXPDL lane = LaneXPDL.createInstance(ele, FluxoXPDL.NO_NAME + index);
			laneList.add(lane);
			
			index++;
		}
		return laneList;
	}
	
	public static LanesXPDL createInstance(Element root) throws IllegalNumberPoolsXPDLException {
		List<Element> poolsElement = XmlUtil.getChildren(root, "Pools");
		List<Element> poolsList = XmlUtil.getChildren(poolsElement.get(0), "Pool");
		if(poolsList.size() > 2) {
			throw new IllegalNumberPoolsXPDLException("O sistema não permite mais de uma piscina na definição dos fluxos.");
		}
		
		Element poll = poolsList.get(1);
		String pollName = XmlUtil.getAttributeValue(poll, "Name");
		List<Element> lanesElement = XmlUtil.getChildren(poll, "Lanes");
		List<Element> laneList = XmlUtil.getChildren(lanesElement.get(0), "Lane");
		return new LanesXPDL(pollName, createLaneXPDLList(laneList));
	}

	public List<LaneXPDL> getLanes() {
		return lanes;
	}
	
	public void assignActivitiesToLane(List<ActivityXPDL> activities) {
		for (LaneXPDL lane : lanes) {
			List<ActivityXPDL> list = lane.findActivitiesBelongingToLane(activities);
			for (ActivityXPDL activity : list) {
				activity.setLane(lane);
			}
		}
	}
	
	public void assignLanesToProcessDefinition(ProcessDefinition definition) {
		for (LaneXPDL lane : lanes) {
			definition.getTaskMgmtDefinition().addSwimlane(lane.toSwimlane());
		}
	}
	
	public String getPoolName() {
		return pollName;
	}
	
	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append("[Lanes] #lanes: " + lanes.size());
		int i = 0;
		while(i < lanes.size() && i < 5) {
			temp.append(lanes.get(i++) + ", ");
		}
		if(i < lanes.size()) {
			temp.append(" ... ");
		}
		return temp.toString();
	}

	
}
