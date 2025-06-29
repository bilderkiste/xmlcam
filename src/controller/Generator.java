/*********************************************************************\
 * Generator.java - xmlCam G-Code Generator                          *
 * Copyright (C) 2020, Christian Kirsch                              *
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

package controller;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import main.Main;
import misc.Settings;
import model.Field;
import model.Line;
import model.Program;
import xml.XMLView;

/**
 * This class is the heart piece of xmlCam. Here all G-Code will generated from XML.
 * @author Christian Kirsch
 *
 */
public class Generator {
	
	private Program programModel;
	private XMLView xmlEditorPane;
	private BigDecimal currentX, currentY, currentZ, newX, newY, newZ;
	private BigDecimal translateX, translateY;
	
	/**
	 * Constructs a new G-Code Generator.
	 * @param programModel The model with the changeable items
	 * @param xmlEditorPane The editorPane with the XML script
	 */
	public Generator(Program programModel, XMLView xmlEditorPane) {
		this.programModel = programModel;
		this.xmlEditorPane = xmlEditorPane;
		this.currentX = new BigDecimal(Double.MIN_VALUE);
		this.currentY = new BigDecimal(Double.MIN_VALUE);
		this.currentZ = new BigDecimal(Double.MIN_VALUE);
		this.newX = new BigDecimal(0);
		this.newY = new BigDecimal(0);
		this.newZ = new BigDecimal(0);
		this.translateX = new BigDecimal(0);
		this.translateY = new BigDecimal(0);
	}
	
