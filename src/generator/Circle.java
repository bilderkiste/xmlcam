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

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generate 2D path for a circle.
 * A circle is defined by the center determined through a <center> tag with attributes x and y and radius defined through a <radius> tag with a value attribute.
 * The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
 * Optional attributes in the <options> tag are segments for the definition of the number of segments i.e. 6 for an hexagon. 
 * Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 
 * An code example snippet:
 * <pre>{@code
	<circle tool="t2">
		<center x="60" y="30" />
		<radius value="20" /> 
		<depth start="0" end="-1" step="0.1" />
		<options segments="5" offset="inset" pocket="parallel" />
	</circle>
 * }</pre>
 * @param node The node with the needed parameters
 */
public class Circle extends ElementClosed {
	
	private Tuple center;
	private double radius;
	private int segments;
	private int resolution; // mm
	private double phiStep;

	public Circle(Node node, Generator gen) {
		super(node, gen);
		center = null;
		radius = 0;
		segments = 0;
		resolution = 2; // mm
		phiStep = 0;
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		
		NamedNodeMap map = node.getAttributes();
		
		map = node.getAttributes();
		setTool(gen.getTool(map.getNamedItem("tool").getTextContent()));
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "center") {
				map = item.getAttributes();
				double coords[] = new double[2];
				coords[0] = Double.parseDouble(map.getNamedItem("x").getTextContent());
				coords[1] = Double.parseDouble(map.getNamedItem("y").getTextContent());
				//xmlPoint = new Point2D.Double(x, y);
				center = new Tuple(coords);
			}
			if(item.getNodeName() == "radius") {
				map = item.getAttributes();
				radius = Double.parseDouble(map.getNamedItem("value").getTextContent());
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
					segments = Integer.parseInt(map.getNamedItem("segments").getTextContent());
				} catch (NullPointerException e) {
				}
				setClosedElementsAttributeVars(map);
			}
		}
		
	}

	@Override
	public void execute() {
		shape = new Path2D.Double();
		double phi = 0;
		
		if(segments == 0) { 
			// Determine phiStep. If the circle is very small, the step should be < 0.5 (that means more G points on the circle
			phiStep = 2 * Math.PI / ((2 * radius * Math.PI) / resolution);
			if(phiStep > 0.5) {
				phiStep = 0.5;
			}
		} else {
			if(segments < 3) {
				throw new IllegalArgumentException("Segment value has to be greater 2.");
			}
			phiStep =  2 * Math.PI / segments;
		}
	
		while(phi < 2 * Math.PI) {
			if(phi == 0) {
				shape.moveTo(radius * Math.sin(phi), radius * Math.cos(phi));
			} else {
				shape.lineTo(radius * Math.sin(phi), radius * Math.cos(phi));
			}
			phi += phiStep;
		}
		shape.closePath();
		
        at = new AffineTransform();
        at.translate(center.getValue(0).doubleValue(), center.getValue(1).doubleValue());
        at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
        
        Path2D.Double pathShape = createOffsetShape(shape);
        
        super.setName(new String("Circle at " + center + " with radius " + radius));
        		
        addToolPathes(generateToolPathes(pathShape, at, 0.1, super.getName()));
        	
		//create pockettoolpath
		if(isPocket()) {
			addToolPathes(createPocket(pathShape, at, getTool()));
		}
		
		Main.log.log(Level.FINE, "Circle element: circle at {0} with translation {1} and radius {2} with {3} points. Step for phi is {4}.", new Object[] { center, gen.getTranslation(), radius, getToolPath(0).size(), phi });	
	}

}
