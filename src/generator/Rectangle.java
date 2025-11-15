package generator;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tool;
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
	
	public Rectangle(Node node, Generator gen) {
		super(node, gen);
		xmlPoints = new ArrayList<Tuple>();
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();

		setClosedElementsAttributeVars(node.getAttributes());
		
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
		shape.lineTo(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue());
		shape.lineTo(xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue());
		shape.lineTo(xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue());
		shape.closePath();
		
		at = new AffineTransform();
		at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
		
        Path2D.Double pathShape = null;
        
        if(super.getPath() == ElementClosed.ENGRAVING) {
        	pathShape = shape;
        } else if(super.getPath() == ElementClosed.INSET) {
        	pathShape = AreaToPath(createInsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        } else if(super.getPath() == ElementClosed.OUTSET) {
        	pathShape = AreaToPath(createOutsetArea(new Area(shape), (float) gen.getTool().getRadius()));
        }
		
		addToolPathes(generateToolPathes(pathShape, at, 0.1, new String("Rectangle from " + xmlPoints.get(0) + " to " + xmlPoints.get(1))));
		
		//create pockettoolpath
		if(isPocket()) {
			addToolPathes(createPocket(pathShape, at, gen.getTool()));
		}

		Main.log.log(Level.FINE, "Rectangle element: rectangle from {0} to {1} with translation {2}.", new Object[] { xmlPoints.get(0), xmlPoints.get(1), gen.getTranslation() } );	
	}

}
