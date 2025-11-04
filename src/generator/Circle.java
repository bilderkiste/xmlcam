package generator;

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
		} catch(NullPointerException e) {
		
		} 
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				center = addTranslation(new Tuple(item));
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
		addToolPath(new String("Circle at " + center + " with radius " + radius));
		double phi = 0;
		float xCenter = center.getValue(0).floatValue();
		float yCenter = center.getValue(1).floatValue();
		float radiusv = radius.getValue(0).floatValue();;
		
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
			getToolPath(0).addPoint(xCenter + radiusv * Math.sin(phi), yCenter + radiusv * Math.cos(phi));
			phi += phiStep;
		}
		getToolPath(0).addPoint(xCenter + radiusv * Math.sin(0), yCenter + radiusv * Math.cos(0));
		
		//create pockettoolpath
		if(pocket) {
			addToolPath(createPocket(getToolPath(0)));
		}
		
		Main.log.log(Level.FINE, "Circle element: circle at (" + center.getValue(0) + "," + center.getValue(1) + ") with " + (int)(((Math.PI * 2) / phiStep) + 1) + " points. Step for phi is " + phiStep + ".");	
	}

}
