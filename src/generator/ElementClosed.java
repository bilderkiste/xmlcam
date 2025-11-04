package generator;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Node;

import controller.Generator;
import model.Tool;
import model.ToolPath;

abstract class ElementClosed extends Element {

	public ElementClosed(Node node, Generator gen) {
		super(node, gen);
	}

	/**
	 * Creates a pocket toolpath for the shape given by the toolPath. The pocket will milled by parallel moves in x direction.
	 * @param toolPath the toolpath from the shape
	 * @return
	 */
	protected ToolPath createPocket(ToolPath toolPath) {
		ToolPath pocketToolPath = new ToolPath("Pocket for " + toolPath.getName());
		this.tool = new Tool(2.0);
		
		//create polygon for the pocket boundaries
		Path2D.Double polygon = new Path2D.Double();
		polygon.moveTo(toolPath.getX(0).doubleValue(), toolPath.getY(0).doubleValue());
		for(int i = 1; i < toolPath.size(); i++) {
			polygon.lineTo(toolPath.getX(i).doubleValue(), toolPath.getY(i).doubleValue());
		}
		polygon.closePath();
		
		// Begrenzungsrechteck berechnen
		Rectangle2D bounds= polygon.getBounds2D();
		double xMin = bounds.getMinX();
		double xMax = bounds.getMaxX();
		double yMin = bounds.getMinY();
		double yMax = bounds.getMaxY();
		
		for(double y = yMin + tool.getRadius(); y < yMax; y += tool.getRadius()) {
			boolean inside = false;
			double startX = 0;
			for(double x = xMin - 0.2; x <= xMax + 0.2; x += 0.1) {
				if(polygon.contains(x, y)) {
					if(!inside) {
						startX = x + tool.getRadius();
						inside = true;
					}
				} else {
					if(inside) {
						double endX = x - tool.getRadius();
						inside = false;
						pocketToolPath.addPoint(startX, y);
						pocketToolPath.addPoint(endX, y);
					}
				}	
			}	
		}
		return pocketToolPath;
	}

}
