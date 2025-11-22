package generator;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.ToolPath;
import model.Tuple;

/**
 * Generate 2D coordinates for a text.
 * A text must defined by a start point bottom left determined through a <p> tag.
 * The Text must defined by the <content> tag.
 * The z-depth must be defined by the <z> tag.
 * Optional tags are <size> for font size in point, <type> for font family, <style> for bold or italic styles and flatness for accuracy. 
 * An code example snippet:
 * <pre>{@code
	<text>
		<content>Ein toller Tag</content>
		<p>20,20</p>
		<size>10</size>
		<type>Calibri</type>
		<style>plain</style>
		<flatness>0.1</flatness>
		<z>0,-1,1</z>
	</text>
 * }</pre>
 */
public class Text extends ElementClosed {
	
	private Tuple xmlPoint;
	private String content;
	private Font font;
	private double flatness;

	public Text(Node node, Generator gen) {
		super(node, gen);
		xmlPoint = null;
		content = null;
		font = null;
		flatness = 0.5;
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
		
		setClosedElementsAttributeVars(node.getAttributes());
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "content") {
				content = item.getTextContent();
			}
			if(item.getNodeName() == "p") {
				xmlPoint = new Tuple(item);
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
		
		super.setName(new String("Text " + content));
	
	}

	@Override
	public void execute() {
		// Holt die Vektor-Umrisse für den gesamten Text
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector glyphVector = font.createGlyphVector(frc, content);
        shape = new Path2D.Double(glyphVector.getOutline());
    	
        // Transformation, um den Text an die Startposition (startX, startY) zu verschieben
        at = new AffineTransform();
        at.translate(xmlPoint.getValue(0).doubleValue(), xmlPoint.getValue(1).doubleValue());
        at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
        at.scale(1.0, -1.0);
        
        ArrayList<Path2D.Double> subShapes = mergeContainedPaths(splitIntoSubpaths(shape));
        for(int i = 0; i < subShapes.size(); i++) {
        	Path2D.Double pathShape = createOffsetShape(subShapes.get(i));
        	addToolPathes(generateToolPathes(pathShape, at, flatness, new String("Text: " + content)));
    		if(isPocket()) {
    			ArrayList<ToolPath> pockets = createPocket(subShapes.get(i), at, gen.getTool());
    			//prüfen ob leere ToolPath vorhanden sind um (tmp)
    			for(int j = 0; j < pockets.size(); j++) {
    				if(pockets.get(j).size() == 0) {
    					pockets.remove(j);
    					j--;
    				}
    			}
    			addToolPathes(pockets);
    			//toolPathes.clear();
    			//toolPathes.add(createPocket(subShapes.get(i), at, new Tool(2)));
    		}
        }
        
        //addToolPathes(generateToolPathes(pathShape, at, 0.1, new String("test")));
       
		/*for(int i = 0; i < toolPathes.size(); i++) {
			System.out.println(toolPathes.get(i).getName() + " " + toolPathes.get(i));
		}*/
        
        Main.log.log(Level.FINE, "Text element: text {0} at {1} and translation {2} with type {3} size {4} and flatness {5}" , new Object[] { content, xmlPoint, gen.getTranslation(), font.getFontName(), font.getSize(), flatness } );
	}

}