	/**
	 * Goes through the DOM recursively. Needed if translation tag is used.
	 * @param node the
	 */
	private void getChildNodes(Node node) {
		NodeList commands = null;
		commands = node.getChildNodes();
		int commandNumber = 0;
		
		/*for(commandNumber = 0; commandNumber < commands.getLength(); commandNumber++) {
			System.out.println(commands.item(commandNumber). + " ;" + commands.item(commandNumber));
		}*/
		for(commandNumber = 0; commandNumber < commands.getLength(); commandNumber++) {
			if(commands.item(commandNumber).getNodeName() == "line") {
				line(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "polyline") {
				polyline(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "circle") {
				circle(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "feedrate") {
				setFeedRate(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "translate") {
				setTranslation(commands.item(commandNumber));
				getChildNodes(commands.item(commandNumber));
				// Translate tag closed. Reset translate values
				this.translateX = new BigDecimal(0);
				this.translateY = new BigDecimal(0);
			}
		}
	}
	
	/**
	 * Generate G-Code from the script;
	 */
	public void generate() {
		Node mainNode;
		
		programModel.clear();
		
		// insert start code
		try {
			programModel.readFromFile("start.gcode");
		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Failed to load >start.gcode<: " + e);
		}
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xmlEditorPane.getText())));
			
			mainNode = doc.getFirstChild();
			if(mainNode.getNodeName() == "program") {
				getChildNodes(mainNode);
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Main.log.log(Level.SEVERE, "XML parsing failed; " + e);
			//e.printStackTrace();
		} catch(NullPointerException | IndexOutOfBoundsException e) {
			Main.log.log(Level.SEVERE, "Missing parameter(s); " + e);
			//e.printStackTrace();
		} catch(NumberFormatException e) {
			Main.log.log(Level.SEVERE, "Illegal parameter(s); " + e);
			//e.printStackTrace();
		} catch(IllegalArgumentException e) {
			Main.log.log(Level.SEVERE, "Illegal parameter(s); " + e);
			//e.printStackTrace();
		}
		
		// insert end code
		try {
			programModel.readFromFile("end.gcode");
		} catch (IOException e) {
			Main.log.log(Level.WARNING, "Failed to load >end.gcode<: " + e);
		}
	}
	
	/**
	 * Adds the translation to the x and y values of a point.
	 * @param pint The point.
	 * @return The point translated.
	 */
	private Tuple addTranslation(Tuple point) {
		point.setValue(0, point.getValue(0).doubleValue() + this.translateX.doubleValue());
		point.setValue(1, point.getValue(1).doubleValue() + this.translateX.doubleValue());
		return point;
	}

	/**
	 * Generate G-Code for a line.
	 * The line is defined by two points defined with <p> tags.
	 * The z-depth must be defined by the <z> tag.
	 * An code example snippet:
	 * <pre>{@code
	 * <line>
	 * 		<p>40,20</p>
	 * 		<p>80,20</p>
	 * 		<z>0,-1,0.1</z>
	 * </line>
	 * }</pre>
	 * @param node The node with the needed parameters
	 */
	private void line(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		ArrayList<Tuple> xmlPoints = new ArrayList<Tuple>();
		ArrayList<double[]> toolPath = new ArrayList<double[]>();
		Tuple zLevel = null;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				xmlPoints.add(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}

		toolPath.add(new double[] { xmlPoints.get(0).getValue(0).doubleValue(), xmlPoints.get(0).getValue(1).doubleValue() });
		toolPath.add(new double[] { xmlPoints.get(1).getValue(0).doubleValue(), xmlPoints.get(1).getValue(1).doubleValue() });
		
		createGCode(toolPath, zLevel);
		
		Main.log.log(Level.FINE, "Line element: line from (" + xmlPoints.get(0).getValue(0) + ", " + xmlPoints.get(0).getValue(0) + ") to (" + xmlPoints.get(1).getValue(0) + ", " + xmlPoints.get(1).getValue(0) + ").");
	}
	
	/**
	 * This method generates G-Code for a polyline.
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
	private void polyline(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		ArrayList<Tuple> xmlPoints = new ArrayList<Tuple>();
		ArrayList<double[]> toolPath = new ArrayList<double[]>();
		Tuple zLevel = null;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				xmlPoints.add(new Tuple(item));
			}
			if(item.getNodeName() == "bez") {
				xmlPoints.add(new Tuple(item, Tuple.BEZIER));
			}
			if(item.getNodeName() == "spl") {
				xmlPoints.add(new Tuple(item, Tuple.SPLINE));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		for(int i = 0; i < xmlPoints.size(); i++) {
			xmlPoints.set(i, addTranslation(xmlPoints.get(i)));
		}
		
		// Create toolpath
		for(int i = 0; i < xmlPoints.size(); i++) {
			if(xmlPoints.get(i).getType() == Tuple.POINT) {
				toolPath.add(new double[] {xmlPoints.get(i).getValue(0).doubleValue(), xmlPoints.get(i).getValue(1).doubleValue()});
				Main.log.log(Level.FINE, "Polyline element: line to (" + xmlPoints.get(i).getValue(0).doubleValue() + ", " + xmlPoints.get(i).getValue(1).doubleValue() + ")");
			} else if(xmlPoints.get(i).getType() == Tuple.BEZIER) {
				int n;
				ArrayList<Tuple> b = new ArrayList<Tuple>();
			
				b.add(xmlPoints.get(i - 1)); // Add first control point b0
				// fill the control points b1 ... bn-1
				for(n = 0; xmlPoints.get(i + n).getType() == Tuple.BEZIER; n++) {
					b.add(xmlPoints.get(i + n));
				}
				b.add(xmlPoints.get(i + n)); // Add the last control point bn

				deCasteljau(b, 0.5, toolPath, 4);
		
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
			
				calculatePoint(points, 0.5, toolPath, 5);
				
				// insert last point of curve, because we do not add the last control point to the toolpath
				if(points.get(3).getType() == Tuple.POINT) {
					toolPath.add(new double[] {points.get(2).getValue(0).doubleValue(), points.get(2).getValue(1).doubleValue()});
				}
				
				Main.log.log(Level.FINE, "Polyline element: spline to (" + points.get(points.size() - 2).getValue(0).doubleValue() + ", " + points.get(points.size() - 2).getValue(1).doubleValue() + ").");
			}
		}
		
		createGCode(toolPath, zLevel);
		
		Main.log.log(Level.FINE, "Generated polyline element with " + toolPath.size() + " points.");
	}
	
	/**
	 * Implements the deCastelau algorithm as a recursive curve interpolation.
	 * The curve will be split at tau and the algorithm will be invoked for both sub curves again until level 0 is reached.
	 * 
	 * @param points The ControlPoints b0,...,bn
	 * @param t The tau value 0 <= t <= 1
	 * @param toolPath The tool path where to insert the curve points
	 * @param level The current level depth of recursive implementation. 
	 * @return The x and y coordinates on the bezier 
	 */
	private void deCasteljau(ArrayList<Tuple> points, double t, ArrayList<double[]> toolPath, int level) {
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
			deCasteljau(partCurvePointsLower, 0.5, toolPath, level);
			deCasteljau(partCurvePointsUpper, 0.5, toolPath, level);	
		} else {
			for(int k = 0; k < n - 1; k++) {
				toolPath.add(new double[] {bx[0][k], by[0][k]});
			}
		}
	
	}
	
