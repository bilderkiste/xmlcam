package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generate 2D coordinates for a line.
 * The line is defined by two points defined with <p> tags.
 * The z-depth must be defined by the <z> tag.
 * An code example snippet:
 * <pre>{@code
 * <line>
 * 		<p>40,20</p>
 * 		<p>80,20</p>
 * 		<z>0,-1,0.1</z>
 * </line>
 * }</pre>
 * @param node The node with the needed parameters
 */

public class Line extends Element {

	private ArrayList<Tuple> xmlPoints;
	
	public Line(Node node, Generator gen) {
		super(node, gen);
		xmlPoints = new ArrayList<Tuple>();
		zLevel = null;
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				xmlPoints.add(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}		
	}
	
	@Override
	public void execute() {
		shape = new Path2D.Double();
		
		/*for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}*/
		
		shape.moveTo(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue());
		shape.lineTo(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue());
		
        AffineTransform at = new AffineTransform();
        at.translate(gen.getTranslateX().doubleValue(), gen.getTranslateY().doubleValue()); //Translation from translation tag
        
        addToolPath(shape, at, 0.1, new String("Line from " + xmlPoints.get(0) + " to " + xmlPoints.get(1)));
		
		Main.log.log(Level.FINE, "Line element: line from " + xmlPoints.get(0) + " to " + xmlPoints.get(1));
	}

}
