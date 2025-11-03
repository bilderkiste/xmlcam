package generator;

import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import controller.Tuple;
import main.Main;
import model.ToolPathPoint;

/**
 * Generate 2D coordinates for a drill.
 * The drill is defined by one points defined with <p> tags.
 * The z-depth must be defined by the <z> tag.
 * An code example snippet:
 * <pre>{@code
 * <drill>
 * 		<p>40,20</p>
 * 		<z>0,-1,0.1</z>
 * </drill>
 * }</pre>
 * @param node The node with the needed parameters
 */


public class Drill extends Element {

	private Tuple xmlPoint;
	
	public Drill(Node node, Generator gen) {
		super(node, gen);
		xmlPoint = null;
		zLevel = null;
	}
	
	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				xmlPoint = addTranslation(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		// cut the z tuple
		if(zLevel.size() > 2) {
			zLevel = zLevel.subList(0,1);
		}
		
		// add one whole step to endZ
		zLevel.addValue(zLevel.getValue(1).abs().doubleValue());
	}
	

	@Override
	public void execute() {
		toolPath.add(new ToolPathPoint(xmlPoint.getValue(0).doubleValue(), xmlPoint.getValue(1).doubleValue(), "drill"));
		Main.log.log(Level.FINE, "Drill element: drill at (" + xmlPoint.getValue(0) + ", " + xmlPoint.getValue(1) + ").");			
	}
}
