package br.com.infox.epp.fluxo.xpdl.lane;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.XmlUtil;

public final class LanesXPDLFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private LanesXPDLFactory() {
    }

    public static List<LaneXPDL> getLanes(Element root) throws IllegalNumberPoolsXPDLException {
        List<Element> poolsElement = XmlUtil.getChildren(root, "Pools");
        List<Element> poolsList = XmlUtil.getChildren(poolsElement.get(0), "Pool");
        if (poolsList.size() > 2) {
            throw new IllegalNumberPoolsXPDLException("O sistema não permite mais de uma piscina na definição dos fluxos.");
        }

        Element poll = poolsList.get(1);
        Element graphics = XmlUtil.getChildByIndex(poll, "NodeGraphicsInfos", 0);
        Element graphic = XmlUtil.getChildByIndex(graphics, "NodeGraphicsInfo", 0);
        Element coordinates = XmlUtil.getChildByIndex(graphic, "Coordinates", 0);
        Point2D poolCoordinates = new Point2D.Float(Float.valueOf(coordinates.getAttributeValue("XCoordinate")), Float.valueOf(coordinates.getAttributeValue("YCoordinate")));
        List<Element> lanesElement = XmlUtil.getChildren(poll, "Lanes");
        List<Element> laneList = XmlUtil.getChildren(lanesElement.get(0), "Lane");
        return createLaneXPDLList(laneList, poolCoordinates);
    }

    private static List<LaneXPDL> createLaneXPDLList(List<Element> list, Point2D poolCoordinates) {
        List<LaneXPDL> laneList = new ArrayList<LaneXPDL>();
        int index = 1;
        for (Element ele : list) {
            LaneXPDL lane = LaneXPDL.createInstance(ele, FluxoXPDL.NO_NAME + index, poolCoordinates);
            laneList.add(lane);

            index++;
        }
        return laneList;
    }

}
