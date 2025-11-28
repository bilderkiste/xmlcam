/*********************************************************************\
 * TableViewDummyModelChangeListener.java - xmlCam G-Code Generator  *
 * Copyright (C) 2025, Christian Kirsch                              *
 *                                                                   *
 * This program is free software; you can redistribute it and/or     *
 * modify it under the terms of the GNU General Public License as    *
 * published by the Free Software Foundation; either version 3 of    *
 * the License, or (at your option) any later version.               *
 *                                                                   *
 * This program is distributed in the hope that it will be useful,   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the     *
 * GNU General Public License for more details.                      *
 *                                                                   *
 * You should have received a copy of the GNU General Public License *
 * along with this program; if not, write to the Free Software       *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.         *
\*********************************************************************/

package generator;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
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
            int segmentType = iterator.currentSegment(coords);
            
            switch (segmentType) {
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
		
		// Wenn kein shape vorhanden, dann Liste ohne ToolPath zurückgeben
		if(shape.getCurrentPoint() == null) {
			return pocketToolPathes;
		}
	
		shape.closePath();
		
		// Wandle die transformierte Shape in eine Area um
        Area area = new Area(at.createTransformedShape(shape));
        
        // Erzeuge mit einem inset eine kleinere Kontur
        Area areaInset = createInsetArea(area, (float) (tool.getRadius() * overlap));
                   
        // Hole die Bounding Box der gesamten Form
        Rectangle2D bounds = areaInset.getBounds2D();
    
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
            scanArea.intersect(areaInset);

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
            
            lineSegments.add(ls);
        }
        
        int stage = 0;
        int j = 0;

        Point2D.Double start, end;
        ToolPath ptp = new ToolPath("Pocket part " + stage++ + " for " + this.getName(), true);
        // Toolpath für Zick-Zack-Bewegung erzeugen
        while(!lineSegments.isEmpty()) {
        	LineSegment ls = lineSegments.get(j);

    		start = new Point2D.Double(ls.get(0), ls.getY());
    		end = new Point2D.Double(ls.get(1), ls.getY());
        	if(directionLeftToRight) {
        		ptp.addPoint(start);
                ptp.addPoint(end);
	        } else {
	        	ptp.addPoint(end);
        		ptp.addPoint(start);
	        }
        	// Wenn Wechselline zwischen den Segmenten das Shape verlässt, neuen Path anfangen
            try {
            	LineSegment nextLs = lineSegments.get(j + 1);
            	
	            if(directionLeftToRight) {
		            if(lineLeavesShape(area, end, new Point2D.Double(nextLs.get(1), nextLs.getY()))) {
		            	pocketToolPathes.add(ptp);
		            	ptp = new ToolPath("Pocket part " + stage++ + " for " + this.getName(), true);
		            } 
	            } else {
		            if(lineLeavesShape(area, start, new Point2D.Double(nextLs.get(0), nextLs.getY())))	{
		            	pocketToolPathes.add(ptp);
		            	ptp = new ToolPath("Pocket part " + stage++ + " for " + this.getName(), true);
		            }
	            }
            } catch(IndexOutOfBoundsException e) {
            	//e.printStackTrace();
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
            
            // Wenn die erste 0 und 1 Segmente leer, dann 2 und 3 Segmente in neuem Toolpath abarbeiten.
            if(j >= lineSegments.size()) {
            	j = 0;
            	pocketToolPathes.add(ptp);
            	ptp = new ToolPath("Pocket part " + stage++ + " for " + this.getName(), true);
            }
 
        }
            
		return pocketToolPathes;
	}
	
	private boolean lineLeavesShape(Area shape, Point2D.Double start, Point2D.Double end) {
		Line2D.Double line = new Line2D.Double(start, end);
		
	    // Start- und Endpunkte müssen im Shape liegen
	    if (!shape.contains(line.getP1()) || !shape.contains(line.getP2())) {
	    	System.out.println("check for " + line.getP1() + " , " + line.getP2() + " - true1");
	        return true; // Linie beginnt/endet nicht im Shape → verlässt Shape
	    }

	    // Prüfen, ob die Linie die Formgrenze schneidet
	    // Einzelne Segmente des Path iterieren und Überschneidungen prüfen
	    PathIterator it = shape.getPathIterator(null, 0.1);
	    
	    double[] coords = new double[6];

	    double lastX = 0, lastY = 0;
	    double startX = 0, startY = 0;

	    while (!it.isDone()) {
	        int type = it.currentSegment(coords);

	        switch (type) {
	            case PathIterator.SEG_MOVETO:
	                startX = lastX = coords[0];
	                startY = lastY = coords[1];
	                break;
	            case PathIterator.SEG_LINETO:
	                Line2D edge = new Line2D.Double(lastX, lastY, coords[0], coords[1]);
	                if (line.intersectsLine(edge)) {
	                	return true;
	                }
	                lastX = coords[0];
	                lastY = coords[1];
	                break;
	            case PathIterator.SEG_CLOSE:
	                Line2D closingEdge = new Line2D.Double(lastX, lastY, startX, startY);
	                if (line.intersectsLine(closingEdge)) {
	                	return true;
	                }
	                break;
	        }

	        it.next();
	    }

	    return false;
	}


	/**
	 * The LineSegment class for the parallel pocket which contains a list with double values which represent the start and the end of the horizontal line segments of a shape.
	 * The field 0 has the startX value of the x line, the field 1 the endX value. if there are side rivers in the shape the further startX and endX are in fields 2 and 3, 4 and 5 ....
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
