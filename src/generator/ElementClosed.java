package generator;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import controller.Generator;
import model.Tool;
import model.ToolPath;

abstract class ElementClosed extends Element {
	
	public static final int ENGRAVING = 0;
	public static final int INSET = 1;
	public static final int OUTSET = 2;
	
	private int pathOffset;
	private boolean pocket;

	public ElementClosed(Node node, Generator gen) {
		super(node, gen);
		pathOffset = ElementClosed.ENGRAVING;
		pocket = false;
	}

	/**
	 * Reads the attributes from a closed element an put the values in the corresponding variables.
	 * @param map The NamedNodeMapt with the attributes
	 */
	protected void setClosedElementsAttributeVars(NamedNodeMap map) {
		try {
			if(map.getNamedItem("pocket").getTextContent().equals("parallel")) {
				pocket = true;
			}
		} catch(NullPointerException e) {
		} 
		
		try {
			if(map.getNamedItem("offset").getTextContent().equals("engraving")) {
				pathOffset = ElementClosed.ENGRAVING;
			} else if(map.getNamedItem("offset").getTextContent().equals("inset")) {
				pathOffset = ElementClosed.INSET;
			} else if(map.getNamedItem("offset").getTextContent().equals("outset")) {
				pathOffset = ElementClosed.OUTSET;
			}
		} catch(NullPointerException e) {
		}
	}
	
	/**
	 * Creates the offset pathes like engravings, insets or outsets. 
	 * @param shape The original shape.
	 * @return The transformed shape.
	 */
	public Path2D.Double createOffsetShape(Path2D.Double shape) {
        Path2D.Double pathShape = null;
        
        if(pathOffset == ElementClosed.ENGRAVING) {
        	pathShape = shape;
        } else if(pathOffset == ElementClosed.INSET) {
        	pathShape = AreaToPath(createInsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        } else if(pathOffset == ElementClosed.OUTSET) {
        	pathShape = AreaToPath(createOutsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        	System.out.println("circleOutset");
        }
        return pathShape;
	}

	/**
	 * Converts an Area shape to an Path2d.Double shape.
	 * @param area The area shape
	 * @return The Path2D.Double shape
	 */
    public Path2D.Double AreaToPath(Area area) {
        Path2D.Double path = new Path2D.Double();

        PathIterator iterator = area.getPathIterator(null);
        double[] coords = new double[6];

        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    path.lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    path.closePath();
                    break;
            }
            iterator.next();
        }
        
        return path;
    }
	
	/**
	 * Creates an inset area with a stroke. 
	 * @param originalArea The original area
	 * @param insetAmount The inset between original shape an inset shape
	 * @return The inset shape
	 */
    public Area createInsetArea(Area originalArea, float insetAmount) {
        float strokeWidth = insetAmount * 2.0f;
        BasicStroke stroke = new BasicStroke(strokeWidth, 
                                             BasicStroke.CAP_BUTT, 
                                             BasicStroke.JOIN_MITER);
        Area strokeArea = new Area(stroke.createStrokedShape(originalArea));
        Area insetArea = new Area(originalArea);
        insetArea.subtract(strokeArea);
        return insetArea;
    }
    
	/**
	 * Creates an outset area with a stroke. 
	 * @param originalArea The original area
	 * @param outsetAmount The outset between original shape an outset shape
	 * @return The outset shape
	 */
    public Area createOutsetArea(Area originalArea, float outsetAmount) {
        float strokeWidth = outsetAmount * 2.0f;
        BasicStroke stroke = new BasicStroke(strokeWidth, 
                                             BasicStroke.CAP_BUTT, 
                                             BasicStroke.JOIN_MITER);
        Area strokeArea = new Area(stroke.createStrokedShape(originalArea));
        Area outsetArea = new Area(originalArea);
        outsetArea.add(strokeArea);
        return outsetArea;
    }
	
	/**
	 * Creates a pocket toolpath for the shape given by the toolPath. The pocket will milled by parallel moves in x direction.
	 * @param shape The path from the shape
	 * @return The ToolPath
	 */
	protected ArrayList<ToolPath> createPocket(Path2D.Double shape, AffineTransform at, Tool tool) {
		ArrayList<ToolPath> pocketToolPathes = new ArrayList<ToolPath>();
		double overlap = 0.8;
		
		ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();
	
		shape.closePath();
		
		// Wandle die transformierte Shape in eine Area um
        Area area = new Area(at.createTransformedShape(shape));
        
        // Erzeuge mit einem inset eine kleinere Kontur
        area = createInsetArea(area, (float) (tool.getRadius() * overlap));
                   
        // Hole die Bounding Box der gesamten Form
        Rectangle2D bounds = area.getBounds2D();
    
        // Beginne den Scanline (Hatching) Prozess
        double[] coords = new double[6];
        boolean directionLeftToRight = true;
		
        // Sammele Linesegmente des Shapes und füge diese für jede Zeile dem lineSegments hinzu
        for(double y = bounds.getMinY(); y <= bounds.getMaxY() + tool.getDiameter() * overlap; y += tool.getDiameter()) {
		
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

        Point2D.Double start, end;
        ToolPath ptp = new ToolPath("Pocket part " + stage++);
        // Toolpath für Zick-Zack-Bewegung erzeugen
        while(!lineSegments.isEmpty()) {
        	LineSegment ls = lineSegments.get(j);
        	//System.out.println(j + "-" + ls.getY() + " -> " + ls);
    		start = new Point2D.Double(ls.get(0), ls.getY());
    		end = new Point2D.Double(ls.get(1), ls.getY());
    		//if(xEnd - xStart > tool.getDiameter()) { //Prüfen, ob das Segment nicht zu kurz
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

	/**
	 * The LineSegment class for the parallel pocket which contains a list with double values which consists the start and the end of the horizontal line segments of a shape.
	 * In field 0 the startX value of the x line, in field 1 the endX value. if there are side rivers in the shape the further startX and endX are in fields 2 and 3.
	 * That means that even indexes are always xStart values and odd numbers always xEnd number of a segment. 
	 * It also consists the y coordinate of the line segment.
	 */
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

	public int getPathOffset() {
		return pathOffset;
	}

	public void setPathOffset(int offset) {
		this.pathOffset = offset;
	}

	public boolean isPocket() {
		return pocket;
	}

	public void setPocket(boolean pocket) {
		this.pocket = pocket;
	}
	
	
		
}
