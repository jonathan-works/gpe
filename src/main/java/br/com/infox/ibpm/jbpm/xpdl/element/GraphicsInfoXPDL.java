package br.com.infox.ibpm.jbpm.xpdl.element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jdom.Element;

import br.com.itx.util.XmlUtil;

public class GraphicsInfoXPDL implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Point2D position;
	private float width;
	private float height;
	private Rectangle2D rectangle;
	
	public GraphicsInfoXPDL(Element element) {
		completePositionInformation(element);
	}
	
	private void completePositionInformation(Element graphics) {
		Element info = XmlUtil.getChildByIndex(graphics, "NodeGraphicsInfo", 0);
		height = Integer.parseInt(info.getAttributeValue("Height"));
		width = Integer.parseInt(info.getAttributeValue("Width"));
		Element coordinates = XmlUtil.getChildByIndex(info, "Coordinates", 0);
		float x = Float.parseFloat(coordinates.getAttributeValue("XCoordinate"));
		float y = Float.parseFloat(coordinates.getAttributeValue("YCoordinate"));
		position = new Point2D.Float(x,y);
		rectangle = new Rectangle2D.Float(x, y, width, height);
	}

	public Point2D getPosition() {
		return position;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "[GrapghicsInfo] position (" + position.getX() + "," + position.getY() + ") width: " + width + " height: " + height;
	}
	
	public Rectangle2D getRectangle() {
		return rectangle;
	}
}
