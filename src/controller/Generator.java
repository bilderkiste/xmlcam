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
import java.math.MathContext;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import main.Main;
import main.Settings;
import model.Field;
import model.Line;
import model.Program;
import view.XMLView;

/**
 * This class is the heart piece of xmlCam. Here all G-Code will generated from XML.
 * @author Christian Kirsch
 *
 */
public class Generator {
	
	private Program programModel;
	private XMLView xmlEditorPane;
	private BigDecimal currentX, currentY, currentZ, newX, newY, newZ;
	
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

	}
	
	/**
	 * Generate G-Code from the script;
	 */
	public void generate() {
		Node mainNode;
		NodeList commands = null;
		int commandNumber = 0;
		
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
				commands = mainNode.getChildNodes();
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
					}
				}
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
		ArrayList<Tuple> points = new ArrayList<Tuple>();
		Tuple zLevel = null;;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				points.add(new Tuple(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		boolean isAPosition = true;

		newX = points.get(0).getValue(0);
		newY = points.get(0).getValue(1);
		newZ = zLevel.getValue(0);
		
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		go0(newX, newY, "Go to start position for the line"); // go to start position
		
		while(newZ.doubleValue() >= endZ.doubleValue()) {
			go1(newX, newY, newZ); // Z sink
			
			if(isAPosition) {
				newX = points.get(1).getValue(0);
				newY = points.get(1).getValue(1);
				isAPosition = false;
			} else {
				newX = points.get(0).getValue(0);
				newY = points.get(0).getValue(1);
				isAPosition = true;
			}
			
			go1(newX, newY, newZ); // X-Y move
			
			newZ = newZ.subtract(stepZ);
		}
		
		go0(currentX, currentY, "End line; Lift up at current position");
			
	}
	
	/**
	 * Generate G-Code for a polyline
	 * @param node The node with the needed parameters
	 */
	/*private void polyline(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		ArrayList<Tupel> points = new ArrayList<Tupel>();
		Tupel zLevel = null;;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				points.add(new Tupel(item));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tupel(item);
			}
		}
		
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		boolean forward = true;
		
		newX = points.get(0).getValue(0);
		newY = points.get(0).getValue(1);
		newZ = zLevel.getValue(0);
		
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		go0(newX, newY, "Go to start position for the polyline"); // go to start position
		
		while(newZ.doubleValue() >= endZ.doubleValue()) {
			if(forward) {
				go1(newX, newY, newZ);  // Z sink
				for(int i = 1; i < points.size(); i++) {
	
					newX = points.get(i).getValue(0);
					newY = points.get(i).getValue(1);
					
					go1(newX, newY, newZ); // X-Y move
				}
				forward = false;
			} else {
				go1(newX, newY, newZ);  // Z sink
				for(int i = points.size() - 2; i >= 0 ; i--) {
	
					newX = points.get(i).getValue(0);
					newY = points.get(i).getValue(1);
					
					go1(newX, newY, newZ); // X-Y move
				}
				forward = true;
			}
			newZ = newZ.subtract(stepZ);
		}
		go0(currentX, currentY, "End polyline; Lift up at current position");
	}*/
	
	/**
	 * Generate G-Code for a polyline.
	 * The polyline is defined by two or more points. 
	 * You can describe a bow by setting an anchor point (quadratic bezier curves) at the moment. You can do this with two points with the <p> tag and an anchor point with a <bez> tag.
	 * The z-depth must be defined by the <z> tag.
	 * An code example snippet:
	 * <pre>{@code
	 * <polyline>
	 * 		<p>40,20</p>
	 * 		<p>80,20</p>
	 * 		<bez>200,60</bez>
	 * 		<p>80,100</p>
	 * 		<p>200,200</p>
	 * 		<z>0,-1,0.1</z>
	 * </polyline>
	 * }</pre>
	 * @param node The node with the needed parameters
	 */
	private void polyline(Node node) throws IllegalArgumentException {
		NodeList children = node.getChildNodes();
		ArrayList<Tuple> points = new ArrayList<Tuple>();
		Tuple zLevel = null;;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				points.add(new Tuple(item));
			}
			if(item.getNodeName() == "bez") {
				points.add(new Tuple(item, Tuple.BEZIER));
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		boolean forward = true;
		
		newX = points.get(0).getValue(0);
		newY = points.get(0).getValue(1);
		newZ = zLevel.getValue(0);
		
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		go0(newX, newY, "Go to start position for the polyline"); // go to start position
		
		while(newZ.doubleValue() >= endZ.doubleValue()) {
			if(forward) {
				go1(newX, newY, newZ);  // Z sink
				for(int i = 1; i < points.size(); i++) {
					if(points.get(i).getType() == Tuple.POINT) {
						newX = points.get(i).getValue(0);
						newY = points.get(i).getValue(1);
						go1(newX, newY, newZ); // X-Y move
						//System.out.println(points.get(i).getType() + ":" + newX + "," + newY + ","  + newZ);
					} else if(points.get(i).getType() == Tuple.BEZIER) {
						for(double t = 0.1; t < 1; t += 0.1) {
							double x =  (points.get(i - 1).getValue(0).floatValue() - 2 * points.get(i).getValue(0).floatValue() + points.get(i + 1).getValue(0).floatValue()) * Math.pow(t, 2)
										+ (- 2 * points.get(i - 1).getValue(0).floatValue() + 2 * points.get(i).getValue(0).floatValue()) * t
										+ points.get(i - 1).getValue(0).floatValue();
							double y =  (points.get(i - 1).getValue(1).floatValue() - 2 * points.get(i).getValue(1).floatValue() + points.get(i + 1).getValue(1).floatValue()) * Math.pow(t, 2)
										+ (- 2 * points.get(i - 1).getValue(1).floatValue() + 2 * points.get(i).getValue(1).floatValue()) * t
										+ points.get(i - 1).getValue(1).floatValue();							
							newX = new BigDecimal(x, new MathContext(4));
							newY = new BigDecimal(y, new MathContext(4));
							go1(newX, newY, newZ); // X-Y move
							//System.out.println(points.get(i).getType() + ":" + newX + "," + newY + ","  + newZ);
						}
						i++; // skip next point, because the bezier use this point as b2 and the machine is already there
					}
				}
				forward = false;
			} else {
				go1(newX, newY, newZ);  // Z sink
				for(int i = points.size() - 2; i >= 0 ; i--) {
					if(points.get(i).getType() == Tuple.POINT) {
						newX = points.get(i).getValue(0);
						newY = points.get(i).getValue(1);
						go1(newX, newY, newZ); // X-Y move
					} else if(points.get(i).getType() == Tuple.BEZIER) {
						for(double t = 0.9; t > 0; t -= 0.1) {
							double x =  (points.get(i - 1).getValue(0).floatValue() - 2 * points.get(i).getValue(0).floatValue() + points.get(i + 1).getValue(0).floatValue()) * Math.pow(t, 2)
										+ (- 2 * points.get(i - 1).getValue(0).floatValue() + 2 * points.get(i).getValue(0).floatValue()) * t
										+ points.get(i - 1).getValue(0).floatValue();
							double y =  (points.get(i - 1).getValue(1).floatValue() - 2 * points.get(i).getValue(1).floatValue() + points.get(i + 1).getValue(1).floatValue()) * Math.pow(t, 2)
										+ (- 2 * points.get(i - 1).getValue(1).floatValue() + 2 * points.get(i).getValue(1).floatValue()) * t
										+ points.get(i - 1).getValue(1).floatValue();							
							newX = new BigDecimal(x, new MathContext(5));
							newY = new BigDecimal(y, new MathContext(5));
							go1(newX, newY, newZ); // X-Y move
						}
						i--;
					}
				}
				forward = true;
			}
			newZ = newZ.subtract(stepZ);
		}
		go0(currentX, currentY, "End polyline; Lift up at current position");
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
		Tuple center = null, radius = null, zLevel = null;
		int resolution = 2; // mm
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "p") {
				center = new Tuple(item);
			}
			if(item.getNodeName() == "rad") {
				radius = new Tuple(item);
			}
			if(item.getNodeName() == "z") {
				zLevel = new Tuple(item);
			}
		}
		
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		float phi = 0;
		float xCenter = center.getValue(0).floatValue();
		float yCenter = center.getValue(1).floatValue();
		float radiusv = radius.getValue(0).floatValue();;
		
		newX = new BigDecimal(xCenter + radiusv * Math.sin(phi), new MathContext(4));
		newY = new BigDecimal(yCenter + radiusv * Math.cos(phi), new MathContext(4));
		newZ = zLevel.getValue(0);
		
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		// Determine phiStep. If the circle is very small, the step should be < 0.5 (that means more G points on the circle
		double phiStep = 2 * Math.PI / ((2 * radiusv * Math.PI) / resolution);
		if(phiStep > 0.5) {
			phiStep = 0.5;
		}
		
		System.out.println("PL: " + phiStep);
		
		go0(newX, newY, "Go to start position for the circle"); // go to start position
		
		while(newZ.floatValue() >= endZ.floatValue()) {
			go1(newX, newY, newZ);
			while(phi < 2 * Math.PI) {
				newX = new BigDecimal(xCenter + radiusv * Math.sin(phi));
				newY = new BigDecimal(yCenter + radiusv * Math.cos(phi));
				go1(newX, newY, newZ);
				
				phi += phiStep;			
			}
			
			// Tool to startpoint
			phi = 0;
			newX = new BigDecimal(xCenter + radiusv * Math.sin(phi));
			newY = new BigDecimal(yCenter + radiusv * Math.cos(phi));
			go1(newX, newY, newZ);
			
			newZ = newZ.subtract(stepZ);
		}
		
		go0(currentX, currentY, "End circle; Lift up at current position");
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
