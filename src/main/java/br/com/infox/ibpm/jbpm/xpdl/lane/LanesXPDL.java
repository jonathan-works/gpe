package br.com.infox.ibpm.jbpm.xpdl.lane;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;

public class LanesXPDL {

	private final String ERRO_MSG = "O sistema e-PA não permite mais de uma piscina na definição dos fluxos.";
	private List<LaneXPDL>	lanes;
	private String name;
	private int index;
	
	public LanesXPDL(Element root) throws IllegalNumberPoolsXPDLException{
		index = 1;
		Element pool = checkNumberPools(root).get(0);
		List<Element> poolsList = XmlUtil.getChildren(pool, "Pool");
		if(poolsList.size() > 2) {
			throw new IllegalNumberPoolsXPDLException(ERRO_MSG);
		}
		name = XmlUtil.getAttributeValue(poolsList.get(1), "Name");
		List<Element> lanesList = XmlUtil.getChildren(poolsList.get(1), "Lanes");
		List<Element> laneList = XmlUtil.getChildren(lanesList.get(0), "Lane");
		lanes = createLanesList(laneList);
	}

	private List<Element> checkNumberPools(Element root) throws IllegalNumberPoolsXPDLException {
		List<Element> poolsList = XmlUtil.getChildren(root, "Pools");
		if(poolsList == null || poolsList.isEmpty())
			throw new IllegalNumberPoolsXPDLException("Não há piscina definida. Impossível criar as raias.");
		if(poolsList.size() > 1)
			throw new IllegalNumberPoolsXPDLException(ERRO_MSG);
		return poolsList;
	}
	
	private List<LaneXPDL> createLanesList(List<Element> list) {
		if (list == null)
			return null;
		List<LaneXPDL> lanes = new ArrayList<LaneXPDL>();
		for (Element ele : list) {
			LaneXPDL lane = new LaneXPDL(ele, FluxoXPDL.NO_NAME + index++);
			lanes.add(lane);
		}
		return lanes;
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
		if (lanes == null)
			return;
		for (LaneXPDL lane : lanes) {
			definition.getTaskMgmtDefinition().addSwimlane(lane.toSwimlane());
		}
	}
	
	public String getPoolName() {
		return name;
	}
	
	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder();
		if(lanes == null) {
			temp.append("[Lanes] #lanes: 0");
		}
		else {
			temp.append("[Lanes] #lanes: " + lanes.size());
			int i = 0;
			while(i < lanes.size() && i < 5) {
				temp.append(lanes.get(i++) + ", ");
			}
			if(i < lanes.size())
				temp.append(" ... ");
		}
		return temp.toString();
	}
}
