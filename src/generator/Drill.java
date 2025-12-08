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
import java.awt.geom.Point2D;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Generator;
import main.Main;
import model.Tuple;

/**
 * Generates an 2D coordinate for a drill.
 * The drill is defined by one point defined with a <point> tag and attributes x and y.
 * The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level.
 * An code example snippet:
 * <pre>{@code
 * <drill>
 * 		<point x="40" y="20"/>
 * 		<depth start="0" end="-1" />
 * </drill>
 * }</pre>
 * @param node The node with the needed parameters
 */


public class Drill extends Element {

	private Point2D.Double point;
	
	public Drill(Node node, Generator gen) {
		super(node, gen);
		point = null;
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
				point = new Point2D.Double(coords[0], coords[1]);
			}
			if(item.getNodeName() == "depth") {
				map = item.getAttributes();
				double values[] = new double[3];
				values[0] = Double.parseDouble(map.getNamedItem("start").getTextContent());
				values[1] = Double.parseDouble(map.getNamedItem("end").getTextContent());
				zLevel = new Tuple(values);
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
		
		shape.moveTo(point.getX(), point.getY());
		
		at = new AffineTransform();
		at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
		
		addToolPathes(generateToolPathes(shape, at, 0.1, new String("Drill at " + point)));

		Main.log.log(Level.FINE, "Drill element: drill at {0} and translation {1}", new Object[] { point, gen.getTranslation() } );			
	}
}
