package br.com.infox.epp.fluxo.xpdl.element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jdom2.Element;

import br.com.itx.util.XmlUtil;

public final class GraphicsInfoXPDL implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Rectangle2D rectangle;
	
	private GraphicsInfoXPDL(Point2D position, double width, double height) {
		this.rectangle = new Rectangle2D.Double(position.getX(), position.getY(), width, height);
	}
	
	public static GraphicsInfoXPDL createInstance(Element element) {
		Element info = XmlUtil.getChildByIndex(element, "NodeGraphicsInfo", 0);
		double height = Double.parseDouble(info.getAttributeValue("Height"));
		double width = Double.parseDouble(info.getAttributeValue("Width"));
		
		Element coordinates = XmlUtil.getChildByIndex(info, "Coordinates", 0);
		double x = Double.parseDouble(coordinates.getAttributeValue("XCoordinate"));
		double y = Double.parseDouble(coordinates.getAttributeValue("YCoordinate"));
		Point2D position = new Point2D.Double(x,y);
		
		return new GraphicsInfoXPDL(position, width, height);
	}

	public Point2D getPosition() {
		return new Point2D.Double(rectangle.getMinX(), rectangle.getMinY());
	}

	public double getWidth() {
		return rectangle.getWidth();
	}

	public double getHeight() {
		return rectangle.getHeight();
	}

	@Override
	public String toString() {
		return "[GrapghicsInfo] position (" + getPosition().getX() + "," + getPosition().getY() + ") width: " + getWidth() + " height: " + getHeight();
	}
	
	public Rectangle2D getRectangle() {
		return rectangle;
	}
}
