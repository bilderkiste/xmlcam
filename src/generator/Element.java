package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.w3c.dom.Node;

import controller.Generator;
import model.Tool;
import model.ToolPath;
import model.Tuple;
/**
 * This abstract class represents an element, which can generate or consist of one ore more toolPathes.
 */
abstract class Element {
	
	protected Generator gen;
	protected Node node;
	protected String name;
	/**
	 * Arraylist with one or more toolpathes.
	 */
	protected ArrayList<ToolPath> toolPathes;
	protected Path2D.Double shape; 
	protected Tuple zLevel;
	protected Tool tool;
	
	public Element(Node node, Generator gen) {
		toolPathes = new ArrayList<ToolPath>();
		this.node = node;
		this.gen = gen;
		this.tool = null;
		this.shape = null;
	}
	
	public abstract void extract() throws IllegalArgumentException;
	
	public abstract void execute();
	
	/**
	 * Returns the toolPath at the index.
	 * @param index The index
	 * @return The toolPath
	 */
	public ToolPath getToolPath(int index) {
		return toolPathes.get(index);
	}
	
	/**
	 * Returns all toolPathes in an ArrayList.
	 * @param index The index
	 * @return The toolPath
	 */
	public ArrayList<ToolPath> getToolPathes() {
		return toolPathes;
	}
	
	
	/**
	 * Adds an new toolPath with the name (i.e. circle or rectangle) to the list.
	 * The name will occur in the G-Code comments to refer the point/G-Code command to an object.
	 * @param name The name of the toolpath
	 */
	public void addToolPath(String name) {
		toolPathes.add(new ToolPath(name));
	}
	
	/**
	 * Adds an existing toolPath to the list.
	 * The name will occur in the G-Code comments to refer the point/G-Code command to an object.
	 * @param toolPath The toolpath
	 */
	public void addToolPath(ToolPath toolPath) {
		toolPathes.add(toolPath);
	}
	
	/**
	 * Determine the number of current toolPathes in the Element.
	 * @return The number
	 */
	public int getToolPathSize() {
		return toolPathes.size();
	}
	
	/**
	 * Returns the zLevel tuple (i.E. (0,-1,0.1).
	 * @return The tuple
	 */
	public Tuple getZLevel() {
		return zLevel;
	}
	
	/**
	 * Adds the translation to the x and y values of a point.
	 * @param pint The point.
	 * @return The point translated.
	 */
	protected Tuple addTranslation(Tuple point) {
		point.setValue(0, point.getValue(0).doubleValue() + gen.getTranslateX().doubleValue());
		point.setValue(1, point.getValue(1).doubleValue() + gen.getTranslateY().doubleValue());
		return point;
	}
	
	public void getVerticesFromPath(Path2D.Double path, AffineTransform at, double flatness) {	        
	    PathIterator pi = path.getPathIterator(at, flatness); 
	    
	    double[] coords = new double[2];
	
	    while (!pi.isDone()) {
	    	
        	int segmentType = pi.currentSegment(coords);
        	if(segmentType == PathIterator.SEG_MOVETO) {
        		toolPathes.add(new ToolPath("Text"));
        	}
        		
        	getToolPath(getToolPathSize() - 1).addPoint(coords[0], coords[1]);
        
            //System.out.println(segmentType + " - " + coords[0] + " " + coords[1]);// +" " + coords[2]+ " " + coords[3] +" " + coords[4] + " " + coords[5]);
            pi.next();
	    }	    
	}

}
