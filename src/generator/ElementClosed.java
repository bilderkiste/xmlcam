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
	protected ArrayList<ToolPath> createPocket(Path2D.Double shape, AffineTransform at, Tool tool) {
		ArrayList<ToolPath> pocketToolPathes = new ArrayList<ToolPath>();
				new ToolPath("Pocket for " + name);
		this.tool = new Tool(2.0);
		
		ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();
	
		shape.closePath();
		
		// 3. Wandle die transformierte Shape in eine Area um
        Area area = new Area(at.createTransformedShape(shape));
        
        // 4. Hole die Bounding Box der gesamten Form
        Rectangle2D bounds = area.getBounds2D();
    
        // 5. Beginne den Scanline (Hatching) Prozess
        double[] coords = new double[6];
        boolean directionLeftToRight = true;
		
        // Sammele linesegmente des Shapes und für Sie lineSegments hinzu
        for(double y = bounds.getMinY() + tool.getRadius(); y <= bounds.getMaxY() - tool.getRadius(); y += tool.getDiameter()) {
		
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
            LineSegment ls = new LineSegment(y);
            PathIterator pi = scanArea.getPathIterator(null, 1);
            while (!pi.isDone()) {
                int segmentType = pi.currentSegment(coords);
                if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_CLOSE) {
                    ls.add(coords[0]); // Füge die X-Koordinate hinzu
                }
                pi.next();
            }
            
            // Sortiere die X-Koordinaten (Start, Ende, Start, Ende, ...)
            Collections.sort(ls);
            
            for(int i = 0; i < ls.size(); i++) {
            	System.out.println(i + "->" + y + "->"+ ls.get(i));
            }
            
            lineSegments.add(ls);
        }
        
        int stage = 0;
        int j = 0;
        ToolPath ptp = new ToolPath("Pocket part " + stage++);
        // 9. G-Code für Zick-Zack-Bewegung erzeugen
        while(!lineSegments.isEmpty()) {
        	LineSegment ls = lineSegments.get(j);
        	System.out.println(ls.getY() + " -> " + ls);
        	double xStart, xEnd;
    		xStart = ls.get(0);
    		xEnd = ls.get(1);
        	if (directionLeftToRight) {
	            // Fahre von Links nach Rechts
        		ptp.addPoint(xStart + tool.getRadius(), ls.getY());
                ptp.addPoint(xEnd - tool.getRadius(), ls.getY());
	        } else {
	        	 // Fahre von Rechts nach Links
	        	ptp.addPoint(xEnd - tool.getRadius(), ls.getY());
        		ptp.addPoint(xStart + tool.getRadius(), ls.getY());
	        }
        	
        	//Lösche gefahrenen Start- und Endpunkt
            ls.remove(0);
            ls.remove(0);
            // Wenn keine Punkte mehr in der Linie, dann lösche das Liniensegment
            if(ls.isEmpty()) {
            	lineSegments.remove(j);
            } else {
            	j++; //ansonsten stehen lassen für spätere Bearbeitung und weitergehen
            }
            directionLeftToRight = !directionLeftToRight;
             
            if(j >= lineSegments.size()) {
            	j = 0;
            	pocketToolPathes.add(ptp);
            	ptp = new ToolPath("Pocket part " + stage++);
            	System.out.println("===========");
            }
            
            
        }
            
		return pocketToolPathes;
	}

	class LineSegment extends ArrayList<Double> {
		
		private static final long serialVersionUID = 1L;
		private double y;
		
		public LineSegment(double y) {
			this.y = y;
		}
		
		public double getY() {
			return y;
		}
	}
		
}
