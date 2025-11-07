package model;

import java.awt.geom.Path2D;
//import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ToolPath extends Path2D.Double {
	
	private static final long serialVersionUID = 1L;
	protected String name;
	private ArrayList<Point2D.Double> vertices;

	public ToolPath(String name) {
		super();
		this.name = name;
		this.vertices = null;
	}
	
	/**
	 * Adds a new coordinate to the toolPath.
	 * @param coords The new coordinates.
	 */
	public void addPoint(double x, double y) {
		if(vertices == null) {
			vertices = new ArrayList<>();
			moveTo(x, y);
		}
		lineTo(x, y);
		vertices.add(new Point2D.Double(x, y));
	}
	
	/**
	 * Returns the x value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The x value
	 */
	public BigDecimal getX(int index) {
		return new BigDecimal(vertices.get(index).getX());
	}
	
	/**
	 * Returns the y value of the coordinate at the index of the toolPath.
	 * @param index The index
	 * @return The y value
	 */
	public BigDecimal getY(int index) {
		return new BigDecimal(vertices.get(index).getY());
	}
	
	/**
	 * Returns the name of the toolPath.
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	public int size() {
		return vertices.size();
	}
	
	/*public ArrayList<Point2D.Double> getVerticesFromPath(Path2D.Double path, double flatness) {
		vertices = new ArrayList<>();
	    
	    // Erhalte einen "Flattening" PathIterator
	    // Er wandelt SEG_QUADTO und SEG_CUBICTO automatisch in viele SEG_LINETO um.
	    PathIterator pi = path.getPathIterator(null, flatness); 
	    
	    double[] coords = new double[6];

	    while (!pi.isDone()) {
	        int segmentType = pi.currentSegment(coords);

	        // Uns interessieren nur noch MOVETO (neuer Startpunkt) 
	        // und LINETO (nächster Punkt)
	        if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
	            vertices.add(new Point2D.Double(coords[0], coords[1]));
	        }
	        // SEG_CLOSE wird ignoriert, da der LINETO-Pfad bereits dorthin führt
	        
	        pi.next();
	    }
	    
	    return vertices;
	}*/
}
