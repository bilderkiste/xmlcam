package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

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
	 * @param shape The path from the shape
	 * @return The ToolPath
	 */
	protected ToolPath createPocket(Path2D.Double shape, AffineTransform at) {
		ToolPath pocketToolPath = new ToolPath("Pocket for " + name);
		this.tool = new Tool(2.0);
		
		double stepover = 2.0;
	
		shape.closePath();
		
		// 3. Wandle die transformierte Shape in eine Area um
        Area area = new Area(at.createTransformedShape(shape));
        
        // 4. Hole die Bounding Box der gesamten Form
        Rectangle2D bounds = area.getBounds2D();
    
        // 5. Beginne den Scanline (Hatching) Prozess
        double[] coords = new double[6];
        boolean directionLeftToRight = true;
		
        // Loop von unten nach oben mit der Schrittweite (stepover)
        for(double y = bounds.getMinY(); y <= bounds.getMaxY(); y += stepover) {
		
        	// 6. Erstelle eine dünne horizontale "Scan-Area"
            Rectangle2D.Double scanRect = new Rectangle2D.Double(
                bounds.getMinX() - 1, y, bounds.getWidth() + 2, 0.001
            );
            Area scanArea = new Area(scanRect);

            // 7. Finde die SCHNITTMENGE (Intersection) zwischen Buchstaben und Scanline
            scanArea.intersect(area);

            if (scanArea.isEmpty()) {
                continue; // Nichts in dieser Zeile
            }

            // 8. Extrahiere alle Segmente aus der Schnittmenge
            // (Für ein 'H' wären dies z.B. zwei getrennte Segmente)
            ArrayList<Double> lineSegments = new ArrayList<Double>();
            PathIterator pi = scanArea.getPathIterator(null, 1);

            while (!pi.isDone()) {
                int segmentType = pi.currentSegment(coords);
                if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
                    lineSegments.add(coords[0]); // Füge die X-Koordinate hinzu
                }
                pi.next();
            }
            
            // Sortiere die X-Koordinaten (Start, Ende, Start, Ende, ...)
            Collections.sort(lineSegments);
            
            // 9. G-Code für Zick-Zack-Bewegung erzeugen
            
            if (directionLeftToRight) {
	            // Fahre von Links nach Rechts
	            for (int i = 0; i < lineSegments.size(); i += 2) {
	            	double xStart = lineSegments.get(i);
	                double xEnd = (i + 1 < lineSegments.size()) ? lineSegments.get(i + 1) : xStart;
	                pocketToolPath.addPoint(xStart + 2, y);
	                pocketToolPath.addPoint(xEnd - 2, y);
	            }
	        } else {
	        	for (int i = lineSegments.size() - 2; i >= 0; i -= 2) {
                    double xStart = lineSegments.get(i);
                    double xEnd = (i + 1 < lineSegments.size()) ? lineSegments.get(i + 1) : xStart;
                    pocketToolPath.addPoint(xStart, y);
	                pocketToolPath.addPoint(xEnd, y);
	        	}
	        }
	            	
            directionLeftToRight = !directionLeftToRight;
            
        }
            
		return pocketToolPath;
	}

}
