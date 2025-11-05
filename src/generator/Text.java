package generator;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

public class Text extends ElementClosed {
	
	private Tuple xmlPoint;
	private String content;
	private Font font;
	private double flatness;

	public Text(Node node, Generator gen) {
		super(node, gen);
		xmlPoint = null;
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		int size = 0;
		String type = null;
		String style = null;
		HashMap<String, Integer> styleMap = new HashMap<>();
		styleMap.put("PLAIN", 0);
		styleMap.put("BOLD", 1);
		styleMap.put("ITALIC", 2);
		styleMap.put("BOLDITALIC", 3);
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "content") {
				content = item.getTextContent();
			}
			if(item.getNodeName() == "p") {
				xmlPoint = addTranslation(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
			if(item.getNodeName() == "size") {
				size = Integer.parseInt(item.getTextContent());
			}
			if(item.getNodeName() == "type") {
				type = new String(item.getTextContent());
			}
			if(item.getNodeName() == "style") {
				style = new String(item.getTextContent().toUpperCase()); 
			}
			if(item.getNodeName() == "flatness") {
				flatness = Double.parseDouble(item.getTextContent()); 
			}
		}
		
		if(size < 1) {
			size = 10;
		}
		if(style ==  null) {
			style = "PLAIN";
		}
		if(flatness < 0.01) {
			flatness = 0.5;
		}
			
		try {
			font = new Font(type, styleMap.get(style), size);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException("Invalid argments in text element.");
			
		}
	
	}

	@Override
	public void execute() {
		// Holt die Vektor-Umrisse für den gesamten Text
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector glyphVector = font.createGlyphVector(frc, content);
        Shape textShape = glyphVector.getOutline();
        
        // Transformation, um den Text an die Startposition (startX, startY) zu verschieben
        AffineTransform at = AffineTransform.getTranslateInstance(xmlPoint.getValue(0).doubleValue(), xmlPoint.getValue(1).doubleValue());
        at.scale(1.0, -1.0);
        
        // Der PathIterator, der Kurven in flache Liniensegmente umwandelt
        PathIterator pi = textShape.getPathIterator(at, flatness);

        double[] coords = new double[2]; // Speichert Koordinaten vom PathIterator
        
        while (!pi.isDone()) {
        	int segmentType = pi.currentSegment(coords);
        	if(segmentType == 0) {
        		addToolPath(new String("Text"));
        	}
        		
        	getToolPath(getToolPathSize() - 1).addPoint(coords[0], coords[1]);
        
            //System.out.println(segmentType + " - " + coords[0] + " " + coords[1]);// +" " + coords[2]+ " " + coords[3] +" " + coords[4] + " " + coords[5]);
            pi.next();
        }
        Main.log.log(Level.FINE, "Text element: text '" + content + "' at " + xmlPoint + " with type " + font.getFontName() + " size " + font.getSize() + " and flatness " + flatness);
	}

}
