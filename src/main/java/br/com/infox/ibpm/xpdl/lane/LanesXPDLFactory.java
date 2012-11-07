package br.com.infox.ibpm.xpdl.lane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.itx.util.XmlUtil;

public class LanesXPDLFactory implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private LanesXPDLFactory() { }

	public static List<LaneXPDL> getLanes(Element root) throws IllegalNumberPoolsXPDLException {
		List<Element> poolsElement = XmlUtil.getChildren(root, "Pools");
		List<Element> poolsList = XmlUtil.getChildren(poolsElement.get(0), "Pool");
		if(poolsList.size() > 2) {
			throw new IllegalNumberPoolsXPDLException("O sistema não permite mais de uma piscina na definição dos fluxos.");
		}
		
		Element poll = poolsList.get(1);
		List<Element> lanesElement = XmlUtil.getChildren(poll, "Lanes");
		List<Element> laneList = XmlUtil.getChildren(lanesElement.get(0), "Lane");
		return createLaneXPDLList(laneList);
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

}
