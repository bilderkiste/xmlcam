package generator;

import java.util.ArrayList;
import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import controller.Tuple;
import main.Main;
import model.ToolPathPoint;

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
		for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}

		toolPath.add(new ToolPathPoint(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue(), "line"));
		toolPath.add(new ToolPathPoint(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue(), "line"));
		
		Main.log.log(Level.FINE, "Line element: line from (" + xmlPoints.get(0).getValue(0) + ", " + xmlPoints.get(0).getValue(1) + ") to (" + xmlPoints.get(1).getValue(0) + ", " + xmlPoints.get(1).getValue(1) + ").");
	}

}
