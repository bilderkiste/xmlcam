package generator;

import java.util.ArrayList;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generate 2D coordinates for a rectangle.
 * A rectangle is defined by two points for the diagonal edges determined through a <p> tag.
 * The z-depth must be defined by the <z> tag.
 * An code example snippet:
 * <pre>{@code
 * <rectangle>
 * 		<p>100,100</p>
 * 		<p>150,150</p>
 *		<z>0,-1,0.1</z>
 * </rectangle>
 * }</pre>
 * @param node The node with the needed parameters
 */

public class Rectangle extends ElementClosed {
	
	private ArrayList<Tuple> xmlPoints;
	private boolean pocket;
	
	public Rectangle(Node node, Generator gen) {
		super(node, gen);
		xmlPoints = new ArrayList<Tuple>();
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
				xmlPoints.add(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
	}

	@Override
	public void execute() {
		for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}
		
		addToolPath(new String("Rectangle from " + xmlPoints.get(0) + " to " + xmlPoints.get(1)));
		
		getToolPath(0).addPoint(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue());
		getToolPath(0).addPoint(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue());
		getToolPath(0).addPoint(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue());
		getToolPath(0).addPoint(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue());
		getToolPath(0).addPoint(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue());
		
		//create pockettoolpath
		if(pocket) {
			addToolPath(createPocket(getToolPath(0)));
		}
		
		Main.log.log(Level.FINE, "Rectangle element: rectangle from (" + xmlPoints.get(0).getValue(0) + ", " + xmlPoints.get(0).getValue(1) + ") to (" + xmlPoints.get(1).getValue(0) + ", " + xmlPoints.get(1).getValue(1) + ").");	
	}

}
