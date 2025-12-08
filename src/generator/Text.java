/*********************************************************************\
 * TableViewDummyModelChangeListener.java - xmlCam G-Code Generator  *
 * Copyright (C) 2025, Christian Kirsch                              *
 *                                                                   *
 * This program is free software; you can redistribute it and/or     *
 * modify it under the terms of the GNU General Public License as    *
 * published by the Free Software Foundation; either version 3 of    *
 * the License, or (at your option) any later version.               *
 *                                                                   *
 * This program is distributed in the hope that it will be useful,   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the     *
 * GNU General Public License for more details.                      *
 *                                                                   *
 * You should have received a copy of the GNU General Public License *
 * along with this program; if not, write to the Free Software       *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.         *
\*********************************************************************/

package generator;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.ToolPath;
import model.Tuple;

/**
 * This class generates an 2D path for a text.
 * The Text must defined by the <content> tag.
 * The position of the text is determined through a <point> tag with attributes x and y defining bottom left.
 * The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
 * Optional attributes in the <options> tag are size for font size in point, font for font family, style for bold or italic styles and flatness for accuracy. 
 * Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 
 * An code example snippet:
 * <pre>{@code
	<text tool="t1">
		<content>Guten Morgen!</content>
		<point x="10" y="50"/>
		<depth start="0" end="-1" step="0.1"/>
		<options size="20" font="C059" style="bold" flatness="0.1" offset="outset"/>
	</text>
 * }</pre>
 */
public class Text extends ElementClosed {
	
	private Tuple point;
	private String content;
	private Font font;
	private double flatness;

	public Text(Node node, Generator gen) {
		super(node, gen);
		point = null;
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
		NamedNodeMap map;
		HashMap<String, Integer> styleMap = new HashMap<>();
		styleMap.put("PLAIN", 0);
		styleMap.put("BOLD", 1);
		styleMap.put("ITALIC", 2);
		styleMap.put("BOLDITALIC", 3);
		
		map = node.getAttributes();
		setTool(gen.getTool(map.getNamedItem("tool").getTextContent()));
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);

			if(item.getNodeName() == "content") {
				content = item.getTextContent();
			}
			if(item.getNodeName() == "point") {
				map = item.getAttributes();
				double coords[] = new double[2];
				coords[0] = Double.parseDouble(map.getNamedItem("x").getTextContent());
				coords[1] = Double.parseDouble(map.getNamedItem("y").getTextContent());
				//xmlPoint = new Point2D.Double(x, y);
				point = new Tuple(coords);
			}
			if(item.getNodeName() == "depth") {
				map = item.getAttributes();
				double values[] = new double[3];
				values[0] = Double.parseDouble(map.getNamedItem("start").getTextContent());
				values[1] = Double.parseDouble(map.getNamedItem("end").getTextContent());
				values[2] = Double.parseDouble(map.getNamedItem("step").getTextContent());
				zLevel = new Tuple(values);
			}
			
			if(item.getNodeName() == "options") {
				map = item.getAttributes();
				try {
					size = Integer.parseInt(map.getNamedItem("size").getTextContent());
				} catch (NullPointerException e) {
				}
				try {
					type = map.getNamedItem("font").getTextContent();
				} catch (NullPointerException e) {
				}
				try {
					style = map.getNamedItem("style").getTextContent().toUpperCase();
				} catch (NullPointerException e) {
				}
				try {
					flatness = Double.parseDouble(map.getNamedItem("flatness").getTextContent());
				} catch (NullPointerException e) {
				}
				setClosedElementsAttributeVars(map);
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
			throw new IllegalArgumentException("Invalid arguments in text element.");
			
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
        at.translate(point.getValue(0).doubleValue(), point.getValue(1).doubleValue());
        at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
        at.scale(1.0, -1.0);
        
        ArrayList<Path2D.Double> subShapes = mergeContainedPaths(splitIntoSubpaths(shape));
        for(int i = 0; i < subShapes.size(); i++) {
        	Path2D.Double pathShape = createOffsetShape(subShapes.get(i));
        	addToolPathes(generateToolPathes(pathShape, at, flatness, new String("Text: " + content)));
    		if(isPocket()) {
    			ArrayList<ToolPath> pockets = createPocket(pathShape, at, getTool());
    			//prüfen ob leere ToolPath vorhanden sind um (tmp)
    			/*for(int j = 0; j < pockets.size(); j++) {
    				if(pockets.get(j).size() == 0) {
    					pockets.remove(j);
    					j--;
    				}
    			}*/
    			addToolPathes(pockets);
    		}
        }
        
        Main.log.log(Level.FINE, "Text element: text {0} at {1} and translation {2} with type {3} size {4} and flatness {5}" , new Object[] { content, point, gen.getTranslation(), font.getFontName(), font.getSize(), flatness } );
	}

}
