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
import java.util.ArrayList;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generates an 2D path for a line.
 * The line is defined by two points defined with <point> tags with attributes x and y.
 * The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
 * An code example snippet:
 * <pre>{@code
 * <line tool="t1">
 *		<point x="10" y="150"/>
 *		<point x="10" y="200"/>
 *		<depth start="0" end="-1" step="0.1"/>
 * </line>
 * }</pre>
 * @param node The node with the needed parameters
 */

public class Line extends Element {

	private ArrayList<Tuple> points;
	
	public Line(Node node, Generator gen) {
		super(node, gen);
		points = new ArrayList<Tuple>();
		zLevel = null;
	}

	@Override
	public void extract() throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		
		NamedNodeMap map = node.getAttributes();
		
		map = node.getAttributes();
		setTool(gen.getTool(map.getNamedItem("tool").getTextContent()));
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "point") {
				map = item.getAttributes();
				double coords[] = new double[2];
				coords[0] = Double.parseDouble(map.getNamedItem("x").getTextContent());
				coords[1] = Double.parseDouble(map.getNamedItem("y").getTextContent());
				points.add(new Tuple(coords));
			}
			if(item.getNodeName() == "depth") {
				map = item.getAttributes();
				double values[] = new double[3];
				values[0] = Double.parseDouble(map.getNamedItem("start").getTextContent());
				values[1] = Double.parseDouble(map.getNamedItem("end").getTextContent());
				values[2] = Double.parseDouble(map.getNamedItem("step").getTextContent());
				zLevel = new Tuple(values);
			}
		}		
	}
	
	@Override
	public void execute() {
		shape = new Path2D.Double();
		
		shape.moveTo(points.get(0).getValue(0).doubleValue(), points.get(0).getValue(1).doubleValue());
		shape.lineTo(points.get(1).getValue(0).doubleValue(), points.get(1).getValue(1).doubleValue());
		
        at = new AffineTransform();
        at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
        
        addToolPathes(generateToolPathes(shape, at, 0.1, new String("Line from " + points.get(0) + " to " + points.get(1))));
		
		Main.log.log(Level.FINE, "Line element: line from {0} to {1} with translation {3}.",  new Object[] { points.get(0), points.get(1), gen.getTranslation() } );
	}

}
