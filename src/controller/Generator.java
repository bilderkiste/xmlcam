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

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

import generator.Circle;
import generator.Drill;
import generator.Line;
import generator.Polyline;
import generator.Rectangle;
import generator.Text;
import main.Main;
import misc.Settings;
import model.Field;
import model.Row;
import model.Tool;
import model.ToolPath;
import model.Program;
import model.Tuple;
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
	private ArrayList<Point2D.Double> translation;
	private HashMap<String, Tool> tools;
	private Tool currentTool;
	
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
		this.translation = new ArrayList<Point2D.Double>();
		this.tools = new HashMap<String, Tool>();
		this.currentTool = null;
	}
	
	/**
	 * Goes through the DOM recursively. Needed if translation tag is used.
	 * @param node the
	 */
	private void getChildNodes(Node node) throws IllegalArgumentException {
		NodeList commands = null;
		commands = node.getChildNodes();
		int commandNumber = 0;
		
		/*for(commandNumber = 0; commandNumber < commands.getLength(); commandNumber++) {
			System.out.println(commands.item(commandNumber). + " ;" + commands.item(commandNumber));
		}*/
		for(commandNumber = 0; commandNumber < commands.getLength(); commandNumber++) {
			
			if(commands.item(commandNumber).getNodeName() == "tools") {
				setTools(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "drill") {
				Drill item = new Drill(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "line") {
				Line item = new Line(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "polyline") {
				Polyline item = new Polyline(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				item.purgePathes();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "circle") {
				Circle item = new Circle(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				//item.showToolPathes();
				item.purgePathes();
				//item.showToolPathes();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "rectangle") {
				Rectangle item = new Rectangle(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				//item.showToolPathes();
				item.purgePathes();
				//item.showToolPathes();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "text") {
				Text item = new Text(commands.item(commandNumber), this);
				item.extract();
				generateToolChange(item.getTool());
				item.execute();
				item.purgePathes();
				//item.showToolPathes();
				createGCode(item.getToolPathes(), item.getZLevel());
				programModel.addElement(item);
			} else if(commands.item(commandNumber).getNodeName() == "feedrate") {
				setFeedRate(commands.item(commandNumber));
			} else if(commands.item(commandNumber).getNodeName() == "translate") {
				setTranslation(commands.item(commandNumber));
				getChildNodes(commands.item(commandNumber));
				// Translate tag closed. Remove the last translation value.
				this.translation.remove(translation.size() - 1);
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
			Main.log.log(Level.SEVERE, "Missing or illegal parameter(s) in XML; " + e);
			//e.printStackTrace();
		} catch(NumberFormatException e) {
			Main.log.log(Level.SEVERE, "Illegal parameter(s) in XML; " + e);
			//e.printStackTrace();
		} catch(IllegalArgumentException e) {
			Main.log.log(Level.SEVERE, "Illegal parameter(s) in XML; " + e);
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
	 * Sets the feedrate in mm/min. (Fxxx)
	 * @param  node The node with the feedrate parameter [feedrate]
	 */
	private void setFeedRate(Node node) throws IllegalArgumentException {
		Tuple feedrate = new Tuple(node);

		programModel.addRow(new Row());
		programModel.addField(new Field('G', new BigDecimal(0)));
		programModel.addField(new Field('F', feedrate.getValue(0)));
		programModel.setComment(new String("Set feedrate to " + feedrate.getValue(0)));
	}
	
	/**
	 * Sets the milling tools and write it to a List.
	 * @param node The node with the id, diameter and name
	 */
	private void setTools(Node node) throws IllegalArgumentException {
		if(!tools.isEmpty()) {
			throw new IllegalArgumentException("Toolset already defined.");
		}
		
		NodeList children = node.getChildNodes();
		
		String id = null;
		String type = null;
		double diameter = 0;
		NamedNodeMap map = null;
		
		for(int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			if(item.getNodeName() == "tool") {
				try {
					map = item.getAttributes();
					id = map.getNamedItem("id").getTextContent();
					if (tools.containsKey(id)) {
						throw new IllegalArgumentException("Tool with id " + id + " already exists.");
					}
				} catch (Exception e) {
					Main.log.log(Level.SEVERE, e.getMessage());
					return;
				}
				try {
					diameter = Double.parseDouble(map.getNamedItem("diameter").getTextContent());
					if(diameter < 0.05) {
						throw new IllegalArgumentException("Diameter must be greater or equal 0.05.");
					}
				} catch (Exception e) {
					Main.log.log(Level.SEVERE, e.getMessage());
					return;
				}
				try {
					type = map.getNamedItem("type").getTextContent();
				} catch (Exception e) {
					Main.log.log(Level.SEVERE, e.getMessage());
				}
				tools.put(id, new Tool(id, diameter, type));
			}
		}
	}
	
	/**
	 * Generates G-Code for a tool change, if the tool differs from previous and next element.
	 * @param newTool The new tool
	 */
	private void generateToolChange(Tool newTool) {
		if(currentTool == null) {
			currentTool = newTool;
		} else {
			if(newTool != currentTool) {
				currentTool = newTool;
				programModel.addRow(new Row(new Field('M', new BigDecimal(6)), "Tool change"));
			}
		}
	}
	
	/**
	 * Returns the current tool with specified id from the tool list.
	 * @param id The tool id
	 * @return The tool object
	 */
	public Tool getTool(String id) {
		return tools.get(id);
	}
	
	/**
	 * Adds a new point to the translation translation list with the x and y coordinates within the translation tag.
	 * The current translation coordinate is then the sum of all points in the list
	 * 	An code example snippet:
	 * <pre>{@code
	 * <translation x="10" y="10">
	 * 	 ...
	 *   <translation x="20" y="20">
	 * 	 ...
	 *   </translation>
	 *   ...
	 * </translation>
	 * }</pre>
	 * @param node
	 * @throws IllegalArgumentException
	 */
	private void setTranslation(Node node) throws IllegalArgumentException {
		NamedNodeMap map = node.getAttributes();
		double x = 0; 
		double y = 0;
		try {
			x = new BigDecimal(map.getNamedItem("x").getTextContent()).doubleValue();
		} catch(NullPointerException e) {
		} catch(NumberFormatException e) {
			Main.log.log(Level.SEVERE, "Illegal translation parameter(s); " + e);
		}	
		
		try {
			y = new BigDecimal(map.getNamedItem("y").getTextContent()).doubleValue();
		} catch(NullPointerException e) {
		} catch(NumberFormatException e) {
			Main.log.log(Level.SEVERE, "Illegal translation parameter(s); " + e);
		}

		translation.add(new Point2D.Double(x, y));
	}	

	/**
	 * This method creates the G-Code for the toolpath.
	 * 
	 * @param toolPath The toolpath
	 * @param zLevel The milling depth (z-axis)
	 */
	private void createGCode(ArrayList<ToolPath> toolPathes, Tuple zLevel) {
		BigDecimal endZ = zLevel.getValue(1);
		BigDecimal stepZ = zLevel.getValue(2);
		
				
		if(stepZ.doubleValue() <= 0) {
			throw new IllegalArgumentException("The Z step must be greater than 0");
		}
		
		// i is number of the toolpath
		for(int i = 0; i < toolPathes.size(); i++) {
			ToolPath toolPath = toolPathes.get(i);
			newX = toolPath.getX(0);
			newY = toolPath.getY(0);
			newZ = zLevel.getValue(0);
			boolean forward = true;
			
			go0(newX, newY, "Go to start position for element " + toolPath.getName()); // go to start position
				
			while(true) {
				if(forward) {
					go1(newX, newY, newZ);  // Z sink
					for(int j = 1; j < toolPath.size(); j++) {
						newX = toolPath.getX(j);
						newY = toolPath.getY(j);
						go1(newX, newY, newZ, toolPath.getName());  // X-Y move
					}
					forward = false;
				} else {
					go1(newX, newY, newZ);  // Z sink
					for(int j = toolPath.size() - 2; j >= 0; j--) {
						newX = toolPath.getX(j);
						newY = toolPath.getY(j);
						go1(newX, newY, newZ, toolPath.getName());  // X-Y move
					}
					forward = true;
				}
				// If last zLevel was cutted, break
				if(newZ.doubleValue() == endZ.doubleValue()) {
					break;
				}
				
				newZ = newZ.subtract(stepZ);
				
				// If last zLevel is < endLevel cut the last zLevel with endZ value
				if(newZ.doubleValue() < endZ.doubleValue()) {
					newZ = new BigDecimal(endZ.doubleValue());
				}
			}
			go0(currentX, currentY, "End element " + toolPath.getName() + " Lift up at current position.");
		}
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
		
		programModel.addRow(new Row());
		
		if(comment != null) {
			programModel.setComment(programModel.sizeRow() - 1, comment);
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
			
		// move z always to security high when G0 move shall performed
		if(!z.equals(currentZ)) {
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
	 * @param comment The comment
	 */
	private void go1(BigDecimal x, BigDecimal y, BigDecimal z, String comment) {
		go1(x, y, z, null, comment);
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
		programModel.addRow(new Row());
		
		if(comment != null) {
			programModel.setComment(programModel.sizeRow() - 1, comment);
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
		
		// remove line if there are no commands
		/*if(programModel.getLine(programModel.size() - 1).size() <= 1) {
			programModel.removeLine(programModel.size() - 1);
		}*/

	}
		
	/**
	 * Returns the current translation value, which is the sum of al translation points. 
	 * @return The current translation value;
	 */
	public Point2D.Double getTranslation() {
		int x = 0;
		int y = 0;
		for(int i = 0; i < translation.size(); i++) {
			x += translation.get(i).getX();
			y += translation.get(i).getY();
		}
		return new Point2D.Double(x, y);
	}

}