	/**
	 * This method calculates the bezier start and end points (0th derivation) and the inner control points (1th derivation or vector) for a spline.
	 * @param points b0 = point before start point, b1 start point, b2 end point, b3 point behind end point
	 * @param t 0 <= tau <= 1
	 */
	private void calculatePoint(ArrayList<Tuple> points, double t, ArrayList<double[]> toolPath, int level) {
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
		
		deCasteljau(pointList, t, toolPath, level);
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
	
	
	/**
	 * Generate G-Code for a circle.
	 * A circle is defined by the center point determined through a <p> tag and a radius defined through a <rad> tag.
	 * The z-depth must be defined by the <z> tag.
	 * An code example snippet:
	 * <pre>{@code
	 * <circle>
	 * 		<p>200,200</p>
	 * 		<rad>75</rad>
	 *		<z>0,-1,0.1</z>
	 * </circle>
	 * }</pre>
	 * @param node The node with the needed parameters
	 */
	private void circle(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		ArrayList<double[]> toolPath = new ArrayList<double[]>();
		Tuple center = null, radius = null, zLevel = null;
		int resolution = 2; // mm
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				center = addTranslation(new Tuple(item));
			}
			if(item.getNodeName() == "rad") {
				radius = new Tuple(item);
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		float phi = 0;
		float xCenter = center.getValue(0).floatValue();
		float yCenter = center.getValue(1).floatValue();
		float radiusv = radius.getValue(0).floatValue();;
			
		// Determine phiStep. If the circle is very small, the step should be < 0.5 (that means more G points on the circle
		double phiStep = 2 * Math.PI / ((2 * radiusv * Math.PI) / resolution);
		if(phiStep > 0.5) {
			phiStep = 0.5;
		}
	
		while(phi < 2 * Math.PI) {
			toolPath.add(new double[] { xCenter + radiusv * Math.sin(phi), yCenter + radiusv * Math.cos(phi) });
			phi += phiStep;			
		}
		toolPath.add(new double[] { xCenter + radiusv * Math.sin(0), yCenter + radiusv * Math.cos(0) });
		
		createGCode(toolPath, zLevel);
		
		Main.log.log(Level.FINE, "Circle element: circle with " + (int)(((Math.PI * 2) / phiStep) + 1) + " points. Step for phi is " + phiStep + ".");
	}
	
	/**
	 * Generate G-Code for a semicircle.
	 * @param node The node with the needed parameters
	 */
	@SuppressWarnings("unused")
	private void semicircle(Node node) throws IllegalArgumentException {
		
	}
	
	/**
	 * Sets the feedrate in mm/min. (Fxxx)
	 * @param  node The node with the feedrate parameter [feedrate]
	 */
	private void setFeedRate(Node node) throws IllegalArgumentException {
		Tuple feedrate = new Tuple(node);

		programModel.addLine(new Line());
		programModel.addField(new Field('G', new BigDecimal(0)));
		programModel.addField(new Field('F', feedrate.getValue(0)));
	}
	
	/**
	 * Sets the translation for x and y coordinates within the translation tag.
	 * 	An code example snippet:
	 * <pre>{@code
	 * <translation x="10" y="10">
	 * 		...
	 * </translation>
	 * }</pre>
	 * @param node
	 * @throws IllegalArgumentException
	 */
	
	private void setTranslation(Node node) throws IllegalArgumentException {
		NamedNodeMap map = node.getAttributes();
		try {
			this.translateX = new BigDecimal(map.getNamedItem("x").getTextContent());
			this.translateY = new BigDecimal(map.getNamedItem("y").getTextContent());
		} catch(NullPointerException e) {
			Main.log.log(Level.SEVERE, "Missing translation parameter(s); " + e);
		} catch(NumberFormatException e) {
			Main.log.log(Level.SEVERE, "Illegal translation parameter(s); " + e);
		}	
	}
	
	/**
	 * This method creates the G-Code for the toolpath.
	 * 
	 * @param toolPath The toolpath
	 * @param zLevel The milling depth (z-axis)
	 */
	private void createGCode(ArrayList<double[]> toolPath, Tuple zLevel) {
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		boolean forward = true;
		
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		newX = new BigDecimal(toolPath.get(0)[0]);
		newY = new BigDecimal(toolPath.get(0)[1]);
		newZ = zLevel.getValue(0);
		
		go0(newX, newY, "Go to start position for element."); // go to start position
			
		while(newZ.doubleValue() >= endZ.doubleValue()) {
			if(forward) {
				go1(newX, newY, newZ);  // Z sink
				for(int j = 1; j < toolPath.size(); j++) {
					newX = new BigDecimal(toolPath.get(j)[0]);
					newY = new BigDecimal(toolPath.get(j)[1]);
					go1(newX, newY, newZ);  // X-Y move
				}
				forward = false;
			} else {
				go1(newX, newY, newZ);  // Z sink
				for(int j = toolPath.size() - 2; j >= 0; j--) {
					newX = new BigDecimal(toolPath.get(j)[0]);
					newY = new BigDecimal(toolPath.get(j)[1]);
					go1(newX, newY, newZ);  // X-Y move
				}
				forward = true;
			}
			newZ = newZ.subtract(stepZ);
		}
		go0(currentX, currentY, "End element; Lift up at current position.");	
	}
	
	/**
	 * Performs a G0 move.
	 * For z the safety height above zero is used. If new x or y is not different to the current x and y, the field will not displayed. I.e. move from (10,10) to (10,20) the output will be G0 Y20.
	 * 
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 */
	@SuppressWarnings("unused")
	private void go0(BigDecimal x, BigDecimal y) {
		go0(x, y, null, null);
	}
	
	/**
	 * Performs a G0 move.
	 * For z the safety height above zero is used. If new x or y is not different to the current x and y, the field will not displayed. I.e. move from (10,10) to (10,20) the output will be G0 Y20.
	 * 
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 * @param comment A comment for the behind the G0
	 */
	private void go0(BigDecimal x, BigDecimal y, String comment) {
		go0(x, y, null, comment);
	}
	
	/**
	 * Performs a G0 move.
	 * For z the safety height above zero is used. If new x or y is not different to the current x and y, the field will not displayed. I.e. move from (10,10) to (10,20) the output will be G0 Y20.
	 * 
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 * @param feedrate The feedrate in mm/min
	 * @param comment A comment for the behind the G0
	 */
	private void go0(BigDecimal x, BigDecimal y, BigDecimal feedrate, String comment) {
		BigDecimal z = new BigDecimal(Settings.securityHeight);
		
		programModel.addLine(new Line());
		
		if(comment != null) {
			programModel.setComment(programModel.getLineSize() - 1, comment);
		}
		
		programModel.addField(new Field('G', new BigDecimal(0)));
		
		if(!newX.equals(currentX)) {
			programModel.addField(new Field('X', x));
			this.currentX = x;
		}
		
		if(!newY.equals(currentY)) {
			programModel.addField(new Field('Y', y));
			this.currentY = y;
		}
			
		if(!newZ.equals(currentZ)) {
			programModel.addField(new Field('Z', z));
			this.currentZ = z;
		}
		
		if(feedrate != null) {
			programModel.addField(new Field('F', feedrate));
		}
	}
	
	/**
	 * Performs a G1 move.
	 * If new x, y or z is not different to the current x,y and z, the field will not displayed. I.e. move from (10,10,10) to (10,20,10) the output will be G1 Y20.
	 * 
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 * @param z The new z coordinate
	 */
	private void go1(BigDecimal x, BigDecimal y, BigDecimal z) {
		go1(x, y, z, null, null);
	}
	
	/**
	 * Performs a G1 move.
	 * If new x, y or z is not different to the current x,y and z, the field will not displayed. I.e. move from (10,10,10) to (10,20,10) the output will be G1 Y20.
	 * 
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 * @param z The new z coordinate
	 * @param feedrate The feedrate in mm/min
	 * @param comment A comment for behind the G1
	 */
	private void go1(BigDecimal x, BigDecimal y, BigDecimal z, BigDecimal feedrate, String comment) {
		programModel.addLine(new Line());
		
		if(comment != null) {
			programModel.setComment(programModel.getLineSize() - 1, comment);
		}
		
		programModel.addField(new Field('G', new BigDecimal(1)));
		
		if(!newX.equals(currentX)) {
			programModel.addField(new Field('X', x));
			this.currentX = x;
		}
		
		if(!newY.equals(currentY)) {
			programModel.addField(new Field('Y', y));
			this.currentY = y;
		}

		if(!newZ.equals(currentZ)) {
			programModel.addField(new Field('Z', z));
			this.currentZ = z;
		}
		
		if(feedrate != null) {
			programModel.addField(new Field('F', feedrate));
		}

	}
	

}
