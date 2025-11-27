package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.Node;

import controller.Generator;
import model.ToolPath;
import model.Tuple;
/**
 * This abstract class represents an element, which can generate or consist of one ore more toolPathes.
 */
public abstract class Element {
	
	protected Generator gen;
	protected Node node;
	private String name;
	/**
	 * Arraylist with one or more toolpathes.
	 */
	protected ArrayList<ToolPath> toolPathes;
	protected Path2D.Double shape; 
	protected AffineTransform at;
	protected Tuple zLevel;
	
	public Element(Node node, Generator gen) {
		toolPathes = new ArrayList<ToolPath>();
		this.node = node;
		this.gen = gen;
		this.shape = null;
		this.at = null;
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
	 * Returns the shape of the element.
	 * @return The shape
	 */
	public Path2D.Double getShape() {
		return shape;
	}
	
	/**
	 * Returns the transformation of the shape.
	 * @return The transformation
	 */
	public AffineTransform getTransform() {
		return at;
	}
	
	/**
	 * Adds an new empty toolPath with the name (i.e. circle or rectangle) to the toolPathes ArrayList.
	 * The name will occur in the G-Code comments to refer the point/G-Code command to an object.
	 * @param name The name of the toolpath
	 */
	/*private void addToolPath(String name) {
		toolPathes.add(new ToolPath(name));
	}*/
	
	/**
	 * Adds an existing toolPath to the toolPathes ArrayList.
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
	public int getNumberOfToolPathes() {
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
	 * Adds an collection of ToolPathes to the Element.
	 * @param toolPathes The collection of ToolPathes
	 */
	public ArrayList<ToolPath> addToolPathes(ArrayList<ToolPath> tp) {
		toolPathes.addAll(tp);
		return toolPathes;
	}
	
	/**
	 * Generates the ToolPathes (ArrayList with Point2D) with the Flattening PathIterator from the Path2D.
	 * @param path The Path2D object.
	 * @param at The transformation.
	 * @param flatness The flatness
	 * @param name The name of the ToolPath
	 * @return The List with the ToolPathes
	 */
	public ArrayList<ToolPath> generateToolPathes(Path2D.Double path, AffineTransform at, double flatness, String name) {	        
	    PathIterator pi = path.getPathIterator(at, flatness); 
	    Point2D.Double startCoords = new Point2D.Double();
	    ArrayList<ToolPath> tpl = new ArrayList<ToolPath>();
	    
	    double[] coords = new double[2];
	
	    while (!pi.isDone()) {
        	int segmentType = pi.currentSegment(coords);
        	if(segmentType == PathIterator.SEG_MOVETO) {
        		tpl.add(new ToolPath(name)); // Create new empty ToolPath and add it to the toolPathes ArrayList
        		startCoords.setLocation(coords[0], coords[1]);
        		tpl.get(tpl.size() - 1).addPoint(coords[0], coords[1]);
        	} else if(segmentType == PathIterator.SEG_LINETO) {
        		tpl.get(tpl.size() - 1).addPoint(coords[0], coords[1]);
        	} else if(segmentType == PathIterator.SEG_CLOSE) {
        		tpl.get(tpl.size() - 1).addPoint(startCoords.getX(), startCoords.getY());
        	} 
      
        	pi.next();
	    }
	    return tpl;
	}

	/**
	 * Split an Path2D.Double with more than one shapes into subpathes.
	 * I.e. A text shape "Bit" will be splitted into six subshapes. 
	 * @param shape The shape
	 * @return The ArrayList with the subShapes
	 */
	public ArrayList<Path2D.Double> splitIntoSubpaths(Path2D.Double shape) {
        ArrayList<Path2D.Double> subpaths = new ArrayList<Path2D.Double>();
        PathIterator it = shape.getPathIterator(null);
        Path2D.Double current = null;
        double[] coords = new double[6];

        while (!it.isDone()) {
            int segType = it.currentSegment(coords);
            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    // Neuer Teilpfad beginnt
                    if (current != null && !current.getBounds2D().isEmpty()) {
                        subpaths.add(current);
                    }
                    current = new Path2D.Double();
                    current.moveTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    current.lineTo(coords[0], coords[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    current.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    current.curveTo(coords[0], coords[1], coords[2], coords[3],
                                    coords[4], coords[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    current.closePath();
                    break;
            }
            it.next();
        }

        if (current != null && !current.getBounds2D().isEmpty()) {
            subpaths.add(current);
        }

        return subpaths;
    }
	
	/**
	 * Check if one subShape contains other subShapes and merge it to one subShape.
	 * I.e. the word Bob has five subShapes. Output are the three subShapes 'B o b'
	 * @param parts The ArrayList with subShapes
	 * @return The ArrayList merged subShapes 
	 */
    public ArrayList<Path2D.Double> mergeContainedPaths(ArrayList<Path2D.Double> parts) {
        ArrayList<Path2D.Double> merged = new ArrayList<>();
        boolean[] used = new boolean[parts.size()];

        for (int i = 0; i < parts.size(); i++) {
            if (used[i]) continue;

            Path2D.Double a = parts.get(i);
            Area areaA = new Area(a);

            Path2D.Double combined = new Path2D.Double();
            combined.append(a, false);
            used[i] = true;

            Rectangle2D boundsA = areaA.getBounds2D();

            for (int j = i + 1; j < parts.size(); j++) {
                if (used[j]) continue;
                Path2D.Double b = parts.get(j);
                Area areaB = new Area(b);

                // Prüfe: liegt B vollständig innerhalb von A?
                if (boundsA.contains(areaB.getBounds2D())) {
                    combined.append(b, false);
                    used[j] = true;
                }
            }

            merged.add(combined);
        }

        return merged;
    }
    
    /**
     * Cleanup the generated ToolPathes for the element
     */
    public void purgePathes() {
    	for(int i = 0; i < toolPathes.size(); i++) {
    		if(!toolPathes.get(i).isPocket()) {
	    		if(isPathValid(toolPathes.get(i))) {
	    			deleteNarrowPoints(toolPathes.get(i));
	    			deleteCollinearPoints(toolPathes.get(i));
	    		} else {
	    			toolPathes.remove(i);
	    			i--;
	    		}
    		}
    			
    	}
   	
    }
    
    /**
     * Delete the almost equal points in a toolpath.
     * @param path The path to cleanup
     * @return The same cleaned path 
     */
    private ToolPath deleteNarrowPoints(ToolPath path) {
        double tol = 0.0001;

        for (int i = 1; i < path.size(); i++) {
            Point2D.Double p0 = path.get(i - 1);
            Point2D.Double p1 = path.get(i);

            double dx = p1.x - p0.x;
            double dy = p1.y - p0.y;

            if (dx * dx + dy * dy < tol) {
                path.remove(i);
                i--;
            }
        }

    	return path;
    }
    
    /**
     * Delete the collinear points.
     * @param path The path to cleanup
     * @return The same cleaned path 
     */
    private ToolPath deleteCollinearPoints(ToolPath path) {
    	double tol = 0.000001;
    	
    	for (int i = 1; i < path.size() - 1; i++) {
    		Point2D.Double prev = path.get(i - 1);
    		Point2D.Double current = path.get(i);
    		Point2D.Double next = path.get(i + 1);
  
	        // Flächeninhalt des Dreiecks ≈ 0?
	        double area =
		              (current.getX() - prev.getX()) * (next.getY() - prev.getY())
		            - (current.getY() - prev.getY()) * (next.getX() - prev.getX());
	        if(Math.abs(area) < tol) {
	        	path.remove(i);
	        	i--;
	        }
    	}
    	return path;
    }
    
    /**
     * Check if the Path2D generated from an Area is valid or degenerated.
     * @param path The ToolPath
     * @return true if valid, false if degenerated
     */
    private boolean isPathValid(ToolPath path) {
    	
    	if (path.size() < 3) return false; // Mindestens 3 Punkte für Fläche

    	// Eindeutige Punkte zählen
    	HashSet<String> unique = new HashSet<>();
    	for (Point2D.Double p : path) {
    	    unique.add(p.x + "|" + p.y);
    	}
    	if (unique.size() < 3) return false; // degeneriert / collinear / identisch

    	// Bounding box prüfen
    	double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
    	double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    	for (Point2D.Double p : path) {
    	    minX = Math.min(minX, p.getX());
    	    minY = Math.min(minY, p.getY());
    	    maxX = Math.max(maxX, p.getX());
    	    maxY = Math.max(maxY, p.getY());
    	}

    	if (maxX - minX < 0.01) return false; // praktisch Nullbreite → degeneriert
    	if (maxY - minY < 0.01) return false; // praktisch Nullhöhe → degeneriert

    	return true;
    }

    
    public void showToolPathes() {
    	System.out.println("Toolpathes for " + this.getName());
    	for(int i = 0; i < toolPathes.size(); i++) {
    		ToolPath tp = toolPathes.get(i);
    		for(int j = 0; j < tp.size(); j++) {
    			System.out.println(tp.getX(j) + " - " + tp.getY(j));
    		}
    		System.out.println("=====");
    	}
    }


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
