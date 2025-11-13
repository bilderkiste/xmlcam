package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generate 2D coordinates for a circle.
 * A circle is defined by the center point determined through a <p> tag and a radius defined through a <rad> tag.
 * The z-depth must be defined by the <z> tag.
 * An code example snippet:
 * <pre>{@code
 * <circle>
 * 		<p>200,200</p>
 * 		<rad>75</rad>
 * 		<seg>5</seg>
 *		<z>0,-1,0.1</z>
 * </circle>
 * }</pre>
 * @param node The node with the needed parameters
 */
public class Circle extends ElementClosed {
	
	private Tuple center;
	private Tuple radius;
	private Tuple segments;
	private int resolution; // mm
	private double phiStep;
	private boolean pocket;

	public Circle(Node node, Generator gen) {
		super(node, gen);
		center = null;
		segments = null;
		resolution = 2; // mm
		phiStep = 0;
		pocket = false;
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		
		NamedNodeMap map = node.getAttributes();

		try {
			if(map.getNamedItem("pocket").getTextContent().equals("parallel")) {
				pocket = true;
			}
			if(map.getNamedItem("path").getTextContent().equals("engraving")) {
				path = ElementClosed.ENGRAVING;
			} else if(map.getNamedItem("path").getTextContent().equals("inset")) {
				path = ElementClosed.INSET;
			} else if(map.getNamedItem("path").getTextContent().equals("outset")) {
				path = ElementClosed.OUTSET;
			}
		} catch(NullPointerException e) {
		
		} 
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				center = new Tuple(item);
			}
			if(item.getNodeName() == "rad") {
				radius = new Tuple(item);
			}
			if(item.getNodeName() == "seg") {
				segments = new Tuple(item);
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
	}

	@Override
	public void execute() {
		shape = new Path2D.Double();
		double phi = 0;

		float radiusv = radius.getValue(0).floatValue();;
		
		//center = addTranslation(center);
		
		if(segments == null) { 
			// Determine phiStep. If the circle is very small, the step should be < 0.5 (that means more G points on the circle
			phiStep = 2 * Math.PI / ((2 * radiusv * Math.PI) / resolution);
			if(phiStep > 0.5) {
				phiStep = 0.5;
			}
		} else {
			if(segments.getValue(0).intValue() < 3) {
				throw new IllegalArgumentException("Segment value has to be greater 2.");
			}
			phiStep =  2 * Math.PI / segments.getValue(0).intValue();
		}
	
		while(phi < 2 * Math.PI) {
			if(phi == 0) {
				shape.moveTo(radiusv * Math.sin(phi), radiusv * Math.cos(phi));
			} else {
				shape.lineTo(radiusv * Math.sin(phi), radiusv * Math.cos(phi));
			}
			phi += phiStep;
		}
		shape.closePath();
		
        at = new AffineTransform();
        at.translate(center.getValue(0).doubleValue(), center.getValue(1).doubleValue());
        at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
        
        Path2D.Double pathShape = null;
        
        if(path == ElementClosed.ENGRAVING) {
        	pathShape = shape;
        } else if(path == ElementClosed.INSET) {
        	pathShape = AreaToPath(createInsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        } else if(path == ElementClosed.OUTSET) {
        	pathShape = AreaToPath(createOutsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        }
        
        addToolPathes(generateToolPathes(pathShape, at, 0.1, new String("Circle at " + center + " with radius " + radius)));
		
		//create pockettoolpath
		if(pocket) {
			addToolPathes(createPocket(pathShape, at, gen.getTool()));
		}
		
		Main.log.log(Level.FINE, "Circle element: circle at {0} with translation {1} and radius {2} with {3} points. Step for phi is {4}.", new Object[] { center, gen.getTranslation(), radius, getToolPath(0).size(), phi });	
	}

}
