package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

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
				xmlPoint = new Tuple(item);
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
		shape = new Path2D.Double();
		
		shape.moveTo(xmlPoint.getValue(0).doubleValue(), xmlPoint.getValue(1).doubleValue());
		
		AffineTransform at = new AffineTransform();
		at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
		
		addToolPathes(generateToolPathes(shape, at, 0.1, new String("Drill at " + xmlPoint)));

		Main.log.log(Level.FINE, "Drill element: drill at {0} and translation {1}", new Object[] { xmlPoint, gen.getTranslation() } );			
	}
}
