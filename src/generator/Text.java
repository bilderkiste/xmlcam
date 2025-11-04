package generator;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import model.Tuple;

public class Text extends ElementClosed {
	
	private Tuple xmlPoint;
	private String text;
	private Font font;
	private double flatness;

	public Text(Node node, Generator gen) {
		super(node, gen);
		xmlPoint = null;
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
		
		font = new Font("Arial", Font.PLAIN, 10);
		text = "Ja va";
		flatness = 0.2;
	}

	@Override
	public void execute() {
		// Holt die Vektor-Umrisse für den gesamten Text
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector glyphVector = font.createGlyphVector(frc, text);
        Shape textShape = glyphVector.getOutline();
        
        // Transformation, um den Text an die Startposition (startX, startY) zu verschieben
        AffineTransform at = AffineTransform.getTranslateInstance(xmlPoint.getValue(0).doubleValue(), xmlPoint.getValue(1).doubleValue());
        at.scale(1.0, -1.0);
        
        // Der PathIterator, der Kurven in flache Liniensegmente umwandelt
        PathIterator pi = textShape.getPathIterator(at, flatness);

        double[] coords = new double[6]; // Speichert Koordinaten vom PathIterator
        
        /*while (!pi.isDone()) {
        	int segmentType = pi.currentSegment(coords);
        	if(segmentType == 0) {
        		addToolPath(new String("Text"));
        	}
        		
        	//getToolPath(getToolPathSize()).addPoint(coords[0], coords[1]);
        
            System.out.println(segmentType + " - " + coords[0] + " " + coords[1] +" " + coords[2]+ " " + coords[3] +" " + coords[4] + " " + coords[5]);
            pi.next();
        }*/
	}

}
