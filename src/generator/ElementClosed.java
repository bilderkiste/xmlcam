package generator;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.w3c.dom.Node;

import controller.Generator;
import model.Tool;
import model.ToolPathPoint;

abstract class ElementClosed extends Element {

	public ElementClosed(Node node, Generator gen) {
		super(node, gen);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a pocket toolpath for the shape given by the toolPath. The pocket will milled by parallel moves in x direction.
	 * @param toolPath the toolpath from the shape
	 * @return
	 */
	protected ArrayList<ToolPathPoint> createPocket(ArrayList<ToolPathPoint> toolPath) {
		ArrayList<ToolPathPoint> pocketToolPath = new ArrayList<ToolPathPoint>();
		this.tool = new Tool(2.0);
		
		//create polygon for the pocket boundaries
		Path2D.Double polygon = new Path2D.Double();
		polygon.moveTo(toolPath.get(0).getX(), toolPath.get(0).getY());
		for(int i = 1; i < toolPath.size(); i++) {
			polygon.lineTo(toolPath.get(i).getX(), toolPath.get(i).getY());
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
						pocketToolPath.add(new ToolPathPoint(startX, y, "Pocket"));
						pocketToolPath.add(new ToolPathPoint(endX, y, "Pocket"));
					}
				}	
			}	
		}
		return pocketToolPath;
	}

}
