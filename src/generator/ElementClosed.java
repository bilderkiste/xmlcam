package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
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
	
		this.tool = new Tool(2.0);
		
		ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();
	
		shape.closePath();
		
		// Wandle die transformierte Shape in eine Area um
        Area area = new Area(at.createTransformedShape(shape));
        
        // Hole die Bounding Box der gesamten Form
        Rectangle2D bounds = area.getBounds2D();
    
        // Beginne den Scanline (Hatching) Prozess
        double[] coords = new double[6];
        boolean directionLeftToRight = true;
		
        // Sammele Linesegmente des Shapes und füge diese für jede Zeile dem lineSegments hinzu
        for(double y = bounds.getMinY() + tool.getRadius(); y <= bounds.getMaxY() - tool.getRadius(); y += tool.getDiameter()) {
		
        	// Erstelle eine dünne horizontale "Scan-Area"
            Rectangle2D.Double scanRect = new Rectangle2D.Double(
                bounds.getMinX() - 1, y, bounds.getWidth() + 2, 0.001
            );
            Area scanArea = new Area(scanRect);

            // Finde die SCHNITTMENGE (Intersection) zwischen Buchstaben und Scanline
            scanArea.intersect(area);

            if (scanArea.isEmpty()) {
                continue; // Nichts in dieser Zeile
            }

            // Extrahiere alle Segmente aus der Schnittmenge
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
            
            /*for(int i = 0; i < ls.size(); i++) {
            	System.out.println(i + "->" + y + "->"+ ls.get(i));
            }*/
            
            lineSegments.add(ls);
        }
        
        int stage = 0;
        int j = 0;
        //Point2D.Double old = new Point2D.Double(lineSegments.get(0).get(0), lineSegments.get(0).getY());
        Point2D.Double start, end;
        double xStart, xEnd;
        ToolPath ptp = new ToolPath("Pocket part " + stage++);
        // Toolpath für Zick-Zack-Bewegung erzeugen
        while(!lineSegments.isEmpty()) {
        	LineSegment ls = lineSegments.get(j);
        	//System.out.println(ls.getY() + " -> " + ls);
    		xStart = ls.get(0) + tool.getRadius();
    		xEnd = ls.get(1) - tool.getRadius();
    		start = new Point2D.Double(xStart, ls.getY());
    		end = new Point2D.Double(xEnd, ls.getY());
    		if(xEnd - xStart > tool.getDiameter()) { //Prüfen, ob das Segment nicht zu kurz
	        	if(directionLeftToRight) {
	        		/*if(old.distance(start) > 10) {
		        		toolPathes.add(ptp);
		        		ptp = new ToolPath("Pocket part " + stage++);
		        		System.out.println("=x=========");
		        	}*/
	        		//System.out.println("LnR " + old + "->" + start + "->Dist: "+ old.distance(start));
		            // Fahre von Links nach Rechts
	        		ptp.addPoint(start);
	                ptp.addPoint(end);
	                //System.out.println(ptp);
	                //old.setLocation(end);
		        } else {
		        	 // Fahre von Rechts nach Links
		        	/*if(old.distance(end) > 10) {
		        		toolPathes.add(ptp);
		        		ptp = new ToolPath("Pocket part " + stage++);
		        		System.out.println("=xx=========");
		        	}*/
		        	//System.out.println("RnL " + old + "->" + start + "->Dist: "+ old.distance(end));
		        	ptp.addPoint(end);
	        		ptp.addPoint(start);
	        		//System.out.println(ptp);
	        		//old.setLocation(start);
		        }
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
            	//System.out.println("===========");
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
