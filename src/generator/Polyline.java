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
 * This class generates 2D coordinates for a polyline.
 * The polyline is defined by two or more points. The tupel in <p> defines the x and y position of the point (<p>x,y</p>). Two consecutive points descibe a line.
 *	
 * You can describe a bow by setting control points. The start point (b0) and end point (bn) are defined by <p> tags. You need to define one ore more inner control points (b1 to bn-1) with tag <bez>x,y</bez>.
 * With one inner control point you describe a quadratic bezier curve (second grade), with two inner control points a cubic bezier curve (third grade), with n control points you describe a curve with grade n + 1.
 * For more information see in German https://de.wikipedia.org/wiki/B%C3%A9zierkurve and in English https://en.wikipedia.org/wiki/B%C3%A9zier_curve.
 * 
 * For creating splines the <spl> tag can be used. The curve will go through the point.
 *	
 * The z-depth must be defined by the <z> tag. The tupel in <z> defines the the start layer (workpiece surface), the end layer (depth), and the steps (<z>startZ,endZ,stepZ</z>).
 * @param node The node with the needed parameters
 */

public class Polyline extends ElementClosed {
	
	private ArrayList<Tuple> points;

	public Polyline(Node node, Generator gen) {
		super(node, gen);
		points = new ArrayList<Tuple>();
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
			if(item.getNodeName() == "bezier") {
				map = item.getAttributes();
				double coords[] = new double[2];
				coords[0] = Double.parseDouble(map.getNamedItem("x").getTextContent());
				coords[1] = Double.parseDouble(map.getNamedItem("y").getTextContent());
				points.add(new Tuple(coords, Tuple.BEZIER));
			}
			if(item.getNodeName() == "spline") {
				map = item.getAttributes();
				double coords[] = new double[2];
				coords[0] = Double.parseDouble(map.getNamedItem("x").getTextContent());
				coords[1] = Double.parseDouble(map.getNamedItem("y").getTextContent());
				points.add(new Tuple(coords, Tuple.SPLINE));
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
				setClosedElementsAttributeVars(map);
			}
		}
				
	}

	@Override
	public void execute() {
		shape = new Path2D.Double();
		
		/*for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}*/
		
		// Create toolpath
		for(int i = 0; i < xmlPoints.size(); i++) {
			if(xmlPoints.get(i).getType() == Tuple.POINT) {
				if(i == 0) {
					shape.moveTo(xmlPoints.get(i).getValue(0).doubleValue(), xmlPoints.get(i).getValue(1).doubleValue());
				} else {
					shape.lineTo(xmlPoints.get(i).getValue(0).doubleValue(), xmlPoints.get(i).getValue(1).doubleValue());
				}
				Main.log.log(Level.FINE, "Polyline element: line to (" + xmlPoints.get(i));
			} else if(xmlPoints.get(i).getType() == Tuple.BEZIER) {
				int n;
				ArrayList<Tuple> b = new ArrayList<Tuple>();
			
				b.add(xmlPoints.get(i - 1)); // Add first control point b0
				// fill the control points b1 ... bn-1
				for(n = 0; xmlPoints.get(i + n).getType() == Tuple.BEZIER; n++) {
					b.add(xmlPoints.get(i + n));
				}
				b.add(xmlPoints.get(i + n)); // Add the last control point bn

				deCasteljau(b, 0.5, shape, 4);
		
				i += n - 1; 			// Skip the next inner control points (b1 - bn-1)
				Main.log.log(Level.FINE, "Polyline element: bezier curve grade " + (n + 1) + " to (" + b.get(b.size() - 1).getValue(0).doubleValue() + ", " + b.get(b.size() - 1).getValue(1).doubleValue() + ").");
			} else if(xmlPoints.get(i).getType() == Tuple.SPLINE) {
				ArrayList<Tuple> points = new ArrayList<Tuple>();
				
				double dx = xmlPoints.get(i).getValue(0).doubleValue() - xmlPoints.get(i - 1).getValue(0).doubleValue();
				double dy = xmlPoints.get(i).getValue(1).doubleValue() - xmlPoints.get(i - 1).getValue(1).doubleValue();
				
				for(int j = -1; j < 1; j++) {
					points.add(xmlPoints.get(i + j));
				}
				
				if(i == 1) { // If are not two points before the first spline
					points.add(0, new Tuple(new double[] { xmlPoints.get(i - 1).getValue(0).doubleValue() - dx, xmlPoints.get(i - 1).getValue(1).doubleValue() - dy } ));	
				} else {
					points.add(0, xmlPoints.get(i - 2));
				}
				if(i == xmlPoints.size() -1) { // If the last point is missing
					if(xmlPoints.get(i).equals(xmlPoints.get(0))) { // Check if last point is the same then first point (closed shape).
						Main.log.log(Level.FINER, "Closed shape!");
						points.add(new Tuple(new double[] { xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue() } ));
					} else {
						points.add(new Tuple(new double[] { xmlPoints.get(i).getValue(0).doubleValue() + dx, xmlPoints.get(i).getValue(1).doubleValue() + dy } ));
					}
				} else {
					points.add(xmlPoints.get(i + 1));
				}
				
				if(Main.log.isLoggable(Level.FINER)) {
					StringBuffer stringBuffer = new StringBuffer("Considerable points for spline " + i + ": ");
					for(int j = 0; j < points.size(); j++) {
						stringBuffer.append(" p" + j + points.get(j) + ":");
					}
					Main.log.log(Level.FINER, stringBuffer.toString());
				}
			
				calculatePoint(points, 0.5, shape, 5);
				
				// insert last point of curve, because we do not add the last control point to the toolpath
				if(points.get(3).getType() == Tuple.POINT) {
					shape.lineTo(points.get(2).getValue(0).doubleValue(), points.get(2).getValue(1).doubleValue());
				}
				
				Main.log.log(Level.FINE, "Polyline element: spline to (" + points.get(points.size() - 2).getValue(0).doubleValue() + ", " + points.get(points.size() - 2).getValue(1).doubleValue() + ").");
			}
		}
		
		at = new AffineTransform();
		at.translate(gen.getTranslation().getX(), gen.getTranslation().getY()); //Translation from translation tag
		
		Path2D.Double pathShape = createOffsetShape(shape);
		
		super.setName(new String("Polyline starting from " + xmlPoints.get(0) + " to " + xmlPoints.get(xmlPoints.size() - 1)));
		
		addToolPathes(generateToolPathes(pathShape, at, 0.1, super.getName()));

		//create pockettoolpath
		if(isPocket()) {
			addToolPathes(createPocket(pathShape, at, gen.getTool()));
		}
		
		Main.log.log(Level.FINE, "Generated polyline element from {0} with translation {1} with {2} points.", new Object[] { xmlPoints.get(0), gen.getTranslation(), getToolPath(0).size() });
	}
	
	/**
	 * Implements the deCastelau algorithm as a recursive curve interpolation.
	 * The curve will be split at tau and the algorithm will be invoked for both sub curves again until level 0 is reached.
	 * 
	 * @param points The ControlPoints b0,...,bn
	 * @param t The tau value 0 <= t <= 1
	 * @param shape The path where to insert the curve points
	 * @param level The current level depth of recursive implementation. 
	 * @return The x and y coordinates on the bezier 
	 */
	private void deCasteljau(ArrayList<Tuple> points, double t, Path2D.Double shape, int level) {
		int n = points.size();
		ArrayList<Tuple> partCurvePointsLower = new ArrayList<Tuple>();
		ArrayList<Tuple> partCurvePointsUpper = new ArrayList<Tuple>();
		
		double[][] bx = new double[n][n];
		double[][] by = new double[n][n];
		
		Main.log.finer("Recursion level:" + level);
		
		for(int k = 0; k < n; k++) {
            bx[0][k] = points.get(k).getValue(0).floatValue();
            by[0][k] = points.get(k).getValue(1).floatValue();
        }

		for(int j = 1; j < n; j++) {
			for (int k = 0; k < n - j; k++) {
				bx[j][k] = bx[j-1][k] * (1 - t) + bx[j - 1][k + 1] * t;
				by[j][k] = by[j-1][k] * (1 - t) + by[j - 1][k + 1] * t;
			}
		}
		
		if(Main.log.isLoggable(Level.FINEST)) {
			StringBuffer matrix = new StringBuffer("deCasteljau Matrix\n");
			for(int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					matrix.append(bx[j][k] + ", ");
				}
				matrix.append("\n");
			}
			Main.log.finest(matrix.toString());
		}
		
		for(int k = 0; k < n; k++) {
			partCurvePointsLower.add(new Tuple(new double[] {bx[k][0], by[k][0]}));
		}
		
		for(int j = n - 1; j >= 0; j--) {
			partCurvePointsUpper.add(new Tuple(new double[] {bx[j][n - 1 - j], by[j][n - 1 - j]}));	
		}
		
		if(Main.log.isLoggable(Level.FINER)) {
			StringBuffer stringBuffer = new StringBuffer("PartCurvePointsLower:");
			for (int j = 0; j < partCurvePointsLower.size(); j++) {
				stringBuffer.append(partCurvePointsLower.get(j) + ", ");
			}
			stringBuffer.append("\nPartCurvePointsUpper:");
			for (int j = 0; j < partCurvePointsUpper.size(); j++) {
				stringBuffer.append(partCurvePointsUpper.get(j) + ", ");
			}
			Main.log.finer(stringBuffer.toString());
		}
	
		if(level > 0) {
			level--;
			deCasteljau(partCurvePointsLower, 0.5, shape, level);
			deCasteljau(partCurvePointsUpper, 0.5, shape, level);	
		} else {
			for(int k = 0; k < n - 1; k++) {
				shape.lineTo(bx[0][k], by[0][k]);
			}
		}
	
	}
	
	/**
	 * This method calculates the bezier start and end points (0th derivation) and the inner control points (1th derivation or vector) for a spline.
	 * @param points b0 = point before start point, b1 start point, b2 end point, b3 point behind end point
	 * @param t 0 <= tau <= 1
	 */
	private void calculatePoint(ArrayList<Tuple> points, double t, Path2D.Double shape, int level) {
		double dx1, dy1, dx2, dy2;
		ArrayList<Tuple> pointList = new ArrayList<Tuple>();
		double[] point = new double[2];
		double distance = points.get(1).distance(points.get(2));

		Main.log.finer("Distance: " + distance);
		
		pointList.add(points.get(1));
		
		if(points.get(1).getType() == Tuple.SPLINE) {
			dx1 = 0.5 * (points.get(2).getValue(0).doubleValue() - points.get(0).getValue(0).doubleValue());
			dy1 = 0.5 * (points.get(2).getValue(1).doubleValue() - points.get(0).getValue(1).doubleValue());
			point[0] = points.get(1).getValue(0).doubleValue() + (1 / 3.0) * dx1;
			point[1] = points.get(1).getValue(1).doubleValue() + (1 / 3.0) * dy1;
			pointList.add(new Tuple(point));
		} else {
			dx1 = points.get(1).getValue(0).doubleValue() - points.get(0).getValue(0).doubleValue();
			dy1 = points.get(1).getValue(1).doubleValue() - points.get(0).getValue(1).doubleValue();
			double unitFactor = Math.sqrt(Math.pow(dx1, 2) + Math.pow(dy1, 2)); // Einheitsvektor
			point[0] = points.get(1).getValue(0).doubleValue() + 1 / unitFactor * dx1 * distance * 0.4;
			point[1] = points.get(1).getValue(1).doubleValue() + 1 / unitFactor * dy1 * distance * 0.4;
			pointList.add(new Tuple(point));
		}
		
		if(points.get(3).getType() == Tuple.SPLINE) {
			dx2 = 0.5 * (points.get(3).getValue(0).doubleValue() - points.get(1).getValue(0).doubleValue());
			dy2 = 0.5 * (points.get(3).getValue(1).doubleValue() - points.get(1).getValue(1).doubleValue());
			point[0] = points.get(2).getValue(0).doubleValue() - (1 / 3.0) * dx2;
			point[1] = points.get(2).getValue(1).doubleValue() - (1 / 3.0) * dy2;
			pointList.add(new Tuple(point));
		} else {
			dx2 = points.get(2).getValue(0).doubleValue() - points.get(3).getValue(0).doubleValue();
			dy2 = points.get(2).getValue(1).doubleValue() - points.get(3).getValue(1).doubleValue();
			double unitFactor = Math.sqrt(Math.pow(dx2, 2) + Math.pow(dy2, 2)); // Einheitsvektor
			point[0] = points.get(2).getValue(0).doubleValue() + (1 / unitFactor) * dx2 * distance * 0.4;
			point[1] = points.get(2).getValue(1).doubleValue() + (1 / unitFactor) * dy2 * distance * 0.4;
			pointList.add(new Tuple(point));
		}

		pointList.add(points.get(2));
		
		if(Main.log.isLoggable(Level.FINER)) {
			StringBuffer stringBuffer = new StringBuffer("Bezier points:");
			for(int i = 0; i < pointList.size(); i++) {
				stringBuffer.append(" t" + i + ":" + pointList.get(i));
			}
			stringBuffer.append(" for tau " + t);
			Main.log.log(Level.FINER, stringBuffer.toString());
		}
		
		deCasteljau(pointList, t, shape, level);
	}
	
	/**
	 * Returns the coordinate of an quadratic spline.
	 * @param b0 The start point
	 * @param b1 The anchor point
	 * @param b2 The end point
	 * @param t The position of the coordinate 0 < t <= 1
	 * @return The coordinate
	 */
	@SuppressWarnings("unused")
	private double quadraticBezier(double b0, double b1, double b2, double t) {
		return  Math.pow((1 - t), 2) * b0
				+ 2 * t * (1 - t) * b1
				+ Math.pow(t, 2) * b2;
	}
	
	/**
	 * Returns the coordinate of an cubic spline.
	 * @param b0 The start point
	 * @param b1 The first anchor point
	 * @param b2 The second anchor point
	 * @param b3 The end point
	 * @param t The position of the coordinate 0 < t <= 1
	 * @return The coordinate
	 */
	@SuppressWarnings("unused")
	private double cubicBezier(double b0, double b1, double b2, double b3, double t) {
		return  Math.pow((1 - t), 3) * b0
				+ 3 * t * Math.pow((1 - t), 2) * b1
				+ 3 * Math.pow(t, 2) * (1 - t) * b2
				+ Math.pow(t, 3) * b3;
	}
	
	/**
	 * Calulates the euclidean length of the control points.
	 * @param b The control points
	 * @return The length
	 */
	@SuppressWarnings("unused")
	private double getVectorLength(ArrayList<Tuple> b) {
		double distance = 0;
		for(int i = 0; i < b.size() - 1; i++) {
			distance += b.get(i).distance(b.get(i + 1));
		}
		return distance;
	}
	
}
